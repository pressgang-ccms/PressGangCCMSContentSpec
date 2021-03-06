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

package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.pressgang.ccms.contentspec.ITopicNode;
import org.jboss.pressgang.ccms.provider.DataProviderFactory;
import org.jboss.pressgang.ccms.provider.ServerSettingsProvider;
import org.jboss.pressgang.ccms.provider.TopicProvider;
import org.jboss.pressgang.ccms.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.CollectionWrapper;

/**
 * A fairly simple container class to hold a set of topics that need to be updated or created
 * using the Provider Abstraction API. By using the topic pool it allows for all the topics to be updated or created
 * in the smallest amount of calls.
 *
 * @author lnewson
 */
public class TopicPool {
    private static final Logger log = Logger.getLogger(TopicPool.class);

    private CollectionWrapper<TopicWrapper> newTopicPool;
    private CollectionWrapper<TopicWrapper> updatedTopicPool;
    private final TopicProvider topicProvider;
    private final Integer cspIdPropertyTagId;
    private boolean initialised = false;

    public TopicPool(final DataProviderFactory providerFactory) {
        this.topicProvider = providerFactory.getProvider(TopicProvider.class);
        newTopicPool = topicProvider.newTopicCollection();
        updatedTopicPool = topicProvider.newTopicCollection();
        cspIdPropertyTagId = providerFactory.getProvider(ServerSettingsProvider.class).getServerSettings().getEntities().getCspIdPropertyTagId();
    }

    /**
     * Add a topic that is to be created to the topic pool.
     *
     * @param topic The topic to be created.
     */
    public void addNewTopic(final TopicWrapper topic) {
        newTopicPool.addItem(topic);
    }

    /**
     * Add a topic that is to be updated to the topic pool.
     *
     * @param topic The topic to be updated.
     */
    public void addUpdatedTopic(final TopicWrapper topic) {
        updatedTopicPool.addItem(topic);
    }

    /**
     * Saves all the topics in the pool to the database using the REST API.
     *
     * @return True if all the topics in the pool were saved successfully,
     *         otherwise false.
     */
    public boolean savePool() {
        if (newTopicPool.isEmpty() && updatedTopicPool.isEmpty()) return true;
        try {
            // Save the new topics
            if (!newTopicPool.isEmpty()) {
                final CollectionWrapper<TopicWrapper> response = topicProvider.createTopics(newTopicPool);
                // Check that the response isn't empty (ie failed)
                if (response == null) return false;
                if (response.getItems() == null) return false;
                // The response is valid so set it as the pool
                newTopicPool = response;
            }

            // Update the existing topics
            if (!updatedTopicPool.isEmpty()) {
                final CollectionWrapper<TopicWrapper> response = topicProvider.updateTopics(updatedTopicPool);
                // Check that the response isn't empty (ie failed)
                if (response == null) return false;
                if (response.getItems() == null) return false;
                // The response is valid so set it as the pool
                updatedTopicPool = response;
            }
            initialised = true;
            return true;
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }

    /**
     * Initialises a content spec topic using the REST topics that exist
     * within this pool. The topic pool must be saved and initialised before
     * this call will work.
     *
     * @param topicNode
     * @return
     */
    public ITopicNode initialiseFromPool(final ITopicNode topicNode) {
        if (initialised) {
            if (newTopicPool != null && !newTopicPool.isEmpty()) {
                for (final TopicWrapper topic : newTopicPool.getItems()) {
                    if (topic.getProperty(cspIdPropertyTagId) != null) {
                        if (topic.getProperty(cspIdPropertyTagId).getValue().equals(topicNode.getUniqueId())) {
                            topicNode.setId(Integer.toString(topic.getId()));
                            return topicNode;
                        }
                    }
                }
            }
            if (updatedTopicPool != null && !updatedTopicPool.isEmpty()) {
                for (final TopicWrapper topic : updatedTopicPool.getItems()) {
                    if (topic.getProperty(cspIdPropertyTagId) != null) {
                        if (topic.getProperty(cspIdPropertyTagId).getValue().equals(topicNode.getUniqueId())) {
                            topicNode.setId(Integer.toString(topic.getId()));
                            return topicNode;
                        }
                    }
                }
            }
        }
        return topicNode;
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
        return (newTopicPool == null ? true : newTopicPool.isEmpty()) && (updatedTopicPool == null ? true : updatedTopicPool.isEmpty());
    }

    /**
     * Rolls back any new topics that were created. Since existing topics are stored
     * in revision data when edited we can't roll back that data properly.
     */
    public void rollbackPool() {
        if (newTopicPool == null || newTopicPool.isEmpty()) return;
        final List<Integer> topicIds = new ArrayList<Integer>();
        for (final TopicWrapper topic : newTopicPool.getItems()) {
            topicIds.add(topic.getTopicId());
        }
        try {
            topicProvider.deleteTopics(topicIds);
            initialised = false;
        } catch (Exception e) {
            log.error("An error occurred while trying to rollback the Topic Pool", e);
        }

    }

}
