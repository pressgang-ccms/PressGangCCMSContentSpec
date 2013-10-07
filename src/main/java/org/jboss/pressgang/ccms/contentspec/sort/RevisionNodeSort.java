package org.jboss.pressgang.ccms.contentspec.sort;

import java.util.Comparator;

import org.jboss.pressgang.ccms.contentspec.structures.RevNumber;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RevisionNodeSort implements Comparator<Element> {

    @Override
    public int compare(Element revision1, Element revision2) {
        if (revision1 == null && revision2 == null) {
            return 0;
        }

        if (revision1 == revision2) {
            return 0;
        }

        if (revision1 == null) {
            return -1;
        }

        if (revision2 == null) {
            return 1;
        }

        final RevNumber revnumber1 = getRevnumber(revision1);
        final RevNumber revnumber2 = getRevnumber(revision2);

        if (revnumber1 == null && revnumber2 == null) {
            return 0;
        }

        if (revnumber1 == revnumber2) {
            return 0;
        }

        if (revnumber1 == null) {
            return -1;
        }

        if (revnumber2 == null) {
            return 1;
        }

        // Compare the revision
        return revnumber1.compareTo(revnumber2);
    }

    private RevNumber getRevnumber(final Element revision) {
        final NodeList revnumbers = revision.getElementsByTagName("revnumber");
        // There should only be one revnumber attribute
        if (revnumbers.getLength() > 0) {
            return new RevNumber(revnumbers.item(0).getTextContent());
        } else {
            return null;
        }
    }
}
