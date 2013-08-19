package org.jboss.pressgang.ccms.contentspec.exceptions;

public class ValidationException extends Exception {
    private static final long serialVersionUID = -757150905453304194L;

    public ValidationException(final String message) {
        super(message);
    }

    public ValidationException(final Throwable cause) {
        super(cause);
    }

    public ValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
