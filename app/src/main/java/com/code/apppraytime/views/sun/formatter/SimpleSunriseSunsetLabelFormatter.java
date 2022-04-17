package com.code.apppraytime.views.sun.formatter;

import com.code.apppraytime.views.sun.model.Time;

public class SimpleSunriseSunsetLabelFormatter implements SunriseSunsetLabelFormatter {
    @Override
    public String formatSunriseLabel(Time sunrise) {
        return formatTime(sunrise);
    }

    @Override
    public String formatSunsetLabel(Time sunset) {
        return formatTime(sunset);
    }

    public String formatTime(Time time) {
        if (time.hour<12)
            return  "Sunrise";
        else return  "Sunset";
//        return String.format(Locale.getDefault(), "%d:%d", time.hour, time.minute);
    }

}
