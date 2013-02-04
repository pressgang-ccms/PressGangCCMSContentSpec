package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInPropertyCategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface PropertyTagInPropertyCategoryProvider extends PropertyTagProvider {
    CollectionWrapper<PropertyTagInPropertyCategoryWrapper> getPropertyTagInPropertyCategoryRevisions(int id, Integer revision);
}
