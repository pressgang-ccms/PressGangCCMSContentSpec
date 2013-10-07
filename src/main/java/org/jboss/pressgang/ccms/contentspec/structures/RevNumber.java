package org.jboss.pressgang.ccms.contentspec.structures;

public class RevNumber implements Comparable<RevNumber> {
    private final Version version;
    private final Version release;

    public RevNumber(final String revnumber) {
        final String[] split = revnumber.split("-", 2);
        version = new Version(split[0]);
        if (split.length > 1) {
            release = new Version(split[1]);
        } else {
            release = null;
        }
    }

    public RevNumber(final Version version, final Version release) {
        this.version = version;
        this.release = release;
    }

    @Override
    public String toString() {
        if (release == null) {
            return version.toString();
        } else {
            return version.toString() + "-" + release.toString();
        }
    }

    @Override
    public int compareTo(final RevNumber revnumber) {
        if (this == revnumber) {
            return 0;
        }

        if (revnumber == null) {
            return 1;
        }

        if (version == null && revnumber.version == null) {
            return 0;
        }

        if (version == revnumber.version) {
            return 0;
        }

        if (version == null) {
            return -1;
        }

        if (revnumber.version == null) {
            return 1;
        }

        if (version.equals(revnumber.version)) {
            if (release == null && revnumber.release == null) {
                return 0;
            }

            if (release == revnumber.release) {
                return 0;
            }

            if (release == null) {
                return -1;
            }

            if (revnumber.release == null) {
                return 1;
            }

            if (release.equals(revnumber.release)) {
                return 0;
            } else {
                return release.compareTo(revnumber.release);
            }
        } else {
            return version.compareTo(revnumber.version);
        }
    }

    public Version getVersion() {
        return version;
    }

    public Version getRelease() {
        return release;
    }
}