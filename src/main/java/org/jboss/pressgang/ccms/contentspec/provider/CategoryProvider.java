package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.CategoryWrapper;

public interface CategoryProvider {
    CategoryWrapper getCategory(final int id);
}
