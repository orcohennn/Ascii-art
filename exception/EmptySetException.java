package exception;

/**
 * The EmptySetException class represents an exception that is thrown when an operation
 * is performed on an empty set and it is not allowed.
 */
public class EmptySetException extends Exception {

    /**
     * Constructs an EmptySetException with the specified error message.
     *
     * @param msg The error message associated with the exception.
     */
    public EmptySetException(String msg) {
        super(msg);
    }
}
