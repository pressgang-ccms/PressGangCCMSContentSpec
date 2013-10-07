package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.jboss.pressgang.ccms.contentspec.exceptions.ParsingException;
import org.jboss.pressgang.ccms.contentspec.sort.RevisionNodeSort;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocBookUtilities extends org.jboss.pressgang.ccms.utils.common.DocBookUtilities {
    public static void mergeRevisionHistories(final Document mainDoc, final Document mergeDoc) throws ParsingException {
        final NodeList revhistories = mainDoc.getElementsByTagName("revhistory");
        if (revhistories.getLength() > 0) {
            final Element revhistory = (Element) revhistories.item(0);

            // Get the revision nodes
            final NodeList docRevisions = mainDoc.getElementsByTagName("revision");
            final NodeList additionalDocRevisions = mergeDoc.getElementsByTagName("revision");
            final List<Element> revisionNodes = new LinkedList<Element>();
            for (int i = 0; i < docRevisions.getLength(); i++) {
                revisionNodes.add((Element) docRevisions.item(i));
            }
            for (int i = 0; i < additionalDocRevisions.getLength(); i++) {
                revisionNodes.add((Element) additionalDocRevisions.item(i));
            }

            // Sort the revisions
            Collections.sort(revisionNodes, new RevisionNodeSort());

            // Insert the additional revisions
            final ListIterator<Element> listIterator = revisionNodes.listIterator(revisionNodes.size());

            Node prevNode = null;
            while (listIterator.hasPrevious()) {
                final Element revisionNode = listIterator.previous();

                // The node is from the additional doc
                if (!revisionNode.getOwnerDocument().equals(mainDoc)) {
                    final Node importedNode = mainDoc.importNode(revisionNode, true);

                    if (prevNode == null) {
                        revhistory.appendChild(importedNode);
                    } else {
                        prevNode.getParentNode().insertBefore(importedNode, prevNode);
                    }
                    prevNode = importedNode;
                } else {
                    prevNode = revisionNode;
                }
            }
        } else {
            throw new ParsingException("The main document has no <revhistory> element and therefore can't be merged.");
        }
    }
}
