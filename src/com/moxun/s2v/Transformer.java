package com.moxun.s2v;

import com.android.ide.common.vectordrawable.Svg2Vector;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.moxun.s2v.utils.*;
import org.apache.http.util.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    private StyleParser styleParser;

    private Transformer() {

    }

    public void transforming(CallBack callBack) {
        if (Configuration.useSystemTools()) {
            Logger.info("use system tools to parse");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            File svgFile = VfsUtilCore.virtualToIoFile(svg.getVirtualFile());
            try {
                String errorCode = Svg2Vector.parseSvgToXml(svgFile, stream);
                if (TextUtils.isEmpty(errorCode)) {
                    String result = new String(stream.toByteArray()).replaceAll("\r", "");
                    XmlFile targetFile = (XmlFile) PsiFileFactory.getInstance(project).createFileFromText(xmlName, StdFileTypes.XML, result);
                    CodeStyleManager.getInstance(project).reformat(targetFile, true);
                    Logger.warn("parse complete [system]: " + targetFile.getName() + ", length: " + result.length());
                    callBack.onComplete(targetFile);
                } else {
                    Logger.error("parse failed: " + errorCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.error("parse exception: "+ e.toString());
            }
        } else {
            Logger.info("use builtin tools to parse");
            svgParser = new SVGParser(svg, dpi);
            styleParser = new StyleParser(svgParser.getStyles());

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
                CodeStyleManager.getInstance(project).reformat(dist, true);
                Logger.debug("parse complete [inner]: " + dist.getName());
                callBack.onComplete(dist);
            }
        }
    }

    private void parseGroup(XmlTag svgTag, XmlTag target) {
        XmlTag groupElement = target.createChildTag("group", target.getNamespace(), null, false);
        //set group's attrs
        Map<String, String> svgGroupAttrs = svgParser.getChildAttrs(svgTag);
        List<String> acceptedAttrs = Arrays.asList("id", "transform");

        for (String key : svgGroupAttrs.keySet()) {
            if (AttrMapper.getAttrName(key) != null && acceptedAttrs.contains(key)) {
                groupElement.setAttribute(AttrMapper.getAttrName(key), svgGroupAttrs.get(key));
            }
        }

        if (svgGroupAttrs.keySet().contains("transform")) {
            Map<String, String> trans = AttrMapper.getTranslateAttrs(svgGroupAttrs.get("transform"));
            for (String key : trans.keySet()) {
                groupElement.setAttribute(key, CommonUtil.formatString(trans.get(key)));
            }
        }

        //add child tags
        //<g> was processed.
        processSubGroups(svgTag, groupElement);

        svgGroupAttrs.remove("id");
        svgGroupAttrs.remove("transform");
        parseShapeNode(svgTag, groupElement, svgGroupAttrs);
        target.addSubTag(groupElement, false);
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

        List<XmlTag> useTags = svgParser.getUseTags(srcTag);
        Map<String, XmlTag> defsTags = svgParser.getAcceptedDefNodes();
        for (XmlTag useTag : useTags) {
            String href = useTag.getAttributeValue("xlink:href");
            XmlTag def = defsTags.get(href.replace("#", ""));
            if (def != null) {
                XmlTag clonedTag = (XmlTag) def.copy();
                for (XmlAttribute attribute : useTag.getAttributes()) {
                    if (!"xlink:href".equalsIgnoreCase(attribute.getName())) {
                        clonedTag.setAttribute(attribute.getName(), attribute.getValue());
                    }
                }
                childes.add(clonedTag);
            }
        }

        for (XmlTag child : childes) {
            XmlTag pathElement = distTag.createChildTag("path", distTag.getNamespace(), null, false);
            Map<String, String> myAttrs = new HashMap<String, String>();
            myAttrs.putAll(existedAttrs);
            myAttrs.putAll(svgParser.getChildAttrs(child));
            Logger.debug("Existed attrs: " + myAttrs);

            XmlAttribute id = child.getAttribute("class");
            String idValue = id == null ? null : id.getValue();
            if (idValue != null) {
                pathElement.setAttribute("android:name", idValue);
            }

            pathElement.setAttribute("android:fillColor", decideFillColor(srcTag, child));

            for (String svgElementAttribute : myAttrs.keySet()) {
                if (AttrMapper.getAttrName(svgElementAttribute) != null && AttrMapper.getAttrName(svgElementAttribute).contains("Color")) {
                    pathElement.setAttribute(AttrMapper.getAttrName(svgElementAttribute), StdColorUtil.formatColor(myAttrs.get(svgElementAttribute)));
                } else if (AttrMapper.getAttrName(svgElementAttribute) != null) {
                    if (AttrMapper.getAttrName(svgElementAttribute).equals("android:fillType")) {
                        String value = myAttrs.get(svgElementAttribute).toLowerCase();
                        String xmlValue = "nonZero";
                        if (value.equals("evenodd")) {
                            xmlValue = "evenOdd";
                        }
                        pathElement.setAttribute("android:fillType", xmlValue);
                    } else {
                        pathElement.setAttribute(AttrMapper.getAttrName(svgElementAttribute), myAttrs.get(svgElementAttribute));
                    }
                }
            }

            if (AttrMapper.isShapeName(child.getName())) {
                pathElement.setAttribute("android:pathData", SVGAttrParser.getPathData(child));
            }

            distTag.addSubTag(pathElement, false);
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

                            DocumentImpl document = (DocumentImpl) file.getViewProvider().getDocument();
                            if (document != null) {
                                document.setAcceptSlashR(true);
                            }

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

    public interface CallBack {

        void onComplete(XmlFile dist);
    }

    //Priority: style > self > parent
    private String decideFillColor(XmlTag group, XmlTag self) {
        String result = Configuration.getDefaultTint();

        XmlAttribute groupFill;
        if ((groupFill = group.getAttribute("fill")) != null) {
            result = StdColorUtil.formatColor(groupFill.getValue());
        }

        XmlAttribute selfFill;
        if ((selfFill = self.getAttribute("fill")) != null) {
            result = StdColorUtil.formatColor(selfFill.getValue());
        }

        XmlAttribute id = self.getAttribute("class");
        String colorFromStyle;
        if (id != null && id.getValue() != null && (colorFromStyle = styleParser.getFillColor(id.getValue())) != null) {
            result = colorFromStyle;
        }

        return result;
    }
}
