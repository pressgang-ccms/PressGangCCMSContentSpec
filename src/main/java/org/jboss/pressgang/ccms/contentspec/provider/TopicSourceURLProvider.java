package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.TopicSourceURLWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface TopicSourceURLProvider {

    CollectionWrapper<TopicSourceURLWrapper> getTopicSourceURLRevisions(int id, Integer revision);

    TopicSourceURLWrapper newTopicSourceURL(TopicWrapper parent);

    CollectionWrapper<TopicSourceURLWrapper> newTopicSourceURLCollection(TopicWrapper parent);
}
