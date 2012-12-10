package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.ImageWrapper;

public interface LanguageImageProvider {
    byte[] getLanguageImageData(int id, Integer revision, ImageWrapper parent);
    byte[] getLanguageImageDataBase64(int id, Integer revision, ImageWrapper parent);
    byte[] getLanguageImageThumbnail(int id, Integer revision, ImageWrapper parent);
}
