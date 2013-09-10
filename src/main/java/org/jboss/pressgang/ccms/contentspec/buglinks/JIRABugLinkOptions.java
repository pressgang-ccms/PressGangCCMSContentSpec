package org.jboss.pressgang.ccms.contentspec.buglinks;

public class JIRABugLinkOptions extends BugLinkOptions {
    private String project;
    private String component;
    private String labels;
    private String version;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
