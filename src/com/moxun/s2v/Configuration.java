package com.moxun.s2v;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColorChooser;
import com.moxun.s2v.utils.CommonUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by moxun on 16/10/10.
 */
public class Configuration implements Configurable {
    private JTextField svgDir;
    private JCheckBox autoCheckUpdateCheckBox;
    private JButton select;
    private JPanel root;
    private JCheckBox overrideExisted;
    private JTextField prefix;
    private JButton pickColor;
    private JLabel color;
    private JCheckBox useSystemTools;

    private static final String KEY_DEFAULT_DIR = "s2v_default_dir";
    private static final String KEY_AUTO_CHECK_UPDATE = "s2v_auto_check_update";
    private static final String KEY_DELETE_EXISTED_XML = "s2v_delete_existed_xml";
    private static final String KEY_PREFIX = "s2v_vector_prefix";
    private static final String KEY_DEFAULT_TINT = "s2v_default_tint";
    private static final String KEY_USE_SYSTEM_TOOLS = "s2v_use_sys_tools";

    @Override
    public String getDisplayName() {
        return "SVG to VectorDrawable";
    }

    @Override
    public String getHelpTopic() {
        return "";
    }

    @Override
    public JComponent createComponent() {
        color.setOpaque(true);
        color.setBorder(new EmptyBorder(10, 10, 10, 10));
        select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
                VirtualFile virtualFile = FileChooser.chooseFile(descriptor, ProjectUtil.guessCurrentProject(select), null);
                if (virtualFile != null && virtualFile.isDirectory()) {
                    svgDir.setText(virtualFile.getCanonicalPath());
                }
            }
        });
        svgDir.setText(getSvgDir());
        autoCheckUpdateCheckBox.setSelected(isAutoCheckUpdate());
        overrideExisted.setSelected(isOverrideExisted());
        prefix.setText(getPrefix());
        int rgb = CommonUtil.parseColor(getDefaultTint());
        final Color saved = new Color(rgb);
        color.setBackground(saved);
        ensureTextColor(saved);
        color.setText(getDefaultTint());
        pickColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = ColorChooser.chooseColor(root.getParent(), "", saved);
                color.setBackground(c);
                color.setText("#" + Integer.toHexString(c.getRGB()).toUpperCase());
                ensureTextColor(c);
            }
        });
        return root;
    }

    private void ensureTextColor(Color c) {
        int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
        Color reverse = new Color(255 - r, 255 - g, 255 - b);
        color.setForeground(reverse);
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance().setValue(KEY_DEFAULT_DIR, svgDir.getText());
        PropertiesComponent.getInstance().setValue(KEY_AUTO_CHECK_UPDATE, String.valueOf(autoCheckUpdateCheckBox.isSelected()));
        PropertiesComponent.getInstance().setValue(KEY_DELETE_EXISTED_XML, String.valueOf(overrideExisted.isSelected()));
        PropertiesComponent.getInstance().setValue(KEY_USE_SYSTEM_TOOLS, String.valueOf(useSystemTools.isSelected()));
        PropertiesComponent.getInstance().setValue(KEY_PREFIX, prefix.getText());
        PropertiesComponent.getInstance().setValue(KEY_DEFAULT_TINT, color.getText());
    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }

    public static String getSvgDir() {
        return PropertiesComponent.getInstance().getValue(KEY_DEFAULT_DIR, "");
    }

    public static boolean isAutoCheckUpdate() {
        return PropertiesComponent.getInstance().isTrueValue(KEY_AUTO_CHECK_UPDATE);
    }

    public static boolean isOverrideExisted() {
        return PropertiesComponent.getInstance().isTrueValue(KEY_DELETE_EXISTED_XML);
    }

    public static String getPrefix() {
        return PropertiesComponent.getInstance().getValue(KEY_PREFIX, "vector_drawable_");
    }

    public static String getDefaultTint() {
        return PropertiesComponent.getInstance().getValue(KEY_DEFAULT_TINT, "#FF000000");
    }

    public static boolean useSystemTools() {
        return PropertiesComponent.getInstance().getBoolean(KEY_USE_SYSTEM_TOOLS, true);
    }
}
