/*
 * Copyright (C) 2017 Isaac Pateau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zaclimon.xipl.player;

import com.google.android.exoplayer2.extractor.SeekMap;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link SeekMap} for MPEG-TS streams.
 *
 * Initially based on Abdallah Moussawi's implementation for ExoPlayer:
 * https://github.com/amoussawi/ExoPlayer/commit/e49e3637d696c24e6001c47d499315e51a3251e3
 *
 * @author zaclimon
 * Creation date: 14/08/17
 */

public class TsSeekMap implements SeekMap {

    // PCR array for seeking
    private Vector<TimeBytePosition> timeToByte;
    // The duration from an Xtream Codes stream is usually defined in minutes.
    private int mDuration;

    /**
     * Default constructor
     *
     * @param duration the duration of the stream.
     */
    public TsSeekMap(int duration) {
        mDuration = duration;
        timeToByte = new Vector<>();
    }

    /**
     * Adds a byte position and it's time value in the map.
     *
     * @param timeMs the time in milliseconds
     * @param bytePos the byte position in the stream.
     */
    public void addPosition(long timeMs, long bytePos) {
        timeToByte.add(new TimeBytePosition(timeMs, bytePos));
    }

    @Override
    public boolean isSeekable() {
        return (true);
    }

    @Override
    public long getPosition(long positionUs) {
        // Search for the closest position using binary search
        positionUs /= 1000;

        // The first time-position might not be aligned with 0 (live-streaming)
        positionUs += timeToByte.get(0).timeMs;
        int q = 0;
        int r = timeToByte.size();
        int mid = -1;
        while (q < r) {
            mid = (q + r) / 2;
            if (timeToByte.get(mid).timeMs == positionUs) {
                break;
            } else if (timeToByte.get(mid).timeMs > positionUs) {
                r = mid;
            } else if (timeToByte.get(mid).timeMs < positionUs) {
                q = mid + 1;
            }
        }
        return mid == -1 ? 0 : timeToByte.get(mid).bytePos;
    }

    @Override
    public long getDurationUs() {
        return (TimeUnit.MINUTES.toMicros(mDuration));
    }

    /**
     * Gets time position from pcr value
     *
     * @param pcrBytes the array of bytes from the PCR
     * @return the time position in milliseconds.
     */
    public long getPcrPositionMs(byte[] pcrBytes) {
        long pcr=(((long)((pcrBytes[0]<<24)
                +(pcrBytes[1]<<16 & 0x00FF0000)
                +(pcrBytes[2]<<8 & 0x0000FF00)
                +(pcrBytes[3] & 0x000000FF)))<<1 & 0x00000001FFFFFFFFL)
                +(pcrBytes[4]>>7 & 1);
        return (pcr / 90);
    }

    /**
     * Private class used to store a PCR position based on the time.
     */
    private class TimeBytePosition {
        // Position in time
        public long timeMs;

        // Equivalent byte offset
        public long bytePos;

        public TimeBytePosition(long timeMs,long bytePos){
            this.timeMs = timeMs;
            this.bytePos = bytePos;
        }
    }

}
