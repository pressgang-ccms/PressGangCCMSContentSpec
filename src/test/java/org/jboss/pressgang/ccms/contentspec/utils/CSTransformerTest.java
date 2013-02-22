package org.jboss.pressgang.ccms.contentspec.utils;

import org.jboss.pressgang.ccms.contentspec.BaseUnitTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.powermock.modules.junit4.rule.PowerMockRule;

/**
 * @author kamiller@redhat.com (Katie Miller)
 */
@Ignore
public class CSTransformerTest extends BaseUnitTest {

    @Rule public PowerMockRule rule = new PowerMockRule();

    protected CSTransformer transformer;

    @Before
    public void setUp() {
        this.transformer = new CSTransformer();
    }
}
