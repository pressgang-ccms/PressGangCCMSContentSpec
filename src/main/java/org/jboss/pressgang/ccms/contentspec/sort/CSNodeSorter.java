package org.jboss.pressgang.ccms.contentspec.sort;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;

public class CSNodeSorter {
    private final HashMap<CSNodeWrapper, Node> nodes;

    public CSNodeSorter(final HashMap<CSNodeWrapper, Node> nodes) {
        this.nodes = nodes;
    }

    public static LinkedHashMap<CSNodeWrapper, Node> sortMap(Map<CSNodeWrapper, Node> map) {
        // If the map is empty then just return an empty map
        if (map.isEmpty()) return new LinkedHashMap<CSNodeWrapper, Node>(map);

        final LinkedHashMap<CSNodeWrapper, Node> retValue = new LinkedHashMap<CSNodeWrapper, Node>();
        final LinkedList<Map.Entry<CSNodeWrapper, Node>> sortIndex = new LinkedList<Map.Entry<CSNodeWrapper, Node>>();

        /*
         * Note: To sort the map we have to use the next node references to create this. Since we also don't know the initial node,
         * we have to work in reverse. So we initially find the last node and then work backwards to until we get the initial node.
         */

        // Find the last node in the map
        Map.Entry<CSNodeWrapper, Node> nodeEntry = findLastEntry(map);

        // Add the initial entry to the linked hash map
        sortIndex.add(nodeEntry);

        // Add the following entries to the map
        while ((nodeEntry = findEntry(map, nodeEntry.getKey().getId())) != null) {
            sortIndex.addFirst(nodeEntry);
        }

        // Now create the map since the we have order
        for (final Map.Entry<CSNodeWrapper, Node> sortKey : sortIndex) {
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
    private static Map.Entry<CSNodeWrapper, Node> findEntry(Map<CSNodeWrapper, Node> map, Integer id) {
        if (id == null) return null;

        for (final Map.Entry<CSNodeWrapper, Node> entry : map.entrySet()) {
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
    private static Map.Entry<CSNodeWrapper, Node> findLastEntry(Map<CSNodeWrapper, Node> map) {
        Map.Entry<CSNodeWrapper, Node> nodeEntry = null;

        // Find the initial entry
        for (final Map.Entry<CSNodeWrapper, Node> entry : map.entrySet()) {
            if (entry.getKey().getNextNode() == null) {
                nodeEntry = entry;
                break;
            }
        }

        return nodeEntry;
    }
}
