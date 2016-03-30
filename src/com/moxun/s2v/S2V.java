package com.moxun.s2v;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.moxun.s2v.utils.Logger;

/**
 * Created by moxun on 15/12/14.
 */
public class S2V extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Logger.init("SVG2VectorDrawable", Logger.INFO);
        GUI gui = new GUI(anActionEvent.getProject());
        gui.show();
    }
}
