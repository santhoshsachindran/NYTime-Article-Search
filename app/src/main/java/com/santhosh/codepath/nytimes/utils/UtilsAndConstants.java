package com.santhosh.codepath.nytimes.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UtilsAndConstants {
    // TODO: Add own api key for testing purposes
    public static final String NYT_API_KEY = "";
    public static final String BASE_NYT_URL =
            "https://api.nytimes.com/svc/search/v2/articlesearch.json?";
    public static final String BASE_THUMBNAIL_URL = "http://static01.nyt.com/";
    public static final String QUERY_KEY = "q=";
    public static final String DEFAULT_SEARCH = "android?";
    public static final String API_KEY = "api-key=";
    public static final String FL_KEY = "fl=web_url,multimedia,headline,pub_date&";
    public static final String FQ_KEY = "fq=";
    public static final String ARTS = "Arts";
    public static final String FASHION = "Fashion Style";
    public static final String SPORTS = "Sports";


    public static final String SHARED_PREFS_NAME = "news_pref";
    public static final String START_DATE = "start_date";
    public static final String SORT = "sort";
    public static final String NEWS_DESK = "news_desk";
    public static final String PAGE = "page=";

    public static final String RESPONSE = "response";
    public static final String DOCS = "docs";
    public static final String WEB_URL = "web_url";
    public static final String MULTIMEDIA = "multimedia";
    public static final String SUBTYPE = "subtype";
    public static final String XLARGE = "xlarge";
    public static final String WIDE = "wide";
    public static final String URL = "url";
    public static final String HEADLINE = "headline";
    public static final String MAIN = "main";

    public static final int ONLY_TEXT = 0;
    public static final int TEXT_WITH_THUMBNAIL = 1;

    public static boolean networkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
