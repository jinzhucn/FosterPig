package com.minlu.fosterpig;

import android.graphics.Color;

/**
 * Created by user on 2016/7/27.
 */
public class StringsFiled {

    public final static String IP_ADDRESS_PREFIX = "address";

    public final static String ACTIVITY_TITLE = "activity_title";

    public final static String OPEN_FRAGMENT_BUNDLE_KEY = "open_fragment_bundle_key";

    public final static String MAIN_TO_WARN_AMMONIA = "main_to_warn_ammonia";

    public final static String MAIN_TO_WARN_TEMPERATURE = "main_to_warn_temperature";

    public final static String MAIN_TO_WARN_HUMIDITY = "main_to_warn_humidity";

    public final static String TAG_OPEN_ALL_SITE_FRAGMENT = "all_site";

    public final static String TAG_OPEN_WARN_INFORMATION_FRAGMENT = "warn_information";

    public final static String TAG_OPEN_SURE_WARN_FRAGMENT = "sure_warn";

    public final static String INFORM_WARN = "inform_warn";

    public final static String LOGIN_USER = "login_user";

    public static final String IS_ALLOW_SOUND_PLAY = "is_allow_sound_play";

    public static final String IS_ALLOW_SOUND_PLAY_TIME = "is_allow_sound_play_time";

    public static final String MEDIA_IS_PLAYING = "media_is_playing";

    public final static String IS_AUTO_LOGIN = "is_auto_login";

    public static final String ALARM_SERVICE_ALREADY_OPEN = "alarm_service_already_open";

    public final static String SEARCH_HISTORY = "search_history";

    public final static String SEARCH_JSON_RESULT = "search_json_result";

    public final static String SEARCH_RESULT_SEAT_NUMBER = "search_result_seat_number";

    public final static String SAVE_USERNAME = "mUser";

    public final static int SERVER_OUTAGE = 0;// 服务器宕机

    public final static int SERVER_THROW = 1;// 服务器异常

    public final static int SERVER_SEND_JSON = 2;// 服务器发送了Json

    public final static int MAIN_TO_WARN_VALUE_AMMONIA = 0;// 氨气报警信息

    public final static int MAIN_TO_WARN_VALUE_TEMPERATURE = 1;// 温度报警信息

    public final static int MAIN_TO_WARN_VALUE_HUMIDITY = 2;// 湿度报警信息

    public final static int SELECT_WARN_INFORMATION_TAB = 3;// 所有的报警信息

    public final static int SELECT_ALL_SITE_TAB = 4;// 所有站点信息

    public final static int SELECT_SURE_WARN_INFORMATION_TAB = 5;// 已经确认的报警信息

    public final static int SWIPE_REFRESH_FIRST_ROUND_COLOR = Color.RED;

    public final static int SWIPE_REFRESH_SECOND_ROUND_COLOR = Color.YELLOW;

    public final static int SWIPE_REFRESH_THIRD_ROUND_COLOR = Color.BLUE;

    public final static int SWIPE_REFRESH_BACKGROUND = Color.GREEN;

    public final static int OBSERVER_MEDIA_PLAYER_PAUSE = 0;

    public final static int OBSERVER_MEDIA_PLAYER_IS_PLAYING = 1;
}
