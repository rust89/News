package ru.sike.lada.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {


    public static String formatDate(long timestamp, String pToday, String pYesterday, String pDayBeforeYesterday) {
        Date dateTime = new Date(timestamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);

        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        Calendar beforeYesterday = Calendar.getInstance();

        yesterday.add(Calendar.DATE, -1);
        beforeYesterday.add(Calendar.DATE, -2);

        DateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        DateFormat dateFormatterWithoutYear = new SimpleDateFormat("dd MMMM HH:mm", Locale.getDefault());
        DateFormat dateFormatterWithYear = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return pToday + " " + timeFormatter.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return pYesterday + " " + timeFormatter.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == beforeYesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == beforeYesterday.get(Calendar.DAY_OF_YEAR)) {
            return pDayBeforeYesterday + " " + timeFormatter.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
            return dateFormatterWithoutYear.format(dateTime);
        } else {
            return dateFormatterWithYear.format(dateTime);
        }
    }

}
