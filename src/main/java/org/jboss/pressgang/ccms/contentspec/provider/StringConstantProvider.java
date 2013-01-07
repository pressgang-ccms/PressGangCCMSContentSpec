package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.StringConstantWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface StringConstantProvider {
    StringConstantWrapper getStringConstant(int id);

    StringConstantWrapper getStringConstant(int id, Integer revision);

    CollectionWrapper<StringConstantWrapper> getStringConstantRevisions(int id, Integer revision);
}
