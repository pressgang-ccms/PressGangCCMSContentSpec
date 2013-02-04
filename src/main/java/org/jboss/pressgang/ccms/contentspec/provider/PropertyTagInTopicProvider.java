package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface PropertyTagInTopicProvider extends PropertyTagProvider {
    CollectionWrapper<PropertyTagInTopicWrapper> getPropertyTagInTopicRevisions(int id, Integer revision);
}
