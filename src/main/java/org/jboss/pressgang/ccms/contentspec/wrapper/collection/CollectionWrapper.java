package org.jboss.pressgang.ccms.contentspec.wrapper.collection;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.EntityWrapper;

public interface CollectionWrapper<T extends EntityWrapper<T>> {
    public void addItem(T entity);

    public void addNewItem(T entity);

    public void addRemoveItem(T entity);

    public void remove(T entity);

    public List<T> getItems();

    public List<T> getAddItems();

    public List<T> getRemoveItems();

    public Object unwrap();

    public int size();

    public boolean isEmpty();
}