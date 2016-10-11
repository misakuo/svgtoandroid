package com.moxun.s2v;

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
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.moxun.s2v.message.InfoMessage;
import com.moxun.s2v.utils.*;

import java.util.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    public void transforming(CallBack callBack) {
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
                    rootTag.setAttribute("android:alpha", svgParser.getAlpha());
                }
            } catch (NullPointerException npe) {
                //do nothing, because those attr is exist certainly.
            }

            //generate group
            if (svgParser.hasGroupTag()) {
                for (XmlTag g : svgParser.getGroups()) {
                    parseGroup(g, rootTag);
                }
            } else {
                Logger.warn("Root tag has no subTag named 'group'");
                parseShapeNode(svg.getRootTag(), rootTag, null);
            }
            CodeStyleManager.getInstance(project).reformat(dist);
            callBack.onComplete(dist);
            Logger.debug(dist.toString());
        }
    }

    private void parseGroup(XmlTag svgTag, XmlTag target) {
        XmlTag group = target.createChildTag("group", target.getNamespace(), null, false);
        //set group's attrs
        Map<String, String> svgGroupAttrs = svgParser.getChildAttrs(svgTag);
        List<String> acceptedAttrs = Arrays.asList("id", "transform");

        for (String key : svgGroupAttrs.keySet()) {
            if (AttrMapper.getAttrName(key) != null && acceptedAttrs.contains(key)) {
                group.setAttribute(AttrMapper.getAttrName(key), svgGroupAttrs.get(key));
            }
        }

        if (svgGroupAttrs.keySet().contains("transform")) {
            Map<String, String> trans = AttrMapper.getTranslateAttrs(svgGroupAttrs.get("transform"));
            for (String key : trans.keySet()) {
                group.setAttribute(key, CommonUtil.formatString(trans.get(key)));
            }
        }

        //add child tags
        //<g> was processed.
        processSubGroups(svgTag, group);

        svgGroupAttrs.remove("id");
        svgGroupAttrs.remove("transform");
        parseShapeNode(svgTag, group, svgGroupAttrs);
        target.addSubTag(group, false);
    }

    private void processSubGroups(XmlTag svgTag, XmlTag parent) {
        Map<String, String> trans = AttrMapper.getTranslateAttrs(svgTag.getAttributeValue("transform"));
        Logger.debug("Translate for sub groups:" + trans.toString());
        XmlTag merged = AttrMergeUtil.mergeAttrs((XmlTag) svgTag.copy(), trans);
        for (XmlTag tag : svgParser.getSubGroups(merged)) {
            parseGroup(tag, parent);
        }
    }

    private void parseShapeNode(XmlTag srcTag, XmlTag distTag, Map<String, String> existedAttrs) {
        if (existedAttrs == null) {
            existedAttrs = new HashMap<String, String>();
        }
        List<XmlTag> childes = svgParser.getShapeTags(srcTag);
        for (XmlTag child : childes) {
            XmlTag element = distTag.createChildTag("path", distTag.getNamespace(), null, false);
            existedAttrs.putAll(svgParser.getChildAttrs(child));
            Logger.debug("Existed attrs: " + existedAttrs);

            for (String key : existedAttrs.keySet()) {
                if (AttrMapper.getAttrName(key) != null && AttrMapper.getAttrName(key).contains("Color")) {
                    element.setAttribute(AttrMapper.getAttrName(key), StdColorUtil.formatColor(existedAttrs.get(key)));
                } else if (AttrMapper.getAttrName(key) != null) {
                    if (AttrMapper.getAttrName(key).equals("android:fillType")) {
                        String value = existedAttrs.get(key).toLowerCase();
                        String xmlValue = "nonZero";
                        if (value.equals("evenodd")) {
                            xmlValue = "evenOdd";
                        }
                        element.setAttribute("android:fillType", xmlValue);
                    } else {
                        element.setAttribute(AttrMapper.getAttrName(key), existedAttrs.get(key));
                    }
                }

                if (AttrMapper.isShapeName(child.getName())) {
                    element.setAttribute("android:pathData", SVGAttrParser.getPathData(child));
                }
            }

            if (element.getAttribute("android:fillColor") == null) {
                if (srcTag.getAttribute("fill") != null) {
                    element.setAttribute("android:fillColor", StdColorUtil.formatColor(srcTag.getAttribute("fill").getValue()));
                } else {
                    element.setAttribute("android:fillColor", Configuration.getDefaultTint());
                }
            }

            distTag.addSubTag(element, false);
        }
    }

    private XmlFile getDistXml() {
        String template = FileTemplateManager.getInstance(project).getInternalTemplate("vector").getText();
        return (XmlFile) PsiFileFactory.getInstance(project).createFileFromText(xmlName, StdFileTypes.XML, template);
    }

    public void writeXmlToDir(final XmlFile file, final boolean needOpen) {
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
                                if (Configuration.isOverrideExisted()) {
                                    Logger.warn(xmlName + " is existed, delete it.");
                                    psiFile.delete();
                                    directory.add(file);
                                    Logger.info("Generating " + file.getName() + " success!");
                                } else {
                                    Logger.warn(xmlName + " is existed, skip it.");
                                }
                            } else {
                                directory.add(file);
                                Logger.info("Generating " + file.getName() + " success!");
                            }

                            if (needOpen) {
                                Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName(project, xmlName, GlobalSearchScope.allScope(project));
                                for (VirtualFile vfile : virtualFiles) {
                                    if (dpi.equals("nodpi") && vfile.getPath().contains("/drawable/")) {
                                        Logger.info("Open file in editor: " + vfile.getPath());
                                        FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, vfile), true);
                                        break;
                                    } else if (vfile.getPath().contains(dpi)) {
                                        Logger.info("Open file in editor: " + vfile.getPath());
                                        FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, vfile), true);
                                        break;
                                    }
                                }
                            }
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
    
    public interface CallBack{

        void onComplete(XmlFile dist);
    }
}
