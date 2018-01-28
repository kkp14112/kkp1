package org.schabi.newpipe.extractor.stream;

/*
 * Created by wojcik.online on 2018-01-25.
 */

import org.schabi.newpipe.extractor.exceptions.ParsingException;

import java.util.Calendar;
import java.util.EnumMap;
import java.util.Map;

/**
 * A helper class that is meant to be used by services that need to parse upload dates in the
 * format '2 days ago' or similar.
 */
public class TimeAgoParser {

    /**
     * A set of english phrases that are contained in the time units.
     * (e.g. '7 minutes ago' contains 'min')
     */
    public static Map<TimeAgoUnit, String[]> DEFAULT_AGO_PHRASES = new EnumMap<>(TimeAgoUnit.class);

    private final Map<TimeAgoUnit, String[]> agoPhrases;

    private final Calendar consistentNow;

    /**
     * Creates a helper to parse upload dates in the format '2 days ago'.
     * <p>
     *     Instantiate a new {@link TimeAgoParser} every time you extract a new batch of items.
     * </p>
     * @param agoPhrases A set of phrases how to recognize the time units in a given language.
     */
    public TimeAgoParser(Map<TimeAgoUnit, String[]> agoPhrases) {
        this.agoPhrases = agoPhrases;
        consistentNow = Calendar.getInstance();
    }

    /**
     * Parses a textual date in the format '2 days ago' into a Calendar representation.
     * Sets the time to the beginning of the day/week/month/year if no exact time is available.
     * @param textualDate The original date as provided by the streaming service
     * @return The parsed (approximated) time
     * @throws ParsingException if the time unit could not be recognized
     */
    public Calendar parse(String textualDate) throws ParsingException {
        try {
            int timeAgoAmount = parseTimeAgoAmount(textualDate);
            TimeAgoUnit timeAgoUnit = parseTimeAgoUnit(textualDate);

            return getCalendar(timeAgoAmount, timeAgoUnit);
        } catch (NumberFormatException e) {
            // If there is no valid number in the textual date, assume it is 'moments ago'.
            return getCalendar(0, TimeAgoUnit.SECONDS);
        }
    }

    private int parseTimeAgoAmount(String textualDate) throws NumberFormatException {
        String timeValueStr = textualDate.replaceAll("\\D+", "");
        return Integer.parseInt(timeValueStr);
    }

    private TimeAgoUnit parseTimeAgoUnit(String textualDate) throws ParsingException {
        for (TimeAgoUnit timeAgoUnit : agoPhrases.keySet()) {
            for (String agoPhrase : agoPhrases.get(timeAgoUnit)) {
                if (textualDate.toLowerCase().contains(agoPhrase.toLowerCase())){
                    return timeAgoUnit;
                }
            }
        }

        throw new ParsingException("Unable to parse the date: " + textualDate);
    }

    private Calendar getCalendar(int timeAgoAmount, TimeAgoUnit timeAgoUnit) {
        Calendar calendarTime = getNow();

        switch (timeAgoUnit) {
            case SECONDS:
                calendarTime.add(Calendar.SECOND, -timeAgoAmount);
                break;

            case MINUTES:
                calendarTime.add(Calendar.MINUTE, -timeAgoAmount);
                break;

            case HOURS:
                calendarTime.add(Calendar.HOUR_OF_DAY, -timeAgoAmount);
                break;

            case DAYS:
                calendarTime.add(Calendar.DAY_OF_MONTH, -timeAgoAmount);
                resetTimeOfDay(calendarTime);
                break;

            case WEEKS:
                calendarTime.add(Calendar.WEEK_OF_YEAR, -timeAgoAmount);
                calendarTime.set(Calendar.DAY_OF_WEEK, calendarTime.getFirstDayOfWeek());
                resetTimeOfDay(calendarTime);
                break;

            case MONTHS:
                calendarTime.add(Calendar.MONTH, -timeAgoAmount);
                calendarTime.set(Calendar.DAY_OF_MONTH, 1);
                resetTimeOfDay(calendarTime);
                break;

            case YEARS:
                calendarTime.add(Calendar.YEAR, -timeAgoAmount);
                calendarTime.set(Calendar.MONTH, 1);
                calendarTime.set(Calendar.DAY_OF_MONTH, 1);
                resetTimeOfDay(calendarTime);
                break;
        }

        return calendarTime;
    }

    private Calendar getNow() {
        return (Calendar) consistentNow.clone();
    }

    private void resetTimeOfDay(Calendar calendarTime) {
        calendarTime.set(Calendar.HOUR_OF_DAY, 0);
        calendarTime.set(Calendar.MINUTE, 0);
        calendarTime.set(Calendar.SECOND, 0);
        calendarTime.set(Calendar.MILLISECOND, 0);
    }

    static {
        DEFAULT_AGO_PHRASES.put(TimeAgoUnit.SECONDS, new String[]{"sec"});
        DEFAULT_AGO_PHRASES.put(TimeAgoUnit.MINUTES, new String[]{"min"});
        DEFAULT_AGO_PHRASES.put(TimeAgoUnit.HOURS, new String[]{"hour"});
        DEFAULT_AGO_PHRASES.put(TimeAgoUnit.DAYS, new String[]{"day"});
        DEFAULT_AGO_PHRASES.put(TimeAgoUnit.WEEKS, new String[]{"week"});
        DEFAULT_AGO_PHRASES.put(TimeAgoUnit.MONTHS, new String[]{"month"});
        DEFAULT_AGO_PHRASES.put(TimeAgoUnit.YEARS, new String[]{"year"});
    }

    public enum TimeAgoUnit {
        SECONDS,
        MINUTES,
        HOURS,
        DAYS,
        WEEKS,
        MONTHS,
        YEARS,
    }
}
