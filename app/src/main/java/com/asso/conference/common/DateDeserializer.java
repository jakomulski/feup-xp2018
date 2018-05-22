package com.asso.conference.common;

import android.util.Log;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DateDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        String dateText = element.getAsString();

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy H:mm a", Locale.forLanguageTag("pt_PT"));

        //formatter.setTimeZone(TimeZone.getTimeZone("WET"));

        try {
            Date date = formatter.parse(dateText);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            //cal.add(Calendar.HOUR_OF_DAY, -1);
            //cal.add(Calendar.DAY_OF_MONTH, 1);
            return cal.getTime();
        } catch (ParseException e) {
            Log.e("Failed to parse Date", e.getMessage());
            return null;
        }
    }
}