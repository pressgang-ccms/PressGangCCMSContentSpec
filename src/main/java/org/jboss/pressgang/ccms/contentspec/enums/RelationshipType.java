package org.jboss.pressgang.ccms.contentspec.enums;

public enum RelationshipType {
    NONE, RELATED, PREREQUISITE, NEXT, PREVIOUS, TARGET, BRANCH, EXTERNAL_TARGET, EXTERNAL_CONTENT_SPEC, LINKLIST;

    public static RelationshipType getRelationshipType(Integer id) {
        switch (id) {
            case 0:
                return PREREQUISITE;
            case 1:
                return RELATED;
            case 2:
                return LINKLIST;
            case 3:
                return NEXT;
            case 4:
                return PREVIOUS;
            default:
                return null;
        }
    }
}
