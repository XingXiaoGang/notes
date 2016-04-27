package com.fenghuo.observer;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Created by 张少林
 * on 15-4-29 下午6:08
 * Email:zsl@kuba.bz
 * Android Studio
 * Function:
 */
public class NotifyInvokeException extends RuntimeException {

    private final Throwable invokeException;

    public NotifyInvokeException(Throwable invokeException) {
        this.invokeException = invokeException;
    }

    @Override
    public void printStackTrace(PrintStream err) {
        invokeException.printStackTrace(err);
    }

    @Override
    public String getMessage() {
        return invokeException.getMessage();
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return invokeException.getStackTrace();
    }

    @Override
    public String getLocalizedMessage() {
        return invokeException.getLocalizedMessage();
    }

    @Override
    public Throwable getCause() {
        return invokeException.getCause();
    }

    @Override
    public void printStackTrace() {
        invokeException.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintWriter err) {
        invokeException.printStackTrace(err);
    }

    @Override
    public String toString() {
        return invokeException.toString();
    }

}
