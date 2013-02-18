package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.Date;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.EntityWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface ContentSpecWrapper extends EntityWrapper<ContentSpecWrapper> {
    CollectionWrapper<TagWrapper> getTags();

    void setTags(CollectionWrapper<TagWrapper> tags);

    CollectionWrapper<CSNodeWrapper> getChildren();

    void setChildren(CollectionWrapper<CSNodeWrapper> nodes);

    UpdateableCollectionWrapper<PropertyTagInContentSpecWrapper> getProperties();

    void setProperties(UpdateableCollectionWrapper<PropertyTagInContentSpecWrapper> properties);

    String getTitle();

    String getProduct();

    String getVersion();

    String getLocale();

    void setLocale(String locale);

    Date getLastModified();

    PropertyTagInContentSpecWrapper getProperty(final int propertyId);

    CSNodeWrapper getMetaData(final String metaDataTitle);
}
