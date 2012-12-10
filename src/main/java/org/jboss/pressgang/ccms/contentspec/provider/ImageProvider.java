package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.LanguageImageWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface ImageProvider {
    CollectionWrapper<LanguageImageWrapper> getImageLanguageImages(int id, Integer revision);
}
