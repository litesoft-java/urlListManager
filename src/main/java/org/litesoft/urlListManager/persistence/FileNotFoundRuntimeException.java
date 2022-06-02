package org.litesoft.urlListManager.persistence;

public class FileNotFoundRuntimeException extends RuntimeException {
    public FileNotFoundRuntimeException( String message ) {
        super( message );
    }
}
