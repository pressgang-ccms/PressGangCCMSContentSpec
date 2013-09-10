package org.jboss.pressgang.ccms.contentspec.enums;

public enum BugLinkType {
    NONE("OFF"), JIRA("Jira"), BUGZILLA("Bugzilla"), OTHER("Other");

    private final String title;

    public static BugLinkType getType(final String type) {
        if (type == null) {
            return null;
        } else if (type.equalsIgnoreCase(BUGZILLA.title) || type.equalsIgnoreCase("ON")) {
            return BUGZILLA;
        } else if (type.equalsIgnoreCase(JIRA.title)) {
            return JIRA;
        } else if (type.equalsIgnoreCase(OTHER.title)) {
            return OTHER;
        } else {
            return NONE;
        }
    }

    private BugLinkType(final String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
