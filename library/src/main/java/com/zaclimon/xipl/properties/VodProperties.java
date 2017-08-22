package com.zaclimon.xipl.properties;

/**
 * Base interface in which one can define properties for VOD content.
 *
 * @author zaclimon
 * Creation date: 11/08/17
 */

public interface VodProperties {

    /**
     * Determines if a video should fit to the dimensions of the display.
     *
     * @return true if the video should fit to the display's dimensions.
     */
    boolean isVideoFitToScreen();

    /**
     * Determines if an external player should be used for playback instead of the internal one.
     *
     * @return true if an external player should be used.
     */
    boolean isExternalPlayerUsed();

}
