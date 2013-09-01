package org.jboss.pressgang.ccms.contentspec;

import org.jboss.pressgang.ccms.contentspec.utils.ContentSpecUtilities;

public class File {
    private String title;
    private Integer id;
    private Integer revision;

    public File(final String title, final Integer id) {
        this.id = id;
        this.title = title;
    }

    public File(final Integer id) {
        this.id = id;
    }

    public String getText() {
        final StringBuilder output = new StringBuilder();
        if (title == null) {
            output.append(getIdAndRevisionText());
        } else {
            output.append(ContentSpecUtilities.escapeRelationshipTitle(title)).append(" ").append("[").append(getIdAndRevisionText()).append
                    ("]");
        }
        return output.toString();
    }

    protected String getIdAndRevisionText() {
        if (revision != null) {
            return id + ", rev: " + revision;
        } else {
            return id.toString();
        }
    }

    public String toString() {
        return getText() + "\n";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
