/*
  Copyright 2011-2014 Red Hat

  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec.buglinks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.pressgang.ccms.contentspec.enums.BugLinkType;

public class BugLinkStrategyFactory {
    protected static final Integer INTERNAL_PRIORITY = 11;
    protected static final Integer MAX_PRIORITY = BugLinkStrategyFactory.INTERNAL_PRIORITY - 1;
    protected static final Integer MIN_PRIORITY = 1;
    private static BugLinkStrategyFactory INSTANCE;

    private boolean internalsRegistered = false;
    private Map<BugLinkType, SortedSet<Helper>> map = Collections.synchronizedMap(new HashMap<BugLinkType, SortedSet<Helper>>());

    public static BugLinkStrategyFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BugLinkStrategyFactory();
        }

        return INSTANCE;
    }

    /**
     * Create a strategy instance to be used for the specified type and server url.
     *
     * @param type The type of strategy to get (eg JIRA, BUGZILLA, etc...)
     * @param serverUrl The url that the bug links should be used against.
     * @param additionalArgs Any additional arguments that are needed to instantiate/configure the strategy.
     * @param <T>
     * @return A registered strategy, or an internal/default helper if no registered strategies exist.
     */
    public <T extends BaseBugLinkStrategy<?>> T create(final BugLinkType type, final String serverUrl,
            final Object... additionalArgs) {
        // Check that the internals are registered
        if (!internalsRegistered) {
            registerInternals();
        }

        if (map.containsKey(type)) {
            try {
                final SortedSet<Helper> helpers = map.get(type);
                T helper = null;
                for (final Helper definedHelper : helpers) {
                    if (definedHelper.useHelper(serverUrl)) {
                        helper = (T) definedHelper.getHelperClass().newInstance();
                        break;
                    }
                }

                if (helper != null) {
                    helper.initialise(serverUrl, additionalArgs);
                }

                return helper;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    protected synchronized void registerInternals() {
        if (!internalsRegistered) {
            if (!map.containsKey(BugLinkType.JIRA)) {
                map.put(BugLinkType.JIRA, new TreeSet<Helper>());
            }
            if (!map.containsKey(BugLinkType.BUGZILLA)) {
                map.put(BugLinkType.BUGZILLA, new TreeSet<Helper>());
            }
            if (!map.containsKey(BugLinkType.OTHER)) {
                map.put(BugLinkType.OTHER, new TreeSet<Helper>());
            }

            map.get(BugLinkType.JIRA).add(new Helper(INTERNAL_PRIORITY, JIRABugLinkStrategy.class));
            map.get(BugLinkType.BUGZILLA).add(new Helper(INTERNAL_PRIORITY, BugzillaBugLinkStrategy.class));
            map.get(BugLinkType.OTHER).add(new Helper(INTERNAL_PRIORITY, DefaultBugLinkHelper.class));

            internalsRegistered = true;
        }
    }

    /**
     * Register a strategy with the factory so that it can be used.
     *
     * @param type          The Bug Links type the strategy is to be used for.
     * @param priority      The priority of the strategy. (1 - 10 with 1 being the Highest and 10 being the lowest)
     * @param strategyClass The strategies class.
     */
    public void registerStrategy(final BugLinkType type, final Integer priority,
            final Class<? extends BaseBugLinkStrategy<?>> strategyClass) {
        registerHelper(type, new Helper(fixPriority(priority), strategyClass));
    }

    /**
     * Register a strategy with the factory so that it can be used.
     *
     * @param type          The Bug Links type the strategy is to be used for.
     * @param priority      The priority of the strategy. (1 - 10 with 1 being the Highest and 10 being the lowest)
     * @param strategyClass The strategies class.
     * @param serverUrl     The server url that the strategy should be used for.
     */
    public void registerStrategy(final BugLinkType type, final Integer priority,
            final Class<? extends BaseBugLinkStrategy<?>> strategyClass, final String serverUrl) {
        registerStrategy(type, priority, strategyClass, Arrays.asList(serverUrl));
    }

    /**
     * Register a strategy with the factory so that it can be used.
     *
     * @param type          The Bug Links type the strategy is to be used for.
     * @param priority      The priority of the strategy. (1 - 10 with 1 being the Highest and 10 being the lowest)
     * @param strategyClass The strategies class.
     * @param serverUrls    A list of server urls that the strategy should be used for.
     */
    public void registerStrategy(final BugLinkType type, final Integer priority,
            final Class<? extends BaseBugLinkStrategy<?>> strategyClass, final List<String> serverUrls) {
        registerHelper(type, new Helper(fixPriority(priority), strategyClass, serverUrls));
    }

    protected void registerHelper(final BugLinkType type, final Helper helper) {
        if (type == null || helper.getHelperClass() == null || helper.getPriority() == null) {
            return;
        }

        if (!map.containsKey(type)) {
            map.put(type, new TreeSet<Helper>());
        }
        map.get(type).add(helper);
    }

    private Integer fixPriority(final Integer priority) {
        if (priority == null) {
            return MAX_PRIORITY;
        } else if (priority <= 0) {
            return MIN_PRIORITY;
        } else if (priority > MAX_PRIORITY) {
            return MAX_PRIORITY;
        } else {
            return priority;
        }
    }
}

class Helper implements Comparable<Helper> {
    private final Integer priority;
    private final Class<? extends BaseBugLinkStrategy<?>> helperClass;
    private final List<String> serverUrls;

    Helper(final Integer priority, final Class<? extends BaseBugLinkStrategy<?>> helperClass) {
        this.priority = priority;
        this.helperClass = helperClass;
        serverUrls = null;
    }

    Helper(final Integer priority, final Class<? extends BaseBugLinkStrategy<?>> helperClass, final List<String> serverUrls) {
        this.priority = priority;
        this.helperClass = helperClass;
        this.serverUrls = fixServerUrls(serverUrls);
    }

    Integer getPriority() {
        return priority;
    }

    Class<? extends BaseBugLinkStrategy<?>> getHelperClass() {
        return helperClass;
    }

    List<String> getServerUrls() {
        return serverUrls;
    }

    private List<String> fixServerUrls(final List<String> serverUrls) {
        final List fixedUrls = new ArrayList<String>();
        for (final String serverUrl : serverUrls) {
            fixedUrls.add(fixServerUrl(serverUrl));
        }
        return fixedUrls;
    }

    private String fixServerUrl(final String serverUrl) {
        return serverUrl == null ? null : (serverUrl.endsWith("/") ? serverUrl : (serverUrl + "/"));
    }

    boolean useHelper(final String serverUrl) {
        if (serverUrls != null) {
            return serverUrls.contains(fixServerUrl(serverUrl));
        } else {
            return true;
        }
    }

    @Override
    public int compareTo(Helper helper) {
        return priority.compareTo(helper.priority);
    }
}
