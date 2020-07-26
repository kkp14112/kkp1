package org.schabi.newpipe.extractor.localization;

import java.io.Serializable;
import java.util.Calendar;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A wrapper class that provides a field to describe
 * if the date is precise or just an approximation.
 */
public class DateWrapper implements Serializable {
    @NonNull
    private final Calendar date;
    private final boolean isApproximation;

    public DateWrapper(@NonNull final Calendar date) {
        this(date, false);
    }

    public DateWrapper(@NonNull final Calendar date, final boolean isApproximation) {
        this.date = date;
        this.isApproximation = isApproximation;
    }

    /**
     * @return the wrapped date.
     */
    @NonNull
    public Calendar date() {
        return date;
    }

    /**
     * @return if the date is considered is precise or just an approximation (e.g. service only returns an approximation
     * like 2 weeks ago instead of a precise date).
     */
    public boolean isApproximation() {
        return isApproximation;
    }
}
