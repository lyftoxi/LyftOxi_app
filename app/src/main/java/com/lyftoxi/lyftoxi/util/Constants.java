package com.lyftoxi.lyftoxi.util;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class Constants {

    public static final String DATE_TIME_FORMAT_WITH_TIME_ZONE = "dd-MM-yyyy'T'HH:mm zzzz";
    public static final String SIMPLE_DATE_FORMAT = "dd-MM-yyyy";
    public static final String DATE_TIME_FORMAT_12HR_FORMAT = "dd-MM-yyyy h:mm a";
    public static final String DATE_FORMAT_WITH_DAY_FORMAT = "EEE','dd-MMM";
    public static final String TIME_FORMAT = "h:mm a";
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String MOBIL_PATTERN = "^[0-9]{10}$";
    public static final String PIN_PATTERN = "^[0-9]{6}$";
    public static final String CAR_NUMBER_PATTERN = "^[A-Z]{2}[\\s|.|-]*[0-9]+[\\s|.|-]*[A-Z]*[\\s|.|-]*[0-9]{4}$";
    public static final Map<String,String> genderLookup= ImmutableMap.of("M", "Male", "F", "Female");
    public static final int RIDE_REPEAT_MAX_NO_DAYS = 4;
    public static final String HTTP_HEADER_USER_ID="userId";
    public static final String APP_PACAKGE = "net.one97.paytm";
    public static final String APP_LINK = "https://paytm.com/";
    public static final String CURRENT_PROMO_CODE = "PAYTM01";
}
