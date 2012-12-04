package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.List;

public interface TagWrapper extends EntityWrapper<TagWrapper> {
    String getTagName();
    List<TagWrapper> getParentTags();
    List<TagWrapper> getChildTags();
    List<CategoryWrapper> getCategories();
    PropertyTagWrapper getProperty(final int propertyId);
    Integer getInCategorySort();
    boolean containedInCategory(int categoryId);
}
