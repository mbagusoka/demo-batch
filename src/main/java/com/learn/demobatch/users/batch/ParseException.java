package com.learn.demobatch.users.batch;

public class ParseException extends RuntimeException {

    public ParseException(String message, Throwable e) {
        super(message, e);
    }
}
