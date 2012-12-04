package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.List;

public interface ImageWrapper extends EntityWrapper<ImageWrapper> {
    String getDescription();
    List<LanguageImageWrapper> getLanguageImages();
}
