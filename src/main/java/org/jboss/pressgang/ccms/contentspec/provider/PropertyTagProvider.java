package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface PropertyTagProvider {
    UpdateableCollectionWrapper<PropertyTagWrapper> getPropertyTagCategories(int id, Integer revision);
}
