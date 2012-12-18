package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.StringConstantWrapper;

public interface StringConstantProvider {
    StringConstantWrapper getStringConstant(int id);

    StringConstantWrapper getStringConstant(int id, Integer revision);
}
