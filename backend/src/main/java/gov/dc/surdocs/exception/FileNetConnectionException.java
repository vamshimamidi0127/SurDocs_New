package gov.dc.surdocs.exception;

public class FileNetConnectionException extends RuntimeException {

    public FileNetConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNetConnectionException(String message) {
        super(message);
    }
}
