package devs.mrp.coolyourturkey.exceptions;

public class InvalidViewTypeException extends Exception{

    public InvalidViewTypeException() {
        super();
    }

    public InvalidViewTypeException(String msg) {
        super(msg);
    }

    public InvalidViewTypeException(Throwable cause) {
        super(cause);
    }

    public InvalidViewTypeException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
