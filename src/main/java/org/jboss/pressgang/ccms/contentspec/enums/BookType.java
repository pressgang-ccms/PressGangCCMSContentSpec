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
