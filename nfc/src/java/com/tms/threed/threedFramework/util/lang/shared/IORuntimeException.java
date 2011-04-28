package com.tms.threed.threedFramework.util.lang.shared;

import java.io.IOException;

public class IORuntimeException extends RuntimeException {

    public IORuntimeException() {
    }

    public IORuntimeException(IOException cause) {
        super(cause);
    }

    public IORuntimeException(String message) {
        super(message);
    }

    public IORuntimeException(String message, IOException cause) {
        super(message, cause);
    }
}

