package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.ImageWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.LanguageImageWrapper;

public interface LanguageImageProvider {
    LanguageImageWrapper getLanguageImage(int id, Integer revision, ImageWrapper parent);

    byte[] getLanguageImageData(int id, Integer revision, ImageWrapper parent);

    byte[] getLanguageImageDataBase64(int id, Integer revision, ImageWrapper parent);

    byte[] getLanguageImageThumbnail(int id, Integer revision, ImageWrapper parent);
}
