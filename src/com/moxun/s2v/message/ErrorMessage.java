package com.moxun.s2v.message;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

/**
 * Created by moxun on 15/12/15.
 */
public class ErrorMessage {
    public static void show(Project project,String txt) {
        Messages.showErrorDialog(project,txt,"ERROR");
    }
}
