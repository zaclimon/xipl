/*
 * Copyright (C) 2016 The Android Open Source Project
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

import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.flv.FlvExtractor;
import com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer2.extractor.ogg.OggExtractor;
import com.google.android.exoplayer2.extractor.ts.Ac3Extractor;
import com.google.android.exoplayer2.extractor.ts.AdtsExtractor;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.extractor.wav.WavExtractor;

import java.lang.reflect.Constructor;

/**
 * Basic implementation of {@link ExtractorsFactory} responsible for injecting a
 * {@link SeekableTsExtractor}
 *
 * @author zaclimon
 * Creation date: 14/08/17
 */

public class SeekableTsExtractorsFactory implements ExtractorsFactory {

    private static final Constructor<? extends Extractor> FLAC_EXTRACTOR_CONSTRUCTOR;
    private int mStreamDuration;

    public SeekableTsExtractorsFactory(int streamDuration) {
        mStreamDuration = streamDuration;
    }

    static {
        Constructor<? extends Extractor> flacExtractorConstructor = null;
        try {
            flacExtractorConstructor =
                    Class.forName("com.google.android.exoplayer2.ext.flac.FlacExtractor")
                            .asSubclass(Extractor.class).getConstructor();
        } catch (ClassNotFoundException e) {
            // Extractor not found.
        } catch (NoSuchMethodException e) {
            // Constructor not found.
        }
        FLAC_EXTRACTOR_CONSTRUCTOR = flacExtractorConstructor;
    }

    @Override
    public synchronized Extractor[] createExtractors() {
        Extractor[] extractors = new Extractor[FLAC_EXTRACTOR_CONSTRUCTOR == null ? 11 : 12];
        extractors[0] = new MatroskaExtractor();
        extractors[1] = new FragmentedMp4Extractor();
        extractors[2] = new Mp4Extractor();
        extractors[3] = new Mp3Extractor();
        extractors[4] = new AdtsExtractor();
        extractors[5] = new Ac3Extractor();
        extractors[6] = getTsExtractor();
        extractors[7] = new FlvExtractor();
        extractors[8] = new OggExtractor();
        extractors[9] = new PsExtractor();
        extractors[10] = new WavExtractor();
        if (FLAC_EXTRACTOR_CONSTRUCTOR != null) {
            try {
                extractors[11] = FLAC_EXTRACTOR_CONSTRUCTOR.newInstance();
            } catch (Exception e) {
                // Should never happen.
                throw new IllegalStateException("Unexpected error creating FLAC extractor", e);
            }
        }
        return extractors;
    }

    /**
     * Determines the MPEG2-TS extractor to give based on the duration of a stream.
     *
     * @return the {@link SeekableTsExtractor}
     */
    private Extractor getTsExtractor() {
        if (mStreamDuration > 0) {
            TsSeekMap seekMap = new TsSeekMap(mStreamDuration);
            return (new SeekableTsExtractor(seekMap));
        } else {
            return (new SeekableTsExtractor());
        }
    }

}
