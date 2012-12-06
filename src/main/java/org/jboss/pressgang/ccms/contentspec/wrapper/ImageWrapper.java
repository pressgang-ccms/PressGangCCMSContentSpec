package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface ImageWrapper extends EntityWrapper<ImageWrapper> {
    String getDescription();
    CollectionWrapper<LanguageImageWrapper> getLanguageImages();
}
