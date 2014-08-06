package com.eastbay.performancezone.print.Response;

/**
 * Created with IntelliJ IDEA.
 * User: bob.gause
 * Date: 5/18/14
 * Time: 3:17 PM
 * SDG
 */
public class Message {
    private MessageLevel messageLevel;
    private String message;

    public static Message createError(String msg) {
        Message retVal = new Message();
        retVal.setMessageLevel(MessageLevel.Error);
        retVal.setMessage(msg);
        return retVal;
    }

    public static Message createWarning(String msg) {
        Message retVal = new Message();
        retVal.setMessageLevel(MessageLevel.Warning);
        retVal.setMessage(msg);
        return retVal;
    }

    public MessageLevel getMessageLevel() {
        return messageLevel;
    }

    public void setMessageLevel(MessageLevel messageLevel) {
        this.messageLevel = messageLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public enum MessageLevel {
        Error,
        Warning;
    }
}
