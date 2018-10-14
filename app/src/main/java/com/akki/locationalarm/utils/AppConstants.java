package com.akki.locationalarm.utils;

/**
 * * Created by v-akhilesh.chaudhary on 06/10/2018.
 */
public class AppConstants {

    public static final String BASE_URL = "http://125.16.74.160:30208";
    public static final String LOGIN_URL_POST_STRING = "/InterviewManagement";
    public static final String LOGIN_METHOD = "/login";
    public static final String LOCATION_HISTORY_METHOD = "/locations";
    public static final String LOCATION_SAVE_METHOD = "/location";

    public static final String USER_LOGIN_TOKEN_KEY = "user_login_token_key";
    public static final String USER_SHARED_FILE_KEY = "user_shared_file_key";

    public static final int READ_TIMEOUT_MILLIES = 6000;
    public static final int CONNECTION_TIMEOUT_MILLIES = 6000;

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final long UPDATE_INTERVAL = 10000;
    public static final long FASTEST_INTERVAL = 5000; // = 5 seconds

    public static final String LOCATION_HISTORY_RESULT = "location_history_result";

    public static final int ALL_PERMISSIONS_RESULT = 1000;
    public static final int LOCATION_RESULT_CODE = 2;
    public static final int RINGTONE_REQUEST_CODE = 100;
    public static final int REQUEST_CURRENT_LOCATION_PERMISSION_CODE = 101;
    public static final int PLACE_PICKER_REQ_CODE = 102;
    public static final int ENABLE_GPS_CODE = 103;

    public static final String ALARM_RINGTONE_KEY = "alarm_ringtone_key";
    public static final String ALARM_TITLE_KEY = "alarm_title_key";
    public static final String ALARM_DESCRIPTION_KEY = "alarm_description_key";
    public static final String ALARM_ISREPEAT_KEY = "alarm_isRepeat_key";
    public static final String ALARM_REPEAT_INTERVAL = "alarm_repeat_interval";
    public static final String ALARM_ISVIBRATE_KEY = "alarm_isVibrate_key";
    public static final String ALARM_TRIGGER_TIME_KEY = "alarm_trigger_time_key";
    public static final String ALARM_LOCATION_LATITUDE = "alarm_location_lat";
    public static final String ALARM_LOCATION_LONGITUDE = "alarm_location_long";
    public static final String ALARM_LOCATION_ALTITUDE = "alarm_location_alt";
    public static final String ALARM_STATUS_KEY = "alarm_status_key";

    public static final String TIMEOUT = "timeout";

    public static final int LOCATION_INTERVAL = 1000; //For debugging purpose only, In final make it 5000 or more
    public static final float LOCATION_DISTANCE = 10f;


    public interface ALARM_ACTION {
        public static String ACTION_START = "action_start";
        public static String ACTION_STOP = "action_stop";
        public static String ACTION_SNOOZE = "action_snooze";
        public static String ACTION_START_FOREGROUND = "action_start_foreground";
        public static String ACTION_STOP_FOREGROUND = "action.stop_foreground";
        public static String MAIN_ACTION = "action.main_foreground";
        public static String INIT_ACTION = "action.init_foreground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }


    public static enum SessionTimerState {
        UNKNOWN,
        STARTED,
        STOPPED;

        private SessionTimerState() {
        }
    }

}
