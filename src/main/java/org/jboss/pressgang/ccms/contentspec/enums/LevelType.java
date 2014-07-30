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

import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;


/**
 * A Enumerator to describe the levels types available for a level. It also contains the ability to get an Unique ID and title for the
 * LevelType's.
 *
 * @author lnewson
 */
public enum LevelType {
    BASE(CSConstants.LEVEL_BASE), CHAPTER(CommonConstants.CS_NODE_CHAPTER), APPENDIX(CommonConstants.CS_NODE_APPENDIX),
    PROCESS(CommonConstants.CS_NODE_PROCESS), SECTION(CommonConstants.CS_NODE_SECTION), PART(CommonConstants.CS_NODE_PART),
    PREFACE(CommonConstants.CS_NODE_PREFACE), INITIAL_CONTENT(CommonConstants.CS_NODE_INITIAL_CONTENT);

    private final int type;

    LevelType(final int id) {
        type = id;
    }

    /**
     * Get the unique ID for the Enumerator.
     *
     * @return The unique ID.
     */
    public Integer getId() {
        return type;
    }

    /**
     * Get the title for the Enumerator.
     *
     * @return The title for the Enumerator.
     */
    public String getTitle() {
        switch (getId()) {
            case CommonConstants.CS_NODE_CHAPTER:
                return CSConstants.CHAPTER;
            case CommonConstants.CS_NODE_SECTION:
                return CSConstants.SECTION;
            case CommonConstants.CS_NODE_APPENDIX:
                return CSConstants.APPENDIX;
            case CommonConstants.CS_NODE_PROCESS:
                return CSConstants.PROCESS;
            case CommonConstants.CS_NODE_PART:
                return CSConstants.PART;
            case CommonConstants.CS_NODE_PREFACE:
                return CSConstants.PREFACE;
            case CommonConstants.CS_NODE_INITIAL_CONTENT:
                return CSConstants.LEVEL_INITIAL_CONTENT;
            case CSConstants.LEVEL_BASE:
                return "Content Specification";
            default:
                return null;
        }
    }

    public static LevelType getLevelType(String name) {
        if (name == null) return null;

        if (name.equalsIgnoreCase(CSConstants.CHAPTER)) {
            return LevelType.CHAPTER;
        } else if (name.equalsIgnoreCase(CSConstants.SECTION)) {
            return LevelType.SECTION;
        } else if (name.equalsIgnoreCase(CSConstants.APPENDIX)) {
            return LevelType.APPENDIX;
        } else if (name.equalsIgnoreCase(CSConstants.PROCESS)) {
            return LevelType.PROCESS;
        } else if (name.equalsIgnoreCase(CSConstants.PART)) {
            return LevelType.PART;
        } else if (name.equalsIgnoreCase(CSConstants.PREFACE)){
            return LevelType.PREFACE;
        } else if (name.equalsIgnoreCase(CSConstants.LEVEL_INITIAL_CONTENT)){
            return LevelType.INITIAL_CONTENT;
        }

        return null;
    }
}
