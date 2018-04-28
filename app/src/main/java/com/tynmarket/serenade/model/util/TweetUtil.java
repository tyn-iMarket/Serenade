package com.tynmarket.serenade.model.util;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.UrlEntity;
import com.tynmarket.serenade.R;
import com.tynmarket.serenade.model.SpeakerDeck;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by tyn-iMarket on 2018/01/22.
 */

public class TweetUtil {
    public static void loadImage(ImageView view, Tweet tweet) {
        String photoUrl = TweetUtil.photoUrl(tweet);
        if (photoUrl != null) {
            Glide.with(view.getContext()).load(photoUrl).into(view);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
            view.setImageDrawable(null);
        }
    }

    @Nullable
    private static String photoUrl(@Nullable Tweet tweet) {
        if (tweet == null) {
            return null;
        }

        List<MediaEntity> media = tweet.entities.media;
        if (media.size() == 0) {
            return null;
        }

        // TODO: stream function (API 24)
        MediaEntity entity = null;
        for (MediaEntity m : media) {
            if (m.type.equals("photo")) {
                entity = m;
                break;
            }
        }

        if (entity != null) {
            return entity.mediaUrlHttps;
        } else {
            return null;
        }
    }

    public static boolean containSlide(Tweet tweet) {
        UrlEntity url = urlEntity(tweet);
        return url != null && url.expandedUrl.startsWith(SpeakerDeck.SPEAKER_DECK_URL);
    }

    public static String tweetText(Tweet tweet) {
        if (tweet.quotedStatus != null) {
            String url = url(tweet);

            if (url != null) {
                return tweet.text.replace(url, "");
            } else {
                return tweet.text;
            }
        } else {
            return tweet.text;
        }
    }

    @Nullable
    public static String url(Tweet tweet) {
        UrlEntity url = urlEntity(tweet);

        if (url != null) {
            return url.url;
        } else {
            return null;
        }
    }

    @Nullable
    public static String expandedUrl(Tweet tweet) {
        tweet = tweet.quotedStatus != null ? tweet.quotedStatus : tweet;
        UrlEntity url = urlEntity(tweet);

        if (url != null) {
            return url.expandedUrl;
        } else {
            return null;
        }
    }

    @Nullable
    public static String expandedUrlWithoutTwitter(Tweet tweet) {
        String url = expandedUrl(tweet);

        if (url != null && !url.contains("twitter.com")) {
            return url;
        } else {
            return null;
        }
    }

    @Nullable
    public static String expandedUrlHost(Tweet tweet) {
        String expandedUrl = expandedUrl(tweet);

        if (expandedUrl != null) {
            Uri uri = Uri.parse(expandedUrl);
            return uri.getHost();
        } else {
            return null;
        }
    }

    public static String expandedUrlDomain(Tweet tweet) {
        String expandedUrl = expandedUrl(tweet);
        Uri uri = Uri.parse(expandedUrl);
        String[] parts = uri.getHost().split("\\.");
        int length = parts[0].length();

        if (parts[0].equals("www")) {
            return parts[1];
        } else if (length == 1 || length == 2) {
            // b.hatena.ne.jp
            // jp.routers.com
            return parts[1];
        } else {
            return parts[0];
        }
    }

    @Nullable
    private static UrlEntity urlEntity(Tweet tweet) {
        List<UrlEntity> urls = tweet.entities.urls;
        if (urls.size() > 0) {
            return urls.get(0);
        } else {
            return null;
        }
    }

    @Nullable
    public static String screenName(@Nullable Tweet tweet) {
        if (tweet == null) {
            return null;
        }
        return UserUtil.screenName(tweet.user);
    }

    @Nullable
    public static String createdAt(@Nullable Tweet tweet) {
        if (tweet == null) {
            return null;
        }

        DateTimeFormatter f = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        ZonedDateTime d;

        try {
            d = ZonedDateTime.parse(tweet.createdAt, f);
        } catch (Exception e) {
            // TODO: Error reporting
            return tweet.createdAt;
        }

        d = d.withZoneSameInstant(ZoneId.systemDefault());

        // TODO: Locale
        // TODO: Format time as any other client
        if (d.toLocalDate().equals(LocalDate.now())) {
            return d.format(DateTimeFormatter.ofPattern("H時m分"));
        } else {
            return d.format(DateTimeFormatter.ofPattern("M月d日"));
        }
    }

    @Nullable
    public static String retweetUserName(Tweet tweet) {
        if (tweet.retweetedStatus == null) {
            return null;
        }

        return String.format("%sがリツイート", tweet.user.name);
    }

    public static String favoriteCountStr(Tweet tweet) {
        Tweet tweetOrRetweetedStatus = tweetOrRetweetedStatus(tweet);
        return formatCount(tweetOrRetweetedStatus.favoriteCount);
    }

    public static Tweet tweetOrRetweetedStatus(Tweet tweet) {
        return tweet.retweetedStatus == null ? tweet : tweet.retweetedStatus;
    }

    public static String formatCount(int count) {
        if (count == 0) {
            return "";
        } else if (count < 10000) {
            // TODO: NumberFormat for api level 24
            return NumberFormat.getInstance().format(count);
        } else {
            // 76K / 7.6万
            String format = Resource.getResources().getString(R.string.format_tweet_count);
            int countOver = count / 10000;
            int countUnder = (count % 10000) / 1000;

            return String.format(format, countOver, countUnder);
        }
    }

    public static void debugTimeline(List<Tweet> tweets) {
        for (int i = 0; i < tweets.size(); i++) {
            Tweet tweet = tweets.get(i);
            Tweet quotedStatus = tweet.quotedStatus;
            TweetEntities entities = tweet.entities;
            List<UrlEntity> urls;

            String photoUrl = photoUrl(tweet);
            String quotedPhotoUrl;

            Log.d("Serenade", String.format("timeline: %d", i));
            Log.d("Serenade", String.format("name: %s", tweet.user.name));
            Log.d("Serenade", String.format("text: %s", tweet.text));

            if (photoUrl != null) {
                Log.d("Serenade", String.format("photoUrl: %s", photoUrl));
            }

            if (entities != null) {
                urls = entities.urls;

                if (urls != null) {
                    for (UrlEntity url : urls) {
                        Log.d("Serenade", String.format("url: %s", url.url));
                        Log.d("Serenade", String.format("expandedUrl: %s", url.expandedUrl));
                        Log.d("Serenade", String.format("displayUrl: %s", url.displayUrl));
                    }
                }
            }

            if (quotedStatus != null) {
                Log.d("Serenade", String.format("quoted status: %d", i));
                Log.d("Serenade", String.format("name: %s", quotedStatus.user.name));
                Log.d("Serenade", String.format("text: %s", quotedStatus.text));

                quotedPhotoUrl = TweetUtil.photoUrl(quotedStatus);
                if (quotedPhotoUrl != null) {
                    Log.d("Serenade", String.format("photoUrl: %s", quotedPhotoUrl));
                }
            }
            Log.d("Serenade", "");
        }
    }
}
