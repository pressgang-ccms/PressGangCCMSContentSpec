package org.jboss.pressgang.ccms.contentspec;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.pressgang.ccms.contentspec.enums.LevelType;
import org.jboss.pressgang.ccms.contentspec.enums.RelationshipType;
import org.jboss.pressgang.ccms.contentspec.utils.ContentSpecUtilities;
import org.jboss.pressgang.ccms.provider.ServerSettingsProvider;
import org.jboss.pressgang.ccms.provider.TopicProvider;
import org.jboss.pressgang.ccms.wrapper.TopicWrapper;

/**
 * A class that is used to represent and process a "Process" within a Content Specification.
 *
 * @author lnewson
 */
public class Process extends Level {

    private final LinkedHashMap<String, SpecTopic> topics = new LinkedHashMap<String, SpecTopic>();
    private boolean topicsProcessed = false;

    /**
     * Constructor
     *
     * @param title      The Title of the Process.
     * @param lineNumber The Line Number of Level in the Content Specification.
     * @param specLine   The Content Specification Line that is used to create the Process.
     */
    public Process(final String title, final int lineNumber, final String specLine) {
        super(title, lineNumber, specLine, LevelType.PROCESS);
    }

    /**
     * Constructor
     *
     * @param title The Title of the Process.
     */
    public Process(final String title) {
        super(title, LevelType.PROCESS);
    }

    @Override
    public void appendSpecTopic(final SpecTopic specTopic) {
        topics.put(specTopic.getUniqueId(), specTopic);
        nodes.add(specTopic);
        specTopic.setParent(this);
    }

    @Override
    public void removeSpecTopic(final SpecTopic specTopic) {
        topics.remove(specTopic.getUniqueId());
        nodes.remove(specTopic);
        specTopic.setParent(null);
    }

    @Override
    public int getNumberOfSpecTopics() {
        return topics.size();
    }

    /**
     * Check if the topics in the Process have already been processed.
     *
     * @return True if the topics have been processed otherwise false.
     */
    public boolean isTopicsProcessed() {
        return topicsProcessed;
    }

    /**
     * Gets all of the Content Specification Unique Topic ID's that are used in the process.
     *
     * @return A List of Unique Topic ID's.
     */
    protected List<String> getTopicIds() {
        LinkedList<String> topicIds = new LinkedList<String>();
        for (final Entry<String, SpecTopic> specTopicEntry : topics.entrySet()) {
            topicIds.add(specTopicEntry.getKey());
        }
        return topicIds;
    }

    @Override
    public LinkedList<SpecTopic> getSpecTopics() {
        final LinkedList<SpecTopic> topicList = new LinkedList<SpecTopic>();
        if (!getFrontMatterTopics().isEmpty()) {
            topicList.addAll(getFrontMatterTopics());
        }
        for (final Entry<String, SpecTopic> specTopicEntry : topics.entrySet()) {
            topicList.add(specTopicEntry.getValue());
        }
        return topicList;
    }

