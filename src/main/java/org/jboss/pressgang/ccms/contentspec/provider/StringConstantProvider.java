package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.StringConstantWrapper;

public interface StringConstantProvider {
    StringConstantWrapper getStringConstant(final int id);
}
