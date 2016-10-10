package com.moxun.s2v;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
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

    private static final String KEY_DEFAULT_DIR = "s2v_default_dir";
    private static final String KEY_AUTO_CHECK_UPDATE = "s2v_auto_check_update";
    private static final String KEY_DELETE_EXISTED_XML = "s2v_delete_existed_xml";
    private static final String KEY_PREFIX = "s2v_vector_prefix";

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
        return root;
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
        PropertiesComponent.getInstance().setValue(KEY_PREFIX, prefix.getText());
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
}
