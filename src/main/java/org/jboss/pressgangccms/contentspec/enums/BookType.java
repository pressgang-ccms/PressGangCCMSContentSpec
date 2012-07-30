package org.jboss.pressgangccms.contentspec.enums;

import org.jboss.pressgangccms.contentspec.constants.CSConstants;

public enum BookType
{
	BOOK(CSConstants.BOOK_TYPE_BOOK), ARTICLE(CSConstants.BOOK_TYPE_ARTICLE);
	
	private final String title;
	
	BookType(final String title)
	{
		this.title = title;
	}
	
	public static BookType getBookType(final String bookType)
	{
		if (bookType.equalsIgnoreCase(CSConstants.BOOK_TYPE_BOOK))
		{
			return BOOK;
		}
		else if (bookType.equalsIgnoreCase(CSConstants.BOOK_TYPE_ARTICLE))
		{
			return ARTICLE;
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public String toString()
	{
		return this.title;
	}
}
