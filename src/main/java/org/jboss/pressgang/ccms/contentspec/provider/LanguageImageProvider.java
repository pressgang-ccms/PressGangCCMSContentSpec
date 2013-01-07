package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.LanguageImageWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface LanguageImageProvider {
    LanguageImageWrapper getLanguageImage(int id, Integer revision);

    byte[] getLanguageImageData(int id, Integer revision);

    byte[] getLanguageImageDataBase64(int id, Integer revision);

    byte[] getLanguageImageThumbnail(int id, Integer revision);

    CollectionWrapper<LanguageImageWrapper> getLanguageImageRevisions(int id, Integer revision);
}
