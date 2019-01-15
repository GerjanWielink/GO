package GoUtilities.Exceptions;

public class BeforeTurnException extends InvalidMoveException {
    public BeforeTurnException() {
        super();
    }

    public BeforeTurnException(String message) {
        super(message);
    }
}
