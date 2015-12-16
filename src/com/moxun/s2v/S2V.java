package com.moxun.s2v;

import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiElement;
import com.jetbrains.cidr.lang.actions.newFile.OCNewFileActionBase;
import com.moxun.s2v.utils.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by moxun on 15/12/14.
 */
public class S2V extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Logger.init("SVG2VectorDrawable",Logger.DEBUG);
        GUI gui = new GUI(anActionEvent.getProject());
        gui.show();
    }
}
