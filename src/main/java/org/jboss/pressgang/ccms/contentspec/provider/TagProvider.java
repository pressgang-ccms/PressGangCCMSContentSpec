package org.jboss.pressgang.ccms.contentspec.provider;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.CategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;

public interface TagProvider {
    TagWrapper getTag(final int id);
    List<TagWrapper> getTagsByName(final String name);
    List<CategoryWrapper> getTagCategories(final int id);
}
