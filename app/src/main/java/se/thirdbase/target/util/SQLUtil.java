package se.thirdbase.target.util;

import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alexp on 2/25/16.
 */
public class SQLUtil {
    private static final String TAG = SQLUtil.class.getSimpleName();

    private static final String DATE_FORMAT_PATTERN = "(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d) (\\d\\d):(\\d\\d):(\\d\\d)";
    private static final int DATE_FORMAT_YEAR   = 1;
    private static final int DATE_FORMAT_MONTH  = 2;
    private static final int DATE_FORMAT_DAY    = 3;
    private static final int DATE_FORMAT_HOUR   = 4;
    private static final int DATE_FORMAT_MINUTE = 5;
    private static final int DATE_FORMAT_SECOND = 6;

    private static Pattern mPattern = Pattern.compile(DATE_FORMAT_PATTERN);

    public static Calendar string2Calendar(String dateString) {
        Log.d(TAG, "string2Calendar(" + dateString + ")");

        Matcher matcher = mPattern.matcher(dateString);
        matcher.matches();

        int year = Integer.parseInt(matcher.group(DATE_FORMAT_YEAR));
        int month = Integer.parseInt(matcher.group(DATE_FORMAT_MONTH));
        int day = Integer.parseInt(matcher.group(DATE_FORMAT_DAY));
        int hour = Integer.parseInt(matcher.group(DATE_FORMAT_HOUR));
        int minute = Integer.parseInt(matcher.group(DATE_FORMAT_MINUTE));
        int second = Integer.parseInt(matcher.group(DATE_FORMAT_SECOND));

        return new GregorianCalendar(year, month, day, hour, minute, second);
    }

    public static String calendar2String(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return String.format("%s-%02d-%02d %02d:%02d", year, month, day, hour, minute);
    }
}
