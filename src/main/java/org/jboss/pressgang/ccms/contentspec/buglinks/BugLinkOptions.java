/*
  Copyright 2011-2014 Red Hat

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec.buglinks;

public class BugLinkOptions {
    private String baseUrl = null;
    private boolean injectLinks = true;
    private boolean useEntities = false;

    public boolean isBugLinksEnabled() {
        return injectLinks;
    }

    /**
     * @param enabled Whether bug links should be injected
     */
    public void setBugLinksEnabled(final boolean enabled) {
        injectLinks = enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isUseEntities() {
        return useEntities;
    }

    public void setUseEntities(boolean useEntities) {
        this.useEntities = useEntities;
    }
}
