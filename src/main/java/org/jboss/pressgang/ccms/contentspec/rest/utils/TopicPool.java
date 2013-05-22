package org.jboss.pressgang.ccms.contentspec.rest.utils;

import javax.ws.rs.core.PathSegment;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.rest.v1.collections.RESTTopicCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentBaseRESTEntityWithPropertiesV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBaseTopicV1;
import org.jboss.pressgang.ccms.rest.v1.expansion.ExpandDataDetails;
import org.jboss.pressgang.ccms.rest.v1.expansion.ExpandDataTrunk;
import org.jboss.pressgang.ccms.rest.v1.jaxrsinterfaces.RESTInterfaceV1;
import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;
import org.jboss.pressgang.ccms.utils.common.ExceptionUtilities;
import org.jboss.resteasy.specimpl.PathSegmentImpl;

/**
 * A fairly simple container class to hold a set of topics that need to be updated or created
 * using the REST API. By using the topic pool it allows for all the topics to be updated or created
 * in one REST call and therefore one transaction.
 *
 * @param <T> The Topic Type eg RESTTranslatedTopicV1 or RESTTopicV1
 * @param <U> The Topics Collection Type eg RESTTranslatedTopicCollectionV1 or RESTTopicCollectionV1
 * @author lnewson
 */
public class TopicPool<T extends RESTBaseTopicV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T,
        U, V>> {

    private static final Logger log = Logger.getLogger(TopicPool.class);

    private RESTTopicCollectionV1 newTopicPool = new RESTTopicCollectionV1();
    private RESTTopicCollectionV1 updatedTopicPool = new RESTTopicCollectionV1();
    private final RESTInterfaceV1 client;
    private final ObjectMapper mapper = new ObjectMapper();
    private boolean initialised = false;

    public TopicPool(final RESTInterfaceV1 client) {
        this.client = client;
    }

    /**
     * Add a topic that is to be created to the topic pool.
     *
     * @param topic The topic to be created.
     */
    public void addNewTopic(final RESTTopicV1 topic) {
        newTopicPool.addItem(topic);
    }

    /**
     * Add a topic that is to be updated to the topic pool.
     *
     * @param topic The topic to be updated.
     */
    public void addUpdatedTopic(final RESTTopicV1 topic) {
        updatedTopicPool.addItem(topic);
    }

    /**
     * Saves all the topics in the pool to the database using the REST API.
     *
     * @return True if all the topics in the pool were saved successfully,
     *         otherwise false.
     */
    public boolean savePool() {
        if ((newTopicPool.returnItems() == null || newTopicPool.returnItems().isEmpty()) && (updatedTopicPool.returnItems() == null ||
                updatedTopicPool.returnItems().isEmpty()))
            return true;
        try {
            /* We need to expand the Properties collection */
            final ExpandDataTrunk expand = new ExpandDataTrunk();
            final ExpandDataTrunk expandTopic = new ExpandDataTrunk(new ExpandDataDetails("topics"));
            expandTopic.setBranches(CollectionUtilities.toArrayList(new ExpandDataTrunk(new ExpandDataDetails("tags")),
                    new ExpandDataTrunk(new ExpandDataDetails("sourceUrls")), new ExpandDataTrunk(new ExpandDataDetails("properties")),
                    new ExpandDataTrunk(new ExpandDataDetails("outgoingRelationships")),
                    new ExpandDataTrunk(new ExpandDataDetails("incomingRelationships"))));
            expand.setBranches(CollectionUtilities.toArrayList(expandTopic));

            final String expandString = mapper.writeValueAsString(expand);
            //final String expandEncodedString = URLEncoder.encode(expandString, "UTF-8");

            // Save the new topics
            if (!(newTopicPool.returnItems() == null || newTopicPool.returnItems().isEmpty())) {
                final RESTTopicCollectionV1 response = client.createJSONTopics(expandString, newTopicPool);
                // Check that the response isn't empty (ie failed)
                if (response == null) return false;
                if (response.returnItems() == null) return false;
                // The response is valid so set it as the pool
                newTopicPool = response;
            }

            // Update the existing topics
            if (!(updatedTopicPool.returnItems() == null || updatedTopicPool.returnItems().isEmpty())) {
                final RESTTopicCollectionV1 response = client.updateJSONTopics(expandString, updatedTopicPool);
                // Check that the response isn't empty (ie failed)
                if (response == null) return false;
                if (response.returnItems() == null) return false;
                // The response is valid so set it as the pool
                updatedTopicPool = response;
            }
            initialised = true;
            return true;
        } catch (Exception e) {
            log.error(ExceptionUtilities.getStackTrace(e));
            return false;
        }
    }

    /**
     * Initialises a content spec topic using the REST topics that exist
     * within this pool. The topic pool must be saved and initialised before
     * this call will work.
     *
     * @param specTopic
     * @return
     */
    public SpecTopic initialiseFromPool(final SpecTopic specTopic) {
        if (initialised) {
            if (newTopicPool.returnItems() != null && !newTopicPool.returnItems().isEmpty()) {
                for (final RESTTopicV1 topic : newTopicPool.returnItems()) {
                    if (ComponentBaseRESTEntityWithPropertiesV1.returnProperty(topic, CSConstants.CSP_PROPERTY_ID) != null) {
                        if (ComponentBaseRESTEntityWithPropertiesV1.returnProperty(topic, CSConstants.CSP_PROPERTY_ID).getValue().equals(
                                Integer.toString(specTopic.getLineNumber()))) {
                            specTopic.setDBId(topic.getId());
                            specTopic.setId(Integer.toString(topic.getId()));
                            cleanSpecTopicWhenCreatedOrUpdated(specTopic);
                            return specTopic;
                        }
                    }
                }
            }
            if (updatedTopicPool.returnItems() != null && !updatedTopicPool.returnItems().isEmpty()) {
                for (final RESTTopicV1 topic : updatedTopicPool.returnItems()) {
                    if (ComponentBaseRESTEntityWithPropertiesV1.returnProperty(topic, CSConstants.CSP_PROPERTY_ID) != null) {
                        if (ComponentBaseRESTEntityWithPropertiesV1.returnProperty(topic, CSConstants.CSP_PROPERTY_ID).getValue().equals(
                                Integer.toString(specTopic.getLineNumber()))) {
                            specTopic.setDBId(topic.getId());
                            specTopic.setId(Integer.toString(topic.getId()));
                            cleanSpecTopicWhenCreatedOrUpdated(specTopic);
                            return specTopic;
                        }
                    }
                }
            }
        }
        return specTopic;
    }

    /**
     * Cleans a SpecTopic to reset any content that should be removed in a post processed content spec.
     *
     * @param specTopic
     */
    private void cleanSpecTopicWhenCreatedOrUpdated(final SpecTopic specTopic) {
        specTopic.setSourceUrls(new ArrayList<String>());
        specTopic.setDescription(null);
        specTopic.setTags(new ArrayList<String>());
        specTopic.setRemoveTags(new ArrayList<String>());
        specTopic.setAssignedWriter(null);
        specTopic.setType(null);
    }

    /**
     * Checks to see if the topic pool has been saved and initialised against
     * the REST Interface.
     *
     * @return True if the topics have been saved and initialised.
     */
    public boolean isInitialised() {
        return initialised;
    }

    /**
     * Checks to see if the topic pool is empty.
     *
     * @return True if the pool is empty otherwise false.
     */
    public boolean isEmpty() {
        return newTopicPool.returnItems() == null ? true : newTopicPool.returnItems().isEmpty();
    }

    /**
     * Rolls back any new topics that were created. Since existing topics are stored
     * in revision data when edited we can't roll back that data properly.
     */
    public void rollbackPool() {
        if (newTopicPool.returnItems() == null || newTopicPool.returnItems().isEmpty()) return;
        final PathSegment path = new PathSegmentImpl("ids", false);
        for (final RESTTopicV1 topic : newTopicPool.returnItems()) {
            path.getMatrixParameters().putSingle(topic.getId().toString(), "");
        }
        try {
            client.deleteJSONTopics(path, "");
            initialised = false;
        } catch (Exception e) {
            log.error(e);
        }

    }

}
