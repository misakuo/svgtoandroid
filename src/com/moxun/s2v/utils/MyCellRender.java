package com.moxun.s2v.utils;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Created by moxun on 15/12/16.
 */
public class MyCellRender extends JLabel implements ListCellRenderer {
    private Set<String> dpis;

    public MyCellRender(Set<String> dpis) {
        this.dpis = dpis;
    }
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        this.setText(value.toString());
        if (!dpis.contains(value.toString())) {
            this.setForeground(Color.GRAY);
        } else {
            this.setForeground(Color.BLACK);
        }
        return this;
    }
}
