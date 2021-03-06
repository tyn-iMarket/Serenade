package com.tynmarket.serenade.model.util;

import android.net.Uri;

/**
 * Created by tynmarket on 2018/02/03.
 */

public class TwitterUtil {
    private static final String TWITTER_URL = "https://twitter.com/";

    public static Uri profileUri(String screenName) {
        return Uri.parse(profileUrl(screenName));
    }

    public static Uri tweetUri(String screenName, String idStr) {
        return Uri.parse(profileUrl(screenName) + "/status/" + idStr);
    }

    public static Uri listUri(String screenName) {
        return Uri.parse(profileUrl(screenName) + "/lists");
    }

    public static Uri newTweetUri() {
        return Uri.parse(TWITTER_URL + "intent/tweet");
    }

    public static Uri notificationUri() {
        return Uri.parse(TWITTER_URL + "i/notifications");
    }

    public static Uri searchUri() {
        return Uri.parse(TWITTER_URL + "search?q=a");
    }

    public static Uri messageUri() {
        return Uri.parse("https://mobile.twitter.com/messages");
    }

    public static Uri followUri(String screenName) {
        return Uri.parse(profileUrl(screenName) + "/following");
    }

    public static Uri followerUri(String screenName) {
        return Uri.parse(profileUrl(screenName) + "/followers");
    }

    public static Uri hashTagUri(String hashTag) {
        return Uri.parse(TWITTER_URL + "hashtag/" + hashTag + "?src=hash");
    }

    public static Uri replyUri(String idStr) {
        return Uri.parse(TWITTER_URL + "intent/tweet?in_reply_to=" + idStr);
    }

    private static String profileUrl(String screenName) {
        return TWITTER_URL + screenName;
    }
}
