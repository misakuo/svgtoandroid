package com.moxun.s2v;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.ui.JBColor;
import com.moxun.s2v.message.ErrorMessage;
import com.moxun.s2v.utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by moxun on 15/12/14.
 */
public class GUI {
    private JPanel rootPanel;
    private JComboBox dpiChooser;
    private JTextField svgPath;
    private JButton svgSelectBtn;
    private JComboBox moduleChooser;
    private JButton generateButton;
    private JTextField xmlName;
    private JCheckBox batch;
    private JFrame frame;

    private Project project;
    private Set<String> distDirList = new HashSet<String>();
    private ModulesUtil modulesUtil;
    private XmlFile svg;
    private PsiDirectory svgDir;

    public GUI(Project project) {
        this.project = project;
        frame = new JFrame("SVG to VectorDrawable (" + CommonUtil.loadMetaInf("version", "") + ")");
        modulesUtil = new ModulesUtil(project);
        distDirList.clear();
        svgPath.setFocusable(false);
        setListener();
        initModules();
    }

    private void initModules() {
        for (String item : modulesUtil.getModules()) {
            moduleChooser.addItem(item);
        }
    }

    private void setListener() {
        batch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                svgPath.setText("");
                xmlName.setText("");
            }
        });

        svgPath.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                svgPath.setBackground(JBColor.YELLOW);
            }

            @Override
            public void focusLost(FocusEvent e) {
                svgPath.setBackground(JBColor.WHITE);
            }
        });

        xmlName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                xmlName.setBackground(JBColor.YELLOW);
            }

            @Override
            public void focusLost(FocusEvent e) {
                xmlName.setBackground(JBColor.WHITE);
            }
        });

        svgSelectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSVGChooser();
                check();
            }
        });

        moduleChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dpiChooser.setRenderer(new MyCellRender(modulesUtil.getExistDpiDirs(moduleChooser.getSelectedItem().toString())));
            }
        });

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String moduleName = (String) moduleChooser.getSelectedItem();
                if (moduleName != null) {
                    PsiDirectory resDir = modulesUtil.getResDir(moduleName);
                    if (resDir != null) {
                        Logger.debug("Got res dir " + resDir.getVirtualFile().getPath());
                        Logger.debug("Existing drawable dirs " + modulesUtil.getDrawableDirs(resDir));
                    }
                }
                if (modulesUtil.isAndroidProject()) {
                    if (check() && !batch.isSelected()) {
                        final Transformer transformer = new Transformer.Builder()
                                .setProject(project)
                                .setSVG(svg)
                                .setDpi((String) dpiChooser.getSelectedItem())
                                .setModule(moduleName)
                                .setXmlName(xmlName.getText())
                                .create();

                        transformer.transforming(new Transformer.CallBack() {
                            @Override
                            public void onComplete(XmlFile dist) {
                                transformer.writeXmlToDir(dist, true);
                                CommonUtil.showTopic(project,
                                        "SVG to VectorDrawable",
                                        "Generating completed.<br>" + dist.getName(),
                                        NotificationType.INFORMATION);
                            }
                        });
                    } else if (check() && batch.isSelected()) {
                        final java.util.List<String> files = new ArrayList<String>();
                        for (PsiFile svg : svgDir.getFiles()) {
                            if (svg != null && !svg.isDirectory() && svg.getName().toLowerCase().endsWith(".svg")) {
                                final Transformer transformer = new Transformer.Builder()
                                        .setProject(project)
                                        .setSVG((XmlFile) svg)
                                        .setDpi((String) dpiChooser.getSelectedItem())
                                        .setModule(moduleName)
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
                                "Generating completed." + msg,
                                NotificationType.INFORMATION);
                    }
                    frame.dispose();
                } else {
                    ErrorMessage.show(project, "Current project is not an Android project!");
                    frame.dispose();
                }
            }
        });
    }

    private void showSVGChooser() {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        if (!batch.isSelected()) {
            descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("svg");
        }
        VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (virtualFile != null) {
            if (!virtualFile.isDirectory() && virtualFile.getName().toLowerCase().endsWith("svg")) {
                svg = (XmlFile) PsiManager.getInstance(project).findFile(virtualFile);
                //got *.svg file as xml
                svgPath.setText(virtualFile.getPath());
                xmlName.setEditable(true);
                xmlName.setEnabled(true);
                xmlName.setText(CommonUtil.getValidName(svg.getName().split("\\.")[0]) + ".xml");
            } else if (virtualFile.isDirectory()) {
                svgDir = PsiManager.getInstance(project).findDirectory(virtualFile);
                svgPath.setText(virtualFile.getPath());
                xmlName.setEditable(false);
                xmlName.setEnabled(false);
                xmlName.setText("keep origin name");
            }
        }
        frame.setAlwaysOnTop(true);
    }

    public void show() {
        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(frame.getParent());
        frame.setVisible(true);

        UpdateUtil.checkUpdate(project);
    }

    private boolean check() {
        boolean pass = false;
        if (svgPath.getText().isEmpty()) {
            svgPath.setBackground(new Color(0xff, 0xae, 0xb9));
            pass = false;
        } else {
            svgPath.setBackground(Color.WHITE);
            pass = true;
        }

        if (xmlName.getText().isEmpty()) {
            xmlName.setBackground(new Color(0xff, 0xae, 0xb9));
            pass = false;
        } else {
            xmlName.setBackground(Color.WHITE);
            pass = true;
        }
        return pass;
    }
}
