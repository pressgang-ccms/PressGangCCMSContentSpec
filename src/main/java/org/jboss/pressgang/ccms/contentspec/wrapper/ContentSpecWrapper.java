package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.Date;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.EntityWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface ContentSpecWrapper extends EntityWrapper<ContentSpecWrapper> {
    CollectionWrapper<TagWrapper> getTags();

    void setTags(CollectionWrapper<TagWrapper> tags);

    UpdateableCollectionWrapper<CSMetaDataInContentSpecWrapper> getMetaData();

    void setMetaData(CollectionWrapper<CSMetaDataInContentSpecWrapper> metaData);

    CollectionWrapper<CSNodeWrapper> getChildren();

    void setChildren(CollectionWrapper<CSNodeWrapper> nodes);

    UpdateableCollectionWrapper<PropertyTagInContentSpecWrapper> getProperties();

    void setProperties(UpdateableCollectionWrapper<PropertyTagInContentSpecWrapper> properties);

    String getTitle();

    void setTitle(String title);

    String getProduct();

    void setProduct(String product);

    String getVersion();

    void setVersion(String version);

    String getLocale();

    void setLocale(String locale);

    Date getLastModified();

    PropertyTagInContentSpecWrapper getProperty(final int propertyId);

}
