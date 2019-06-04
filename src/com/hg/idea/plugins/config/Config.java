package com.hg.idea.plugins.config;

import com.intellij.openapi.vfs.VirtualFile;

public class Config {
    private VirtualFile projectSrc;

    public Config(VirtualFile projectSrc) {
        this.projectSrc = projectSrc;
    }

    public VirtualFile getProjectSrc() {
        return projectSrc;
    }

    public void setProjectSrc(VirtualFile projectSrc) {
        this.projectSrc = projectSrc;
    }
}
