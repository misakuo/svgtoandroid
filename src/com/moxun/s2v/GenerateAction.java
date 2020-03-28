package com.moxun.s2v;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.xml.XmlFile;
import com.moxun.s2v.utils.CommonUtil;
import com.moxun.s2v.utils.Logger;
import com.moxun.s2v.utils.ModulesUtil;
import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kale
 * @date 2016/10/1
 *
 * 将svg批量转换为vd
 */
public class GenerateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        
        Logger.init("SVG2VectorDrawable", Logger.INFO);
        ModulesUtil util = new ModulesUtil(project);
        if (!util.isAndroidProject()) {
            CommonUtil.showTopic(
                    project,
                    "SVG to VectorDrawable",
                    "Please make sure the current file is in android module",
                    NotificationType.ERROR);
            return;
        }

        String sourceDir = Configuration.getSvgDir();

        if (TextUtils.isEmpty(sourceDir)) {
            CommonUtil.showTopic(
                    project,
                    "SVG to VectorDrawable",
                    "SVG source directory not set, please setting it first",
                    NotificationType.ERROR);
            return;
        }

        String currentModule = util.getCurrentModule();
        VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByPath(sourceDir);
        PsiDirectory svgDir = PsiDirectoryFactory.getInstance(project).createDirectory(vf);

        final List<String> files = new ArrayList<String>();
        for (PsiFile svg : svgDir.getFiles()) {
            if (svg != null && !svg.isDirectory() && svg.getName().toLowerCase().endsWith(".svg")) {
                final Transformer transformer = new Transformer.Builder()
                        .setProject(project)
                        .setSVG((XmlFile) svg)
                        .setDpi("nodpi")
                        .setModule(currentModule)
                        .setXmlName(CommonUtil.getValidName(svg.getName().split("\\.")[0]) + ".xml")
                        .create();

                Transformer.CallBack callBack = new Transformer.CallBack() {
                    @Override
                    public void onComplete(XmlFile dist) {
                        transformer.writeXmlToDir(dist, false);
                        files.add(dist.getName());
                    }
                };
                transformer.transforming(callBack);
            }
        }

        String msg = "";
        for (String s : files) {
            msg = msg + s + "<br>";
        }

        CommonUtil.showTopic(project,
                "SVG to VectorDrawable",
                "Generating completed.<br>" + msg,
                NotificationType.INFORMATION);
    }
}