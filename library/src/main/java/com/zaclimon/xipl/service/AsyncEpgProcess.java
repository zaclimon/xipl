package com.zaclimon.xipl.service;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser;
import com.zaclimon.xipl.properties.ChannelProperties;
import com.zaclimon.xipl.util.ProviderChannelUtil;
import com.zaclimon.xipl.util.RichFeedUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Custom class used to download and process in a asynchronous fashion the M3U playlist as well
 * as the EPG guide from a user.
 *
 * @author zaclimon
 * Creation date: 11/06/17
 */
public class AsyncEpgProcess extends AsyncTask<Void, Void, Boolean> {

    private WeakReference<Context> mReference;
    private String mPlaylistUrl;
    private String mEpgUrl;
    private ChannelProperties mChannelProperties;
    private List<Channel> mChannels;
    private XmlTvParser.TvListing mTvListing;
    private EpgProcessingCallback mCallback;

    public AsyncEpgProcess(Context context, String playlistUrl, String epgUrl, ChannelProperties channelProperties, EpgProcessingCallback epgProcessingCallback) {
        mReference = new WeakReference<>(context);
        mPlaylistUrl = playlistUrl;
        mEpgUrl = epgUrl;
        mChannelProperties = channelProperties;
        mCallback = epgProcessingCallback;
    }

    @Override
    public Boolean doInBackground(Void... params) {

        Context context = mReference.get();

        if (context != null) {
            try {
                InputStream inputStream = RichFeedUtil.getInputStream(context, Uri.parse(mPlaylistUrl));

                if (mEpgUrl != null) {
                    mTvListing = RichFeedUtil.getRichTvListings(context, mEpgUrl);
                }

                mChannels = ProviderChannelUtil.createChannelList(inputStream, context, mChannelProperties);

                return (mChannels != null);
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        return (false);
    }

    @Override
    public void onPostExecute(Boolean result) {

        if (result) {
            mCallback.onProcessSuccess(mChannels, mTvListing);
        } else {
            mCallback.onProcessFailed();
        }
    }
}
