package com.code.apppraytime.views.sun.formatter;

import com.code.apppraytime.views.sun.model.Time;

public interface SunriseSunsetLabelFormatter {

    String formatSunriseLabel(Time sunrise);

    String formatSunsetLabel(Time sunset);
}
