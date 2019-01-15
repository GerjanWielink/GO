package GoUtilities.Exceptions;

public class KoException extends InvalidMoveException {
    public KoException() {
        super();
    }

    public KoException(String message){
        super(message);
    }
}
