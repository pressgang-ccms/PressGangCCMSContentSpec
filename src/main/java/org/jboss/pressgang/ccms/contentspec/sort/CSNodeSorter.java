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

package org.jboss.pressgang.ccms.contentspec.sort;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;

public class CSNodeSorter {
    public static <T extends Node> LinkedHashMap<CSNodeWrapper, T> sortMap(Map<CSNodeWrapper, T> map) {
        // If the map is empty then just return an empty map
        if (map.isEmpty()) return new LinkedHashMap<CSNodeWrapper, T>(map);

        final LinkedHashMap<CSNodeWrapper, T> retValue = new LinkedHashMap<CSNodeWrapper, T>();
        final LinkedList<Map.Entry<CSNodeWrapper, T>> sortIndex = new LinkedList<Map.Entry<CSNodeWrapper, T>>();

        /*
         * Note: To sort the map we have to use the next node references to create this. Since we also don't know the initial node,
         * we have to work in reverse. So we initially find the last node and then work backwards to until we get the initial node.
         */

        // Find the last node in the map
        Map.Entry<CSNodeWrapper, T> nodeEntry = findLastEntry(map);

        // Add the initial entry to the linked hash map
        sortIndex.add(nodeEntry);

        // Add the following entries to the map
        while ((nodeEntry = findEntry(map, nodeEntry.getKey().getId())) != null) {
            sortIndex.addFirst(nodeEntry);
        }

        // Now create the map since the we have order
        for (final Map.Entry<CSNodeWrapper, T> sortKey : sortIndex) {
            retValue.put(sortKey.getKey(), sortKey.getValue());
        }

        return retValue;
    }

    /**
     * Find the entry, how has a next node that matches a specified by a node id.
     *
     * @param map The map to find the entry from.
     * @param id  The next node ID of to find in the map
     * @return The Entry value where the key matches the node id otherwise null.
     */
    private static <T extends Node> Map.Entry<CSNodeWrapper, T> findEntry(Map<CSNodeWrapper, T> map, Integer id) {
        if (id == null) return null;

        for (final Map.Entry<CSNodeWrapper, T> entry : map.entrySet()) {
            if (entry.getKey().getNextNode() != null && entry.getKey().getNextNode().getId().equals(id)) {
                return entry;
            }
        }

        return null;
    }

    /**
     * Finds the initial entry for the unordered map.
     *
     * @param map The unordered map.
     * @return The initial entry to start sorting the map from.
     */
    private static <T extends Node> Map.Entry<CSNodeWrapper, T> findLastEntry(Map<CSNodeWrapper, T> map) {
        Map.Entry<CSNodeWrapper, T> nodeEntry = null;

        // Find the initial entry
        for (final Map.Entry<CSNodeWrapper, T> entry : map.entrySet()) {
            if (entry.getKey().getNextNode() == null) {
                nodeEntry = entry;
                break;
            }
        }

        return nodeEntry;
    }
}
