package org.jboss.pressgang.ccms.contentspec.exceptions;

public class BugLinkException extends RuntimeException {
    private static final long serialVersionUID = -8718891814610272666L;

    public BugLinkException(final String message) {
        super(message);
    }

    public BugLinkException(final Throwable cause) {
        super(cause);
    }

    public BugLinkException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