    /**
     * Processes a processes topics and creates the targets and relationships
     *
     * @param specTopics        A mapping of all the topics in a content specification to their unique ids
     * @param topicTargets      The topic targets that already exist in a content specification
     * @param topicDataProvider A TopicProvider object that is used to access database objects via the REST Interface
     * @param serverSettingsProvider A TopicProvider object that is used to access the server settings.
     * @return True if everything loaded successfully otherwise false
     */
    public boolean processTopics(final Map<String, SpecTopic> specTopics, final Map<String, SpecTopic> topicTargets,
            final TopicProvider topicDataProvider, final ServerSettingsProvider serverSettingsProvider) {
        // Check if the topics have already been processed. If so then don't re-process them.
        if (isTopicsProcessed()) return true;

        final Integer taskId = serverSettingsProvider.getServerSettings().getEntities().getTaskTagId();

        boolean successfullyLoaded = true;
        SpecTopic prevTopic = null;
        int count = 1;
        final LinkedList<String> processTopics = new LinkedList<String>(getTopicIds());
        for (final String uniqueTopicId : processTopics) {
            final SpecTopic specTopic = topics.get(uniqueTopicId);

            // If the topic is an existing or cloned topic then use the database information
            if (specTopic.isTopicAnExistingTopic() || specTopic.isTopicAClonedTopic() || specTopic.isTopicAClonedDuplicateTopic()) {
                // Get the topic information from the database
                final TopicWrapper topic;
                if (specTopic.isTopicAClonedTopic()) {
                    topic = topicDataProvider.getTopic(Integer.parseInt(specTopic.getId().substring(1)), null);
                } else if (specTopic.isTopicAClonedDuplicateTopic()) {
                    topic = topicDataProvider.getTopic(Integer.parseInt(specTopic.getId().substring(2)), null);
                } else {
                    topic = topicDataProvider.getTopic(specTopic.getDBId(), null);
                }

                if (topic != null) {
                    // Add relationships if the topic is a task
                    if (topic.hasTag(taskId)) {
                        // Create a target if one doesn't already exist
                        if (specTopic.getTargetId() == null) {
                            // Create a randomly generated target id using the process topic count
                            String topicTargetId = ContentSpecUtilities.generateRandomTargetId(specTopic.getUniqueId(), count);
                            // Check that the topic id doesn't already exist. If it does then keep generating random numbers
                            // until a unique one is found
                            while (topicTargets.containsKey(topicTargetId)) {
                                topicTargetId = ContentSpecUtilities.generateRandomTargetId(specTopic.getUniqueId(), count);
                            }

                            specTopic.setTargetId(topicTargetId);
                        }

                        if (prevTopic != null) {
                            specTopic.addRelationshipToProcessTopic(prevTopic, RelationshipType.PREVIOUS);
                            prevTopic.addRelationshipToProcessTopic(specTopic, RelationshipType.NEXT);
                        }

                        // Set the current topic as the previous topic
                        prevTopic = specTopic;
                    }
                } else {
                    successfullyLoaded = false;
                }
            } else {
                // Not an existing or cloned topic
                // The Topic is a duplicated topic so get the type from the original topic
                String type = specTopic.getType();
                if (specTopic.isTopicADuplicateTopic()) {
                    final SpecTopic referenceTopic = specTopics.get("N" + specTopic.getId().substring(1));
                    if (referenceTopic == null) {
                        continue;
                    }
                    type = referenceTopic.getType();
                }
                // Add relationships if the topic is a task
                if (type.equals("Task")) {
                    // Create a target if one doesn't already exist
                    if (specTopic.getTargetId() == null) {
                        // Create a randomly generated target id using the process topic count
                        String topicTargetId = ContentSpecUtilities.generateRandomTargetId(specTopic.getUniqueId(), count);
                        // Check that the topic id doesn't already exist. If it does then keep generating random numbers until a
                        // unique one is found
                        while (topicTargets.containsKey(topicTargetId)) {
                            topicTargetId = ContentSpecUtilities.generateRandomTargetId(specTopic.getUniqueId(), count);
                        }

                        specTopic.setTargetId(topicTargetId);
                    }

                    if (prevTopic != null) {
                        specTopic.addRelationshipToProcessTopic(prevTopic, RelationshipType.PREVIOUS);
                        prevTopic.addRelationshipToProcessTopic(specTopic, RelationshipType.NEXT);
                    }

                    // Set the current topic as the previous topic
                    prevTopic = specTopic;
                }
            }
            count++;
        }

        topicsProcessed = true;

        return successfullyLoaded;
    }

    @Override
    public String toString() {
        final StringBuilder output = new StringBuilder();
        output.append(getSpacer());
        output.append(getText());
        output.append("\n");

        for (final Node node : nodes) {
            output.append(node.toString());
        }
        return output.toString();
    }
}
