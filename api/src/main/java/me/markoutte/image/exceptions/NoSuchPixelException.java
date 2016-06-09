package me.markoutte.image.exceptions;

public class NoSuchPixelException extends RuntimeException {

    public NoSuchPixelException() {
        super();
    }

    public NoSuchPixelException(String message) {
        super(message);
    }

    public NoSuchPixelException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchPixelException(Throwable cause) {
        super(cause);
    }

    protected NoSuchPixelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
