package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.ImageWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.LanguageImageWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface ImageProvider {
    ImageWrapper getImage(int id);

    ImageWrapper getImage(int id, Integer revision);

    CollectionWrapper<LanguageImageWrapper> getImageLanguageImages(int id, Integer revision);

    CollectionWrapper<ImageWrapper> getImageRevisions(int id, Integer revision);
}
