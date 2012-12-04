package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.List;

public interface CategoryWrapper extends EntityWrapper<CategoryWrapper> {
    List<TagWrapper> getTags();
    boolean isMutuallyExclusive();
    String getName();
}
