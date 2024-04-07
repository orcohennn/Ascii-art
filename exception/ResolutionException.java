package exception;

/**
 * The ResolutionException class represents an exception that is thrown when an invalid resolution
 * is encountered during a process that requires a resolution value.
 */
public class ResolutionException extends Exception {

    /**
     * Constructs a ResolutionException with the specified error message.
     *
     * @param msg The error message associated with the exception.
     */
    public ResolutionException(String msg) {
        super(msg);
    }
}
