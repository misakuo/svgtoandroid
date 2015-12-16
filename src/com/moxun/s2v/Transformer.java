package com.moxun.s2v;

import com.intellij.codeStyle.CodeStyleFacade;
import com.intellij.icons.AllIcons;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.moxun.s2v.message.InfoMessage;
import com.moxun.s2v.utils.*;
import org.apache.xerces.impl.dtd.XMLAttributeDecl;

import java.util.*;

/**
 * Created by moxun on 15/12/14.
 */
public class Transformer {
    private Project project;
    private XmlFile svg;
    private String moduleName;
    private String dpi;
    private ModulesUtil modulesUtil;
    private String xmlName;
    private PsiDirectory distDir;
    private SVGParser svgParser;

    private Transformer() {

    }

    public void transforming() {
        svgParser = new SVGParser(svg, dpi);
        Logger.debug(svgParser.toString());

        XmlFile dist = getDistXml();
        XmlDocument document = dist.getDocument();
        if (document != null && document.getRootTag() != null) {
            XmlTag rootTag = document.getRootTag();

            //set attr to root tag
            try {
                rootTag.getAttribute("android:width").setValue(svgParser.getWidth());
                rootTag.getAttribute("android:height").setValue(svgParser.getHeight());
                rootTag.getAttribute("android:viewportWidth").setValue(svgParser.getViewportWidth());
                rootTag.getAttribute("android:viewportHeight").setValue(svgParser.getViewportHeight());

                if (svgParser.getAlpha().length() > 0) {
                    rootTag.setAttribute("android:alpha",svgParser.getAlpha());
                }
            } catch (NullPointerException npe) {
                //do nothing, because those attr is exist certainly.
            }

            //generate group
            if (svgParser.hasGroupTag()) {
                for (XmlTag g : svgParser.getGroups()) {
                    XmlTag group = rootTag.createChildTag("group", rootTag.getNamespace(), null, false);
                    //set group's attrs
                    Map<String, String> svgGroupAttrs = svgParser.getChildAttrs(g);
                    for (String key : svgGroupAttrs.keySet()) {
                        if (AttrMapper.getAttrName(key) != null) {
                            group.setAttribute(AttrMapper.getAttrName(key), svgGroupAttrs.get(key));
                        }
                    }

                    //add child tags
                    parseShapeNode(g,group);
                    rootTag.addSubTag(group, false);
                }
            } else {
                Logger.warn("Root tag has no subTag named 'group'");
                parseShapeNode(svg.getRootTag(), rootTag);
            }
            CodeStyleManager.getInstance(project).reformat(dist);
            writeXmlToDir(dist);
            Logger.debug(dist.toString());
        }
    }

    private void parseShapeNode(XmlTag srcTag,XmlTag distTag) {
        List<XmlTag> childes = svgParser.getShapeTags(srcTag);
        for (XmlTag child : childes) {
            XmlTag element = distTag.createChildTag("path", distTag.getNamespace(), null, false);
            Map<String, String> childAttrs = svgParser.getChildAttrs(child);
            for (String key : childAttrs.keySet()) {
                if (AttrMapper.getAttrName(key) != null && AttrMapper.getAttrName(key).contains("Color")) {
                    element.setAttribute(AttrMapper.getAttrName(key), StdColorUtil.formatColor(childAttrs.get(key)));
                } else if (AttrMapper.getAttrName(key) != null) {
                    element.setAttribute(AttrMapper.getAttrName(key), childAttrs.get(key));
                }

                if (AttrMapper.isShapeName(child.getName())) {
                    element.setAttribute("android:pathData",SVGAttrParser.getPathData(child));
                }
            }

            if (!childAttrs.keySet().contains("fill")) {
                element.setAttribute("android:fillColor", "#000000");
            }

            distTag.addSubTag(element, false);
        }
    }

    private XmlFile getDistXml() {
        String template = FileTemplateManager.getInstance(project).findInternalTemplate("vector").getText();
        return (XmlFile) PsiFileFactory.getInstance(project).createFileFromText(xmlName, StdFileTypes.XML, template);
    }

    private void writeXmlToDir(final XmlFile file) {
        try {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    new WriteCommandAction(project) {
                        @Override
                        protected void run(Result result) throws Throwable {
                            PsiDirectory directory = modulesUtil.getOrCreateDrawableDir(moduleName, getDrawableDirName());
                            PsiFile psiFile = directory.findFile(xmlName);
                            if (psiFile != null) {
                                Logger.debug(xmlName + "is existed, delete it.");
                                psiFile.delete();
                            }
                            directory.add(file);

                            Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName(project, xmlName, GlobalSearchScope.allScope(project));
                            for (VirtualFile vfile : virtualFiles) {
                                if (dpi.equals("nodpi") && vfile.getPath().contains("/drawable/")) {
                                    Logger.debug("Open file in editor: " + vfile.getPath());
                                    FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, vfile), true);
                                    break;
                                } else if (vfile.getPath().contains(dpi)) {
                                    Logger.debug("Open file in editor: " + vfile.getPath());
                                    FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, vfile), true);
                                    break;
                                }
                            }
                            InfoMessage.show(project,"Generating succeeded!");
                        }
                    }.execute();
                }
            });
        } catch (Exception e) {
            Logger.warn("error with async writing.");
        }
    }

    private String getDrawableDirName() {
        if (dpi.equals("nodpi")) {
            return "drawable";
        } else {
            return "drawable-" + dpi;
        }
    }


    public static class Builder {
        private Transformer transformer = new Transformer();

        public Builder setProject(Project project) {
            transformer.project = project;
            transformer.modulesUtil = new ModulesUtil(project);
            return this;
        }

        public Builder setSVG(XmlFile svg) {
            transformer.svg = svg;
            return this;
        }

        public Builder setModule(String moduleName) {
            transformer.moduleName = moduleName;
            return this;
        }

        public Builder setDpi(String dpi) {
            transformer.dpi = dpi;
            return this;
        }

        public Builder setXmlName(String name) {
            transformer.xmlName = name;
            return this;
        }

        public Transformer create() {
            return transformer;
        }
    }
}
