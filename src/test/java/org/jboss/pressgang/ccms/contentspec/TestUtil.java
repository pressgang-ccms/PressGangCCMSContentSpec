package org.jboss.pressgang.ccms.contentspec;

import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

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
