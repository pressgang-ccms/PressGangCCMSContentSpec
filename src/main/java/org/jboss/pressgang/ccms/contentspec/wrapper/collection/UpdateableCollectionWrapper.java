package org.jboss.pressgang.ccms.contentspec.wrapper.collection;

import org.jboss.pressgang.ccms.contentspec.wrapper.EntityWrapper;

public interface UpdateableCollectionWrapper<T extends EntityWrapper<T>> extends CollectionWrapper<T> {    
    public void addUpdateItem(T entity);
}