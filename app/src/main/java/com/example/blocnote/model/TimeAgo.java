package com.example.blocnote.model;

public class TimeAgo {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }


        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "maintenant";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "il y'a 1 minute";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "il y'a "+diff / MINUTE_MILLIS + " minutes";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "il y'a 1 heure";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "il y a "+ diff / HOUR_MILLIS + " heures";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Hier";
        } else {
            return "il y'a "+ diff / DAY_MILLIS + " jours";
        }
    }
}