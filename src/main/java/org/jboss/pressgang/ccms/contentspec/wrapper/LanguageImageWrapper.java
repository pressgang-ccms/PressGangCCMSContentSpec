package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.EntityWrapper;

public interface LanguageImageWrapper extends EntityWrapper<LanguageImageWrapper> {
    String getFilename();

    String getLocale();

    byte[] getImageData();

    byte[] getImageDataBase64();

    byte[] getThumbnail();
}
