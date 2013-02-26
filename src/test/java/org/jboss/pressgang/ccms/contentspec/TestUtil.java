package org.jboss.pressgang.ccms.contentspec;

import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;

import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.mockito.BDDMockito.given;

/**
 * Shared utility methods to assist with testing.
 *
 * @author kamiller@redhat.com (Katie Miller)
 */
public class TestUtil {
    public static <T> T selectRandomListItem(List<T> list) {
        return list.get(nextInt(list.size()));
    }
}
