package org.litesoft.urlListManager.persistence;

import java.io.IOException;
import java.util.concurrent.Callable;

public class IoRuntimeException extends RuntimeException {
    public static <T> T with( String context, Callable<T> callable ) throws IoRuntimeException {
        try {
            return callable.call();
        }
        catch ( Exception e ) {
            throw from( context, e );
        }
    }

    public static IoRuntimeException from( String context, Exception e ) {
        if ( e == null ) {
            return null;
        }
        if ( e instanceof IOException ) {
            return new IoRuntimeException( "IO error with '" + context + "': " + e.getMessage(), e );
        }
        return new IoRuntimeException( "Error with '" + context + "' during IO: " + e.getMessage(), e );
    }

    @SuppressWarnings("unused")
    public IoRuntimeException( String message ) {
        super( message );
    }

    public IoRuntimeException( String message, Throwable cause ) {
        super( message, cause );
    }
}
