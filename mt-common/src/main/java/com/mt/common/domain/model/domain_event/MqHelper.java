package com.mt.common.domain.model.domain_event;

public class MqHelper {
    private static final String HANDLER = "handler";
    private static final String REPLY_EVENT = "_reply_event";
    private static final String _EVENT = "_event";
    private static final String CANCEL_ = "cancel_";

    public static String handlerOf(String eventName) {
        return String.join("_", eventName, HANDLER);
    }

    public static String handleCancelOf(String eventName) {
        return String.join("_", cancelOf(eventName), HANDLER);
    }

    public static String handleReplyCancelOf(String originalEventName) {
        return String.join("_", replyOf(cancelOf(originalEventName)), HANDLER);
    }

    public static String handleReplyOf(String originalEventName) {
        return String.join("_", replyOf(originalEventName), HANDLER);
    }

    public static String replyOf(String originalEventName) {
        return originalEventName.replace(_EVENT, REPLY_EVENT);
    }

    public static String replyCancelOf(String originalEventName) {
        return cancelOf(originalEventName).replace(_EVENT, REPLY_EVENT);
    }

    public static String cancelOf(String originalEventName) {
        return CANCEL_ + originalEventName;
    }
}
