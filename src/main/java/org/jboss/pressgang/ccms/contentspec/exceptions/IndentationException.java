/*
  Copyright 2011-2014 Red Hat

  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec.exceptions;

/**
 * IndentationException class to be thrown when a Content Specification has incorrect indentation.
 */
public class IndentationException extends Exception {

    private static final long serialVersionUID = -4667574815926735224L;

    /**
     *
     */
    public IndentationException() {
    }

    /**
     * @param message
     */
    public IndentationException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public IndentationException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public IndentationException(String message, Throwable cause) {
        super(message, cause);
    }

}
