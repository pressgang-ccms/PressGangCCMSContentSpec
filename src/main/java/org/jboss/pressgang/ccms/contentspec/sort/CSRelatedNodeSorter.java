package org.jboss.pressgang.ccms.contentspec.sort;

import java.util.Comparator;

import org.jboss.pressgang.ccms.wrapper.CSRelatedNodeWrapper;

public class CSRelatedNodeSorter implements Comparator<CSRelatedNodeWrapper> {

    @Override
    public int compare(CSRelatedNodeWrapper relatedNode, CSRelatedNodeWrapper relatedNode2) {
        if (relatedNode.getRelationshipSort() == null && relatedNode2.getRelationshipSort() != null) {
            return 1;
        } else if (relatedNode2.getRelationshipSort() == null) {
            return 0;
        } else if (relatedNode.getRelationshipSort() > relatedNode2.getRelationshipSort()) {
            return 1;
        } else if (relatedNode.getRelationshipSort() < relatedNode2.getRelationshipSort()) {
            return -1;
        } else {
            return 0;
        }
    }
}
