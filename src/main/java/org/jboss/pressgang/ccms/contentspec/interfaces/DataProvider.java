package org.jboss.pressgang.ccms.contentspec.interfaces;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.BlobConstantWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.CategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.StringConstantWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicWrapper;

public interface DataProvider {
    /*
     * TOPIC METHODS
     */
    TopicWrapper getTopicById(final int id);
    TopicWrapper getTopicById(final int id, final Integer revision);
    List<TopicWrapper> getTopicsByIds(final List<Integer> ids);
    
    /*
     * TAG METHODS
     */
    TagWrapper getTagById(final int id);
    List<TagWrapper> getTagsByName(final String name);
    
    /*
     * CATEGORY METHODS
     */
    CategoryWrapper getCategoryById(final int id);
    
    /*
     * BLOB CONSTANT METHODS
     */
    BlobConstantWrapper getBlobConstantById(final int id);
    
    /*
     * STRING CONSTANT PROVIDER
     */
    StringConstantWrapper getStringConstantById(final int id);
}
