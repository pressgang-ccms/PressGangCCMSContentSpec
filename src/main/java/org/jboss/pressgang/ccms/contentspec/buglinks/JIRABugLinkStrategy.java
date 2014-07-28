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

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jboss.pressgang.ccms.contentspec.InitialContent;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.exceptions.BugLinkException;
import org.jboss.pressgang.ccms.contentspec.exceptions.ValidationException;
import org.jboss.pressgang.ccms.contentspec.utils.EntityUtilities;
import org.jboss.pressgang.ccms.jira.rest.JIRAProxyFactory;
import org.jboss.pressgang.ccms.jira.rest.JIRARESTInterface;
import org.jboss.pressgang.ccms.jira.rest.entities.component.JIRAComponent;
import org.jboss.pressgang.ccms.jira.rest.entities.project.JIRAProject;
import org.jboss.pressgang.ccms.jira.rest.entities.version.JIRAVersion;
import org.jboss.pressgang.ccms.provider.exception.ProviderException;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JIRABugLinkStrategy extends BaseBugLinkStrategy<JIRABugLinkOptions> {
    private static final Logger LOG = LoggerFactory.getLogger(JIRABugLinkStrategy.class);
    protected static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");
    protected static final String DESCRIPTION_TEMPLATE = "Title: %s\n\n" + "Describe the issue:\n\n\nSuggestions for " +
            "improvement:\n\n\nAdditional information:";

    private JIRARESTInterface client = null;
    private List<JIRAProject> projects = null;
    private Map<String, JIRAProject> projectKeyMap = new HashMap<String, JIRAProject>();

    public JIRABugLinkStrategy() {
    }

    @Override
    public void initialise(final String jiraUrl, final Object... args) {
        setServerUrl(jiraUrl);
        if (jiraUrl != null) {
            client = JIRAProxyFactory.create(jiraUrl).getRESTClient();
        }
    }

    @Override
    public String generateUrl(final JIRABugLinkOptions bugOptions, final SpecTopic specTopic, final String buildName,
            Date buildDate) throws UnsupportedEncodingException {
        final BaseTopicWrapper<?> topic = specTopic.getTopic();

        final String description = URLEncoder.encode(String.format(DESCRIPTION_TEMPLATE, topic.getTitle()), ENCODING);
        final StringBuilder jiraEnvironment = buildBaseEnvironment(bugOptions, buildName, buildDate);
        jiraEnvironment.append("\nTopic ID: ").append(topic.getTopicId()).append("-").append(topic.getTopicRevision());
        if (specTopic.getRevision() == null) {
            jiraEnvironment.append(" [Latest]");
        } else {
            jiraEnvironment.append(" [Specified]");
        }

        // Encode the URL and add in the build name/date entities
        final String encodedJIRAEnvironment = addBuildNameAndDateEntities(URLEncoder.encode(jiraEnvironment.toString(), ENCODING));

        return generateUrl(bugOptions, description, encodedJIRAEnvironment);
    }

    @Override
    public String generateUrl(final JIRABugLinkOptions bugOptions, final InitialContent initialContent, final String buildName,
            Date buildDate) throws UnsupportedEncodingException {
        final String description = URLEncoder.encode(String.format(DESCRIPTION_TEMPLATE, initialContent.getParent().getTitle()), ENCODING);
        final StringBuilder jiraEnvironment = buildBaseEnvironment(bugOptions, buildName, buildDate);
        jiraEnvironment.append("\nTopic IDs:");

        for (final SpecTopic initialContentTopic : initialContent.getSpecTopics()) {
            final BaseTopicWrapper<?> topic = initialContentTopic.getTopic();

            jiraEnvironment.append("\n");
            jiraEnvironment.append(topic.getTopicId()).append("-").append(topic.getTopicRevision());
            if (initialContentTopic.getRevision() == null) {
                jiraEnvironment.append(" [Latest]");
            } else {
                jiraEnvironment.append(" [Specified]");
            }
        }

        // Encode the URL and add in the build name/date entities
        final String encodedJIRAEnvironment;
        if (bugOptions.isUseEntities()) {
            encodedJIRAEnvironment = addBuildNameAndDateEntities(URLEncoder.encode(jiraEnvironment.toString(), ENCODING));
        } else {
            encodedJIRAEnvironment = URLEncoder.encode(jiraEnvironment.toString(), ENCODING);
        }

        return generateUrl(bugOptions, description, encodedJIRAEnvironment);
    }

    @Override
    public String generateEntities(final JIRABugLinkOptions bugOptions, final String buildName,
            final Date buildDate) throws UnsupportedEncodingException {
        final StringBuilder retValue = new StringBuilder(super.generateEntities(bugOptions, buildName, buildDate));

        final JIRAProject project = getJIRAProject(client, bugOptions.getProject());
        if (project == null) {
            throw new BugLinkException("The JIRA Project \"" + bugOptions.getProject() + "\" cannot be found");
        } else {
            retValue.append("<!ENTITY BUILD_JIRA_PID \"").append(project.getId()).append("\">\n");

            if (bugOptions.getComponent() != null) {
                final JIRAComponent component = getJIRAComponent(bugOptions.getComponent(), project);
                if (component == null) {
                    throw new BugLinkException("The JIRA Component \"" + bugOptions.getComponent() + "\" cannot be found");
                } else {
                    retValue.append("<!ENTITY BUILD_JIRA_CID \"").append(component.getId()).append("\">\n");
                }
            }

            if (bugOptions.getVersion() != null) {
                final JIRAVersion version = getJIRAVersion(bugOptions.getVersion(), project);
                if (version == null) {
                    throw new BugLinkException("The JIRA Version \"" + bugOptions.getVersion() + "\" cannot be found");
                } else {
                    retValue.append("<!ENTITY BUILD_JIRA_VID \"").append(version.getId()).append("\">\n");
                }
            }

            return retValue.toString();
        }
    }

    protected String generateUrl(final JIRABugLinkOptions bugOptions, final String encodedDescription,
            final String encodedJIRAEnvironment) throws UnsupportedEncodingException {
        // build the bugzilla url options
        final StringBuilder JIRAURLComponents = new StringBuilder("?issuetype=1");

        JIRAURLComponents.append("&amp;");
        JIRAURLComponents.append("environment=").append(encodedJIRAEnvironment);
        JIRAURLComponents.append("&amp;");
        JIRAURLComponents.append("description=").append(encodedDescription);

        final JIRAProject project = getJIRAProject(client, bugOptions.getProject());
        if (project == null) {
            throw new BugLinkException("The JIRA Project \"" + bugOptions.getProject() + "\" cannot be found");
        } else {
            JIRAURLComponents.append("&amp;");
            JIRAURLComponents.append("pid=");
            if (bugOptions.isUseEntities()) {
                JIRAURLComponents.append("&BUILD_JIRA_PID;");
            } else {
                JIRAURLComponents.append(project.getId());
            }

            if (bugOptions.getComponent() != null) {
                JIRAURLComponents.append("&amp;");
                JIRAURLComponents.append("components=");
                if (bugOptions.isUseEntities()) {
                    JIRAURLComponents.append("&BUILD_JIRA_CID;");
                } else {
                    final JIRAComponent component = getJIRAComponent(bugOptions.getComponent(), project);
                    if (project == null) {
                        throw new BugLinkException("The JIRA Component \"" + bugOptions.getComponent() + "\" cannot be found");
                    } else {
                        JIRAURLComponents.append(component.getId());
                    }
                }
            }

            if (bugOptions.getVersion() != null) {
                JIRAURLComponents.append("&amp;");
                JIRAURLComponents.append("versions=");
                if (bugOptions.isUseEntities()) {
                    JIRAURLComponents.append("&BUILD_JIRA_VID;");
                } else {
                    final JIRAVersion version = getJIRAVersion(bugOptions.getVersion(), project);
                    if (project == null) {
                        throw new BugLinkException("The JIRA Version \"" + bugOptions.getVersion() + "\" cannot be found");
                    } else {
                        JIRAURLComponents.append(version.getId());
                    }
                }
            }

            if (bugOptions.getLabels() != null) {
                final String[] labels = bugOptions.getLabels().split("\\s*,\\s*");
                for (final String label : labels) {
                    JIRAURLComponents.append("&amp;");
                    JIRAURLComponents.append("labels=");
                    JIRAURLComponents.append(URLEncoder.encode(label, ENCODING));
                }
            }

            // build the JIRA url with the base components
            return getFixedServerUrl() + "secure/CreateIssueDetails!init.jspa" + JIRAURLComponents.toString();
        }
    }

    @Override
    public void validate(final JIRABugLinkOptions jiraOptions) throws ValidationException {
        if (isNullOrEmpty(jiraOptions.getProject())) {
            throw new ValidationException("No Jira Project was specified.");
        } else {
            final JIRAProject project = getJIRAProject(client, jiraOptions.getProject());
            if (project == null) {
                throw new ValidationException("No JIRA Project exists for project \"" + jiraOptions.getProject() + "\".");
            } else {
                // Validate the JIRA Component
                if (jiraOptions.getComponent() != null) {
                    final JIRAComponent component = getJIRAComponent(jiraOptions.getComponent(), project);
                    if (component == null) {
                        throw new ValidationException("No JIRA Component exists for component \"" + jiraOptions.getComponent() + "\".");
                    }
                }

                // Validate the JIRA Version
                if (jiraOptions.getVersion() != null) {
                    final JIRAVersion version = getJIRAVersion(jiraOptions.getVersion(), project);
                    if (version == null) {
                        throw new ValidationException("No JIRA Version exists for version \"" + jiraOptions.getVersion() + "\".");
                    }
                }
            }
        }
    }

    @Override
    public boolean hasValuesChanged(ContentSpecWrapper contentSpecEntity, JIRABugLinkOptions bugOptions) {
        boolean changed = false;
        // Server
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_JIRA_SERVER_TITLE, getServerUrl(), contentSpecEntity)) {
            changed = true;
        }

        // Project
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_JIRA_PROJECT_TITLE, bugOptions.getProject(),
                contentSpecEntity)) {
            changed = true;
        }

        // Version
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_JIRA_VERSION_TITLE, bugOptions.getVersion(),
                contentSpecEntity)) {
            changed = true;
        }

        // Component
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_JIRA_COMPONENT_TITLE, bugOptions.getComponent(),
                contentSpecEntity)) {
            changed = true;
        }

        // Labels
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_JIRA_LABELS_TITLE, bugOptions.getLabels(),
                contentSpecEntity)) {
            changed = true;
        }

        return changed;
    }

    @Override
    public void checkValidValues(JIRABugLinkOptions bugOptions) throws ValidationException {
        if (isNullOrEmpty(getServerUrl())) {
            throw new ValidationException("No JIRA server set.");
        } else if (isNullOrEmpty(bugOptions.getProject())) {
            throw new ValidationException("No JIRA Project has been specified. A Project must be specified for all JIRA links.");
        }
    }

    protected JIRAProject getJIRAProject(final JIRARESTInterface client, final String project) {
        try {
            // Check our key map first
            if (projectKeyMap.containsKey(project)) {
                return projectKeyMap.get(project);
            }

            if (projects == null) {
                // Try and get the project first if the project entered is the project key
                JIRAProject projectEntity = null;
                try {
                    projectEntity = client.getProject(project);
                } catch (Exception e) {
                    LOG.debug("", e);
                    // do nothing as we will pick up a missing project later
                }

                // If the project isn't null then we found a matching one, otherwise load all the projects
                if (projectEntity != null) {
                    projectKeyMap.put(project, projectEntity);
                    return projectEntity;
                } else {
                    projects = client.getProjects();
                }
            }

            // Check all the projects to find one that matches
            for (final JIRAProject projectEntity : projects) {
                if (projectEntity.getKey() != null && projectEntity.getKey().equals(project)) {
                    return projectEntity;
                } else if ((projectEntity.getName() != null && projectEntity.getName().equals(project)) || (NUMBER_PATTERN.matcher(
                        project).matches() && projectEntity.getId().equals(Long.parseLong(project)))) {
                    // Load the project from the server as we'll need it to get the components/versions
                    final JIRAProject foundProject = client.getProject(projectEntity.getKey());
                    projectKeyMap.put(project, foundProject);
                    return foundProject;
                }
            }
        } catch (ProviderException e) {
            // We have to catch this because of the Error Interceptor
        } catch (ClientResponseFailure e) {
            // Do nothing, as if an error occurs than it likely means the project doesn't exist
        }

        return null;
    }

    protected JIRAComponent getJIRAComponent(final String component, final JIRAProject project) {
        if (project.getComponents() != null) {
            for (final JIRAComponent componentEntity : project.getComponents()) {
                if (componentEntity.getName() != null && componentEntity.getName().equals(component)) {
                    return componentEntity;
                } else if (NUMBER_PATTERN.matcher(component).matches() && componentEntity.getId().equals(Long.parseLong(component))) {
                    return componentEntity;
                }
            }
        }

        return null;
    }

    protected JIRAVersion getJIRAVersion(final String version, final JIRAProject project) {
        if (project.getVersions() != null) {
            for (final JIRAVersion versionEntity : project.getVersions()) {
                if (versionEntity.getName() != null && versionEntity.getName().equals(version)) {
                    return versionEntity;
                } else if (NUMBER_PATTERN.matcher(version).matches() && versionEntity.getId().equals(Long.parseLong(version))) {
                    return versionEntity;
                }
            }
        }

        return null;
    }
}
