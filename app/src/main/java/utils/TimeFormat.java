package utils;

import android.annotation.SuppressLint;

public class TimeFormat {
    @SuppressLint("DefaultLocale")
    public static String ShowTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }
}
