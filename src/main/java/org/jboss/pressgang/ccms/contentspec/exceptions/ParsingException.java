/*
  Copyright 2011-2014 Red Hat

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec.exceptions;

/**
 * ParsingException class to be thrown when a Content Specification has incorrect syntax.
 */
public class ParsingException extends Exception {

    private static final long serialVersionUID = 6946236720289332910L;

    /**
     *
     */
    public ParsingException() {
    }

    /**
     * @param message
     */
    public ParsingException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ParsingException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }

}
