package org.jboss.pressgang.ccms.docbook.processing;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.entities.JIRABugLinkOptions;
import org.jboss.pressgang.ccms.contentspec.exceptions.ValidationException;
import org.jboss.pressgang.ccms.docbook.compiling.BugLinkStrategy;
import org.jboss.pressgang.ccms.jira.rest.JIRAProxyFactory;
import org.jboss.pressgang.ccms.jira.rest.JIRARESTInterface;
import org.jboss.pressgang.ccms.jira.rest.entities.component.JIRAComponent;
import org.jboss.pressgang.ccms.jira.rest.entities.project.JIRAProject;
import org.jboss.pressgang.ccms.jira.rest.entities.version.JIRAVersion;
import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;
import org.jboss.resteasy.client.ClientResponseFailure;

public class JIRABugLinkStrategy implements BugLinkStrategy<JIRABugLinkOptions> {
    protected static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");
    protected static final String ENCODING = "UTF-8";
    protected static final String DESCRIPTION_TEMPLATE = "Title: %s\n\n" + "Describe the issue:\n\n\nSuggestions for " +
            "improvement:\n\n\nAdditional information:";

    private final JIRARESTInterface client;
    private final String jiraUrl;
    private List<JIRAProject> projects = null;
    private Map<String, JIRAProject> projectKeyMap = new HashMap<String, JIRAProject>();

    public JIRABugLinkStrategy(final String jiraUrl) {
        client = JIRAProxyFactory.create(jiraUrl).getRESTClient();
        this.jiraUrl = jiraUrl.endsWith("/") ? jiraUrl : (jiraUrl + "/");;
    }

    @Override
    public String generateUrl(JIRABugLinkOptions bugOptions, SpecTopic specTopic, String buildName) throws UnsupportedEncodingException {
        final BaseTopicWrapper<?> topic = specTopic.getTopic();

        final String description = URLEncoder.encode(String.format(DESCRIPTION_TEMPLATE, topic.getTitle()), ENCODING);
        final StringBuilder jiraEnvironment = new StringBuilder("\nBuild Name: ").append(buildName).append("\nTopic ID: ").append
                (topic.getId()).append("-").append(topic.getRevision());
        final String encodedJIRAEnvironment = URLEncoder.encode("Build Date: ", ENCODING) + "&BUILDDATE;" + URLEncoder.encode
                (jiraEnvironment.toString(), ENCODING);

        // build the bugzilla url options
        final StringBuilder JIRAURLComponents = new StringBuilder("?issuetype=1");

        JIRAURLComponents.append("&amp;");
        JIRAURLComponents.append("environment=").append(encodedJIRAEnvironment);
        JIRAURLComponents.append("&amp;");
        JIRAURLComponents.append("description=").append(description);

        final JIRAProject project = getJIRAProject(client, bugOptions.getProject());
        JIRAURLComponents.append("&amp;");
        JIRAURLComponents.append("pid=").append(project.getId());

        if (bugOptions.getComponent() != null) {
            final JIRAComponent component = getJIRAComponent(bugOptions.getComponent(), project);
            JIRAURLComponents.append("&amp;");
            JIRAURLComponents.append("components=").append(component.getId());
        }

        if (bugOptions.getVersion() != null) {
            final JIRAVersion version = getJIRAVersion(bugOptions.getVersion(), project);
            JIRAURLComponents.append("&amp;");
            JIRAURLComponents.append("versions=").append(version.getId());
        }

        if (bugOptions.getLabels() != null) {
            final String[] labels = bugOptions.getLabels().split("\\s*,\\s*");
            for (final String label : labels) {
                JIRAURLComponents.append("&amp;");
                JIRAURLComponents.append("labels=").append(URLEncoder.encode(label, ENCODING));
            }
        }

        // build the JIRA url with the base components
        return jiraUrl + "secure/CreateIssueDetails!init.jspa" + JIRAURLComponents.toString();
    }

    @Override
    public void validate(final JIRABugLinkOptions jiraOptions) throws ValidationException {
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
                    throw new ValidationException("No JIRA Version exists for version \"" + jiraOptions.getComponent() + "\".");
                }
            }
        }
    }

    protected JIRAProject getJIRAProject(final JIRARESTInterface client, final String project) {
        if (projects == null) {
            // Check our key map first
            if (projectKeyMap.containsKey(project)) {
                return projectKeyMap.get(project);
            }

            // Try and get the project first if the project entered is the project key
            JIRAProject projectEntity = null;
            try {
                projectEntity = client.getProject(project);
            } catch (ClientResponseFailure e) {

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
            } else if (projectEntity.getName() != null && projectEntity.getName().equals(project)) {
                return projectEntity;
            } else if (NUMBER_PATTERN.matcher(project).matches() && projectEntity.getId().equals(Long.parseLong(project))) {
                return projectEntity;
            }
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