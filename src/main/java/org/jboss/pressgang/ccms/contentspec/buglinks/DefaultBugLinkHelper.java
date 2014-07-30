/*
  Copyright 2011-2014 Red Hat, Inc

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

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.jboss.pressgang.ccms.contentspec.InitialContent;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.exceptions.ValidationException;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;

public class DefaultBugLinkHelper extends BaseBugLinkStrategy<BugLinkOptions> {
    @Override
    public void initialise(String serverUrl, Object... args) {
    }

    @Override
    public String generateUrl(BugLinkOptions bugOptions, SpecTopic specTopic, String buildName,
            Date buildDate) throws UnsupportedEncodingException {
        return bugOptions.getBaseUrl();
    }

    @Override
    public String generateUrl(BugLinkOptions bugOptions, InitialContent initialContent, String BuildName,
            Date buildDate) throws UnsupportedEncodingException {
        return bugOptions.getBaseUrl();
    }

    @Override
    public void validate(BugLinkOptions bugOptions) throws ValidationException {
    }

    @Override
    public boolean hasValuesChanged(ContentSpecWrapper contentSpecEntity, BugLinkOptions bugOptions) {
        return false;
    }

    @Override
    public void checkValidValues(BugLinkOptions bugOptions) throws ValidationException {
    }
}
