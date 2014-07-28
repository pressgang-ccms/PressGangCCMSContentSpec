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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jboss.pressgang.ccms.contentspec.InitialContent;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.exceptions.ValidationException;
import org.jboss.pressgang.ccms.utils.common.StringUtilities;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;

public abstract class BaseBugLinkStrategy<T extends BugLinkOptions> {
    protected static final String ENCODING = "UTF-8";
    protected static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String serverUrl;

    protected BaseBugLinkStrategy() {
    }

    public abstract void initialise(final String serverUrl, final Object... args);

    public abstract String generateUrl(T bugOptions, SpecTopic specTopic, String buildName,
            Date buildDate) throws UnsupportedEncodingException;

    public abstract String generateUrl(T bugOptions, InitialContent initialContent, String buildName,
            Date buildDate) throws UnsupportedEncodingException;

    public String generateEntities(T bugOptions, String buildName, Date buildDate) throws UnsupportedEncodingException {
        final StringBuilder retValue = new StringBuilder();

        // Add the build name/date
        final String urlEncodedBuildDate = URLEncoder.encode(DATE_FORMATTER.format(buildDate), ENCODING);
        final String urlEncodedBuildName = URLEncoder.encode(buildName, ENCODING);
        retValue.append("<!ENTITY BUILD_DATE \"").append(StringUtilities.escapeForXMLEntity(urlEncodedBuildDate)).append("\">\n");
        retValue.append("<!ENTITY BUILD_NAME \"").append(StringUtilities.escapeForXMLEntity(urlEncodedBuildName)).append("\">\n");

        return retValue.toString();
    }

    /**
     * Validates the bugzilla options against an external server.
     *
     * @param bugOptions
     * @throws ValidationException
     */
    public abstract void validate(T bugOptions) throws ValidationException;

    public abstract boolean hasValuesChanged(ContentSpecWrapper contentSpecEntity, T bugOptions);

    /**
     * Check that the values in the bug options are valid so that they can be sent to an external server.
     *
     * @param bugOptions
     * @throws ValidationException
     */
    public abstract void checkValidValues(T bugOptions) throws ValidationException;

    protected String getServerUrl() {
        return serverUrl;
    }

    protected void setServerUrl(final String serverUrl) {
        this.serverUrl = serverUrl;
    }

    protected String getFixedServerUrl() {
        return getServerUrl() == null ? null : (getServerUrl().endsWith("/") ? getServerUrl() : (getServerUrl() + "/"));
    }

    protected StringBuilder buildBaseEnvironment(final T bzOptions, final String buildName, final Date buildDate) {
        final StringBuilder bugzillaEnvironment = new StringBuilder("Build Name: ");
        if (!bzOptions.isUseEntities()) {
            bugzillaEnvironment.append(buildName);
        }
        bugzillaEnvironment.append("\nBuild Date: ");
        if (!bzOptions.isUseEntities()) {
            bugzillaEnvironment.append(DATE_FORMATTER.format(buildDate));
        }

        return bugzillaEnvironment;
    }

    protected String addBuildNameAndDateEntities(final String encodedEnvironment) {
        return  encodedEnvironment.replace("Build+Date%3A+", "Build+Date%3A+&BUILD_DATE;")
                .replace("Build+Name%3A+", "Build+Name%3A+&BUILD_NAME;");
    }
}
