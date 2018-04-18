package com.zaclimon.xipl.service;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.xmltv.XmlTvParser;

import java.util.List;

/**
 * Interface responsible for notifying if Rich Tv (M3U/XMLTV) data has been
 * successfully downloaded and processed.
 *
 * @author zaclimon
 * Creation date: 17/04/18
 */
public interface EpgProcessingCallback {

    /**
     * Notifies the user that the Rich Tv data has been processed successfully.
     *
     * @param channels the list of channels retrieved from a M3U playlist
     * @param listing the programs listings normally retrieved from an XMLTV
     */
    void onProcessSuccess(List<Channel> channels, XmlTvParser.TvListing listing);

    /**
     * Notifies the user that the processing has failed.
     */
    void onProcessFailed();

}
