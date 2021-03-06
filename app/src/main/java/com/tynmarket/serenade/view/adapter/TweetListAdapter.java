package com.tynmarket.serenade.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.twitter.sdk.android.core.models.Tweet;
import com.tynmarket.serenade.R;
import com.tynmarket.serenade.model.entity.TwitterCard;
import com.tynmarket.serenade.model.util.LogUtil;
import com.tynmarket.serenade.model.util.TweetUtil;
import com.tynmarket.serenade.view.holder.TweetViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tynmarket on 2017/12/18.
 */

public class TweetListAdapter extends RecyclerView.Adapter<TweetViewHolder> {
    private final ArrayList<Tweet> tweets;
    private Map<String, TwitterCard> cards;
    private boolean cardsLoaded = false;

    public TweetListAdapter(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
        this.cards = new HashMap<>();
    }

    @Override
    // parent is RecyclerView
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_tweet, parent, false);

        return new TweetViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(TweetViewHolder holder, int position) {
        LogUtil.d(String.format("onBindViewHolder: position=%d", position));

        holder.setAdapter(this);

        Tweet tweet = this.tweets.get(position);
        TwitterCard card = cards.get(TweetUtil.expandedUrl(tweet));
        if (card != null) {
            card.tweet = tweet;
        }

        holder.setTweetAndCardToBindings(tweet, card);
    }

    @Override
    public int getItemCount() {
        return this.tweets.size();
    }

    public Tweet getTweet(int position) {
        return tweets.get(position);
    }

    public boolean requestCardCache(int position) {
        if (tweets.size() == 0) {
            return false;
        }

        Tweet tweet = tweets.get(position);
        String url = TweetUtil.expandedUrlWithoutTwitter(tweet);
        TwitterCard card = cards.get(url);

        return (url != null && card == null) || (card != null && card.retry);
    }

    public void refresh(List<Tweet> newTweets) {
        tweets.clear();
        tweets.addAll(newTweets);
        notifyDataSetChanged();
    }

    public void addTweets(List<Tweet> prevTweets) {
        int itemCount = tweets.size();
        tweets.addAll(prevTweets);
        notifyItemRangeInserted(itemCount, prevTweets.size());
    }

    public void replaceTweet(int position, Tweet tweet) {
        tweets.set(position, tweet);
    }

    public void refreshCards(Map<String, TwitterCard> cards) {
        if (cards.size() == 0) {
            this.cards = cards;
        } else {
            this.cards.putAll(cards);
        }
        cardsLoaded = true;
    }

    public void replaceCard(TwitterCard card) {
        cards.put(card.url, card);
    }

    public boolean cardsLoaded() {
        return cardsLoaded;
    }

    public void clearAll() {
        tweets.clear();
        cards.clear();
        notifyDataSetChanged();
    }
}
