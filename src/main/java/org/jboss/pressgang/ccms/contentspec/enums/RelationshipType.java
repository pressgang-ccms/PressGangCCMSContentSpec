package org.jboss.pressgang.ccms.contentspec.enums;

import org.jboss.pressgang.ccms.utils.constants.CommonConstants;

public enum RelationshipType {
    NONE, REFER_TO, PREREQUISITE, NEXT, PREVIOUS, TARGET, EXTERNAL_TARGET, EXTERNAL_CONTENT_SPEC, LINKLIST;

    public static RelationshipType getRelationshipType(Integer id) {
        switch (id) {
            case CommonConstants.CS_RELATIONSHIP_PREREQUISITE:
                return PREREQUISITE;
            case CommonConstants.CS_RELATIONSHIP_REFER_TO:
                return REFER_TO;
            case CommonConstants.CS_RELATIONSHIP_LINK_LIST:
                return LINKLIST;
            case CommonConstants.CS_RELATIONSHIP_NEXT:
                return NEXT;
            case CommonConstants.CS_RELATIONSHIP_PREVIOUS:
                return PREVIOUS;
            default:
                return null;
        }
    }

    public static Integer getRelationshipTypeId(RelationshipType relationshipType) {
        switch (relationshipType) {
            case PREREQUISITE:
                return CommonConstants.CS_RELATIONSHIP_PREREQUISITE;
            case REFER_TO:
                return CommonConstants.CS_RELATIONSHIP_REFER_TO;
            case LINKLIST:
                return CommonConstants.CS_RELATIONSHIP_LINK_LIST;
            case NEXT:
                return CommonConstants.CS_RELATIONSHIP_NEXT;
            case PREVIOUS:
                return CommonConstants.CS_RELATIONSHIP_PREVIOUS;
            default:
                return null;
        }
    }
}
