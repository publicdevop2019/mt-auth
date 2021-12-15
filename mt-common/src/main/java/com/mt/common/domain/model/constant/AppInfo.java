package com.mt.common.domain.model.constant;

public class AppInfo {
    public static final String MT0_APP_NAME="access";
    public static final String MT1_APP_NAME="proxy";
    public static final String MT2_APP_NAME="profile";
    public static final String MT3_APP_NAME="mall";
    public static final String MT6_APP_NAME="payment";
    public static final String MT15_APP_NAME="saga";

    public static class EventName {
        public static final String MT3_MALL_NOTIFICATION ="MT3_MALL_NOTIFICATION";
        public static final String MT3_SKU_UPDATE_FAILED ="SKU_UPDATE_FAILED";
        public static final String MT2_ORDER_UPDATE_FAILED="ORDER_UPDATE_FAILED";
        public static final String MT2_CART_UPDATE_FAILED="CART_UPDATE_FAILED";
        public static final String MT6_PAYMENT_UPDATE_FAILED="PAYMENT_UPDATE_FAILED";
    }

    public static class MISC {
        public static final String SKU_CHANGE_DETAIL="SKU_CHANGE_DETAIL";
        public static final String ADMIN_OPT="ADMIN_OPT";
        public static final String STACK_TRACE="STACK_TRACE";
        public static final String MESSAGE="MESSAGE";
    }
}
