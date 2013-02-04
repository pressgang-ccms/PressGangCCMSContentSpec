package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface PropertyTagInTagProvider extends PropertyTagProvider {
    CollectionWrapper<PropertyTagInTagWrapper> getPropertyTagInTagRevisions(int id, Integer revision);
}
