package org.jboss.pressgang.ccms.contentspec.exceptions;

public class InitializationException extends Exception {
    private static final long serialVersionUID = 40321298532271561L;

    public InitializationException(final String message) {
        super(message);
    }

    public InitializationException(final Throwable cause) {
        super(cause);
    }

    public InitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
