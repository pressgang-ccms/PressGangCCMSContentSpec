/*
  Copyright 2011-2014 Red Hat, Inc

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec.enums;

import org.jboss.pressgang.ccms.utils.constants.CommonConstants;

public enum RelationshipType {
    NONE, REFER_TO, PREREQUISITE, NEXT, PREVIOUS, LINKLIST;

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
