package org.jboss.pressgang.ccms.contentspec.sort;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;

public class CSNodeSorter {
    private final HashMap<CSNodeWrapper, Node> nodes;

    public CSNodeSorter(final HashMap<CSNodeWrapper, Node> nodes) {
        this.nodes = nodes;
    }

    public static LinkedHashMap<CSNodeWrapper, Node> sortMap(Map<CSNodeWrapper, Node> map) {
        final LinkedHashMap<CSNodeWrapper, Node> retValue = new LinkedHashMap<CSNodeWrapper, Node>();
        Map.Entry<CSNodeWrapper, Node> nodeEntry = null;

        // Find the initial entry
        for (final Map.Entry<CSNodeWrapper, Node> entry : map.entrySet()) {
            if (entry.getKey().getPreviousNodeId() == null) {
                nodeEntry = entry;
                break;
            }
        }

        // Add the initial entry to the linked hash map
        retValue.put(nodeEntry.getKey(), nodeEntry.getValue());

        // Add the following entries to the map
        while ((nodeEntry = findEntry(map, nodeEntry.getKey().getNextNodeId())) != null) {
            retValue.put(nodeEntry.getKey(), nodeEntry.getValue());
        }

        return retValue;
    }

    /**
     * Find the entry specified by a node id.
     *
     * @return The Entry value where the key matches the node id otherwise null.
     */
    private static Map.Entry<CSNodeWrapper, Node> findEntry(Map<CSNodeWrapper, Node> map, Integer id) {
        if (id == null) return null;

        for (final Map.Entry<CSNodeWrapper, Node> entry : map.entrySet()) {
            if (entry.getKey().getId().equals(id)) {
                return entry;
            }
        }

        return null;
    }
}