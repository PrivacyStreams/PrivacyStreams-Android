package com.github.privacystreams.audio;

import android.net.Uri;

import com.github.privacystreams.core.Function;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.providers.MultiItemStreamProvider;
import com.github.privacystreams.core.providers.SingleItemStreamProvider;

/**
 * An Audio item represents an audio record
 */

public class Audio extends Item {
    // type: Long
    public static final String TIMESTAMP = "timestamp";
    // type: String, representing the URI of audio file
    public static final String URI = "uri";

    Audio(long timestamp, Uri file_uri) {
        this.setFieldValue(TIMESTAMP, timestamp);
        this.setFieldValue(URI, file_uri.toString());
    }

    /**
     * get a item provider that provides a audio item
     * the audio item represents a recorded audio with certain length of time
     * @param duration the time duration of audio
     * @return the provider
     */
    public static SingleItemStreamProvider record(long duration) {
        return new AudioRecorder(duration);
    }

    /**
     * get a stream provider that provides a audio item
     * each audio item represents a recorded audio with certain length of time
     * audio is recorded every certain time
     * @param duration_per_record the time duration per audio record
     * @param interval the interval between each audio record
     * @return the provider
     */
    public static MultiItemStreamProvider recordPeriodically(long duration_per_record, long interval) {
        return new AudioPeriodicRecorder(duration_per_record, interval);
    }
}
