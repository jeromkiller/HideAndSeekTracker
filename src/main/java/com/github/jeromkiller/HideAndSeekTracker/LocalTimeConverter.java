package com.github.jeromkiller.HideAndSeekTracker;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Objects;

public class LocalTimeConverter extends TypeAdapter<LocalTime> {

    @Override
    public void write(JsonWriter jsonWriter, LocalTime localTime) throws IOException {
        if (Objects.isNull(localTime)) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(localTime.toString());
    }

    @Override
    public LocalTime read(JsonReader jsonReader) throws IOException {
        String dateTimeString = jsonReader.nextString();
        return LocalTime.parse(dateTimeString);
    }
}
