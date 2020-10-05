package com.kookyapps.gpstankertracking.Utils;

public class URLs {
    public static final String BASE_URL= "http://ec2-15-206-253-136.ap-south-1.compute.amazonaws.com:8081/api/tanker/";
    public static final String SOCKET_URL="http://ec2-15-206-253-136.ap-south-1.compute.amazonaws.com:8081?token=";
    public static final String SIGN_IN_URL="signin";
    public static final String SIGN_OUT_URL="signout";
    public static final String REQUEST_LIST="pending-booking-list";
    public static final String BOOKING_LIST="booking-list";
    public static final String BOOKING_ACCEPTED="booking/accept/";
    public static final String BOOKING_START="booking/start/";
    public static final String BOOKING_BY_ID="booking/";
    public static final String NOTIFICATION_LIST="notification";
    public static final String READ_NOTIFICATIONS="/notification/read/";
    public static final String BOOKING_END ="booking/end/";
    public static final String TRIP_DETAILS="trips";
    public static final String NOTIFICATION_COUNT="/notification/unread-count";
    public static final String CANCELLED_TRIP_DETAILS="cancelled-trips";
    public static final String LANGUAGE_CHANGED="settings";
    public static final String UPDATE_LOCATION="update-location";
    public static final String CHECK_VALIDITY="check-validity";
    public static final String LOW_BATTERY="notification/battery";
    public static final String RESEND_OTP="booking/resend-otp/";
}
