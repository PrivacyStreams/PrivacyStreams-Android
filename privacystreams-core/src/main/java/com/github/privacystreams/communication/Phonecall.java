package com.github.privacystreams.communication;


import com.github.privacystreams.core.Function;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.MultiItemStream;
import com.github.privacystreams.core.providers.MultiItemStreamProvider;
import com.github.privacystreams.utils.annotations.PSItem;
import com.github.privacystreams.utils.annotations.PSItemField;

/**
 * A Phonecall item represents a phonecall record
 */
@PSItem
public class Phonecall extends Item {

    /**
     * The timestamp of when the phonecall is happened.
     */
    @PSItemField(type = Long.class)
    public static final String TIMESTAMP = "timestamp";

    /**
     * The contact (phone number or name) of the phonecall.
     */
    @PSItemField(type = String.class)
    public static final String CONTACT = "contact";

    /**
     * The duration of the phonecall, in milliseconds.
     */
    @PSItemField(type = Long.class)
    public static final String DURATION = "duration";

    /**
     * The phonecall type, could be "incoming", "outgoing" or "missed".
     */
    @PSItemField(type = String.class)
    public static final String TYPE = "type";

    public static class Types {
        public static final String INCOMING = "incoming";
        public static final String OUTGOING = "outgoing";
        public static final String MISSED = "missed";
    };

    Phonecall(Long timestamp, String phone_number, Long duration, String call_type) {
        this.setFieldValue(TIMESTAMP, timestamp);
        this.setFieldValue(CONTACT, phone_number);
        this.setFieldValue(DURATION, duration);
        this.setFieldValue(TYPE, call_type);
    }

    /**
     * Get a provider that provides a stream of Phonecall asLogs
     * @return the stream provider
     */
    public static Function<Void, MultiItemStream> asLogs() {
        return new PhonecallLogProvider();
    }
}