package com.moxun.s2v.message;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

/**
 * Created by moxun on 15/12/16.
 */
public class InfoMessage {
    public static void show(Project project,String txt) {
        Messages.showInfoMessage(project,txt,"Information");
    }
}
