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

public enum BookType {
    BOOK(CSConstants.BOOK_TYPE_BOOK), ARTICLE(CSConstants.BOOK_TYPE_ARTICLE), BOOK_DRAFT(CSConstants.BOOK_TYPE_BOOK_DRAFT), ARTICLE_DRAFT(
            CSConstants.BOOK_TYPE_ARTICLE_DRAFT), INVALID("");

    private final String title;

    BookType(final String title) {
        this.title = title;
    }

    public static BookType getBookType(final String bookType) {
        if (bookType.equalsIgnoreCase(CSConstants.BOOK_TYPE_BOOK)) {
            return BOOK;
        } else if (bookType.equalsIgnoreCase(CSConstants.BOOK_TYPE_ARTICLE)) {
            return ARTICLE;
        } else if (bookType.equalsIgnoreCase(CSConstants.BOOK_TYPE_BOOK_DRAFT)) {
            return BOOK_DRAFT;
        } else if (bookType.equalsIgnoreCase(CSConstants.BOOK_TYPE_ARTICLE_DRAFT)) {
            return ARTICLE_DRAFT;
        } else {
            return INVALID;
        }
    }

    public static Integer getBookTypeId(final BookType bookType) {
        switch (bookType) {
            case BOOK:
                return CommonConstants.CS_BOOK;
            case BOOK_DRAFT:
                return CommonConstants.CS_BOOK_DRAFT;
            case ARTICLE:
                return CommonConstants.CS_ARTICLE;
            case ARTICLE_DRAFT:
                return CommonConstants.CS_ARTICLE_DRAFT;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return title;
    }
}
