package com.kookyapps.gpstankertracking.Utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.kookyapps.gpstankertracking.R;

import java.util.ArrayList;

public class Constants {

    public static boolean isTripOngoing = false;
    public static boolean isPathSnapped = false;
    public static String ongoingBookingId = null;
    public static ArrayList<LatLng> travelled_path = null;
    public static ArrayList<Location> travelled_path1 = null;


    public static final String REQUEST_INIT= "request_init";
    public static final String NOTIFICATION_INIT= "notification_init";
    public static final String SPLASH_INIT= "splash_init";
    //public static final String TRIP_INIT= "trip_init";
    //public static final String COMPLETED_CALL="Completed";
    public static final String TRIP_END_IMG="trip_end_img";
    public static final String BOOKING_INIT= "booking_init";
    public static final String COMPLETED_TRIP = "completed";
    public static final String CANCELLED_TRIP = "cancelled";




    public static final String SHARED_PREF_CAPTURE_IMAGE_BEFORE = "pref_capture_before";
    public static final String SHARED_PREF_LOGIN_TAG="login_pref";
    public static final String SHARED_PREF_BOOKING_TAG="booking_pref";
    public static final String SHARED_PREF_TRIP_TAG="trip_pref";
    public static final String SHARED_PREF_ONGOING_TAG="ongoing_pref";
    public static final String SHARED_PREF_IMAGE_TAG="image_tag";
    public static final String CAPTURE_IMAGE = "tanker_img";



    public static final String ONGOING_CALL="Ongoing";
    public static final String PENDING_CALL="Pending";


    public static final int MULTIPLE_PERMISSIONS_REQUEST_CODE =1101;
    public static final String SHARED_PREF_NOTICATION_TAG = "pref_notification";
    public static final String SHARED_NOTIFICATION_COUNT_KEY = "notification_count";
    public static final String SHARED_NOTIFICATION_UPDATE_KEY  = "notification_update";
    public static final String SHARED_END_IMAGE_KEY="image_key";
    public static final String SHARED_ONGOING_TRAVELLED_PATH="ongoing_travelled_path";
    public static final String SHARED_ONGOING_PATH_SIZE="ongoing_path_size";
    public static final String SHARED_ONGOING_BOOKING_ID="ongoing_booking_id";
    public static final String SHARED_ONGOING_DRIVER_ID="ongoing_driver_id";


    public static final String HINDI_LANGUAGE  = "hi";
    public static final String ENGLISH_LANGUAGE  = "en";


    public static final String IS_ONLINE="1";
    public static final String IS_OFFLINE="0";



    public static final String ABORTED_CALL="Aborted";

    public static final String NOTIFICATION_PAGE_TITLE="Notifications";
    public static final String OTP_PAGE_TITLE="Enter OTP";
    public static final String TRIP_COMPLETE_PAGE_TITLE="Trip Complete";
    public static final String TRIP_DETAILS_PAGE_TITLE="Trip Details";


    public static final String TANKERDETAIL_PAGE_TITLE="Notifications";
    public static final String MAP_PAGE_TITLE="Map1";

    public static final int PICKUP_ACTIVITY_PICKUP_LOCATION_REQUEST_CODE=1011;
    public static final int BOOKINGFORM_ACTIVITY_PICKUP_ACTIVITY_REQUEST_CODE=1012;
    public static final int BOOKINGFORM_ACTIVITY_DROP_ACTIVITY_REQUEST_CODE=1013;

    public static final String PICKUP_LOCATION_INTENT_DATA_TITLE="pickuplocations";

    public static final String GOOGLE_API_KEY_MAPS= "AIzaSyDzV0TMTS1RXLeT3e4dBxjP6yQcUbzL02Y";






}
