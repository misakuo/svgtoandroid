package com.moxun.s2v;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.moxun.s2v.utils.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Kale
 * @date 2016/10/1
 *
 * 将svg批量转换为vd
 */
public class GenerateAction extends AnAction {

    private Project project;

    @Override
    public void actionPerformed(AnActionEvent e) {
        long start = System.currentTimeMillis();
        
        Logger.init(getClass().getName(), Logger.DEBUG);
        
        String path;
        try {
            path = getCurrentModulePath(e);
        } catch (FileNotFoundException e1) {
            showHint("Please make sure the current file is in android module", true, e);
            return;
        }

        Logger.debug(path);

        path = path.substring(0, path.indexOf("src") + 3);

        File src = new File(path);

        File drawable = new File(src.getAbsolutePath() + File.separator + "main"
                + File.separator + "res" + File.separator + "drawable");
        if (!drawable.exists()) {
            drawable.mkdirs();
        }

        // TODO: 2016/10/10 generate 
        

        long ms = System.currentTimeMillis() - start;
        String timeStr = ms + "ms";
        if (ms > 1000) {
            timeStr = ms / 1000 + "s " + ms % 1000 + "ms";
        }

        new GUI(project).show();

        showHint("Generate completed successfully in " + timeStr, false, e);
    }

    private void showHint(String msg, boolean isError, AnActionEvent e) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(DataKeys.PROJECT.getData(e.getDataContext()));
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("<html>" + msg + "</html>", isError ? MessageType.ERROR : MessageType.INFO, null)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.above);
    }

    @NotNull
    private String getCurrentModulePath(AnActionEvent e) throws FileNotFoundException {
        project = e.getProject();
        if (project == null) {
            throw new FileNotFoundException("project is null");
        }
        final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor(); // 得到当前的文件
        if (editor == null) {
            throw new FileNotFoundException("editor is null");
        }
        final VirtualFile vf = FileDocumentManager.getInstance().getFile(editor.getDocument());
        if (vf == null) {
            throw new FileNotFoundException("vf is null");
        }
        String path = vf.getCanonicalPath();
        if (path == null) {
            throw new FileNotFoundException("path is null");
        }
        return path;
    }

}