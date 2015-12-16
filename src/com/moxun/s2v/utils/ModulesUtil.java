package com.moxun.s2v.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.file.PsiDirectoryFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by moxun on 15/12/15.
 */
public class ModulesUtil {
    private Project project;
    private boolean isAndroidProject = false;

    public ModulesUtil(Project project) {
        this.project = project;
    }

    public Set<String> getModules() {
        Set<String> modules = new HashSet<String>();
        PsiDirectory baseDir = PsiDirectoryFactory.getInstance(project).createDirectory(project.getBaseDir());
        if (isAndroidProject(baseDir)) {
            Logger.debug(project.getName() + " is an Android project");
            PsiDirectory[] dirs = baseDir.getSubdirectories();
            for (PsiDirectory dir : dirs) {
                if (!dir.getName().equals("build") && !dir.getName().equals("gradle")) {
                    if (isModule(dir)) {
                        Logger.debug(dir.getName() + " is a Module");
                        modules.add(dir.getName());
                    }
                }
            }
        }
        Logger.debug(modules.toString());
        return modules;
    }

    public PsiDirectory getResDir(String moduleName) {
        PsiDirectory baseDir = PsiDirectoryFactory.getInstance(project).createDirectory(project.getBaseDir());
        PsiDirectory moduleDir = baseDir.findSubdirectory(moduleName);
        if (moduleDir != null && moduleDir.isDirectory()) {
            PsiDirectory srcDir = moduleDir.findSubdirectory("src");
            if (srcDir != null && srcDir.isDirectory()) {
                PsiDirectory mainDir = srcDir.findSubdirectory("main");
                if (mainDir != null && mainDir.isDirectory()) {
                    PsiDirectory resDir = mainDir.findSubdirectory("res");
                    if (resDir != null && resDir.isDirectory()) {
                        return resDir;
                    }
                }
            }
        }
        return null;
    }

    public PsiDirectory getOrCreateDrawableDir(String moduleName,String dirName) {
        PsiDirectory baseDir = PsiDirectoryFactory.getInstance(project).createDirectory(project.getBaseDir());
        PsiDirectory moduleDir = baseDir.findSubdirectory(moduleName);
        if (moduleDir != null) {
            PsiDirectory srcDir = moduleDir.findSubdirectory("src");
            if (srcDir == null) {
                srcDir = moduleDir.createSubdirectory("src");
                Logger.debug("Creating dir :" + srcDir.getName());
            }

            PsiDirectory mainDir = srcDir.findSubdirectory("main");
            if (mainDir == null) {
                mainDir = srcDir.createSubdirectory("main");
                Logger.debug("Creating dir :" + mainDir.getName());
            }

            PsiDirectory resDir = mainDir.findSubdirectory("res");
            if (resDir == null) {
                resDir = mainDir.createSubdirectory("res");
                Logger.debug("Creating dir :" + resDir.getName());
            }

            PsiDirectory drawableDir = resDir.findSubdirectory(dirName);
            if (drawableDir == null) {
                drawableDir = resDir.createSubdirectory(dirName);
                Logger.debug("Creating dir :" + drawableDir.getName());
            }
            return drawableDir;
        }
        return null;
    }

    public Set<String> getDrawableDirs(PsiDirectory resDir) {
        Set<String> dirs = new HashSet<String>();
        if (resDir != null) {
            PsiDirectory[] subdirs = resDir.getSubdirectories();
            for (PsiDirectory dir : subdirs) {
                if (dir.getName().contains("drawable")) {
                    dirs.add(dir.getName());
                }
            }
        }
        return dirs;
    }

    public Set<String> getExistDpiDirs(String moduleName) {
        Set<String> dpis = new HashSet<String>();
        if (moduleName != null) {
            for (String s : getDrawableDirs(getResDir(moduleName))) {
                if (s.equals("drawable")) {
                    dpis.add("nodpi");
                } else {
                    dpis.add(s.split("-")[1]);
                }
            }
        }
        return dpis;
    }


    private boolean isAndroidProject(PsiDirectory directory) {
        PsiFile[] files = directory.getFiles();
        for (PsiFile file : files) {
            if (file.getName().equals("build.gradle")) {
                isAndroidProject = true;
                return true;
            }
        }
        isAndroidProject = false;
        return false;
    }

    public boolean isAndroidProject() {
        Logger.debug("Is Android project:" + isAndroidProject);
        return isAndroidProject;
    }

    private boolean isModule(PsiDirectory directory) {
        boolean hasGradle = false;
        boolean hasSrc = false;
        PsiFile[] files = directory.getFiles();
        PsiDirectory[] dirs = directory.getSubdirectories();
        for (PsiFile file : files) {
            if (file.getName().equals("build.gradle")) {
                hasGradle = true;
                break;
            }
        }

        for (PsiDirectory dir : dirs) {
            if (dir.getName().equals("src")) {
                hasSrc = true;
                break;
            }
        }
        return hasGradle && hasSrc;
    }
}
