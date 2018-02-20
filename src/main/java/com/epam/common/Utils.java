package com.epam.common;

import java.util.Calendar;

public class Utils {

    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static Calendar current() {
        return Calendar.getInstance();
    }

    public static long currentTimeInMillis() {
        return current().getTimeInMillis();
    }

    public static int currentYear() {
        return current().get(Calendar.YEAR);
    }
}
