package br.com.constance.app.appkstore.backend.util.serializadores;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateLongTypeAdapter extends TypeAdapter<Long> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void write(JsonWriter out, Long value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            String formattedDate = dateFormat.format(new Date(value));
            out.value(formattedDate);
        }
    }

    @Override
    public Long read(JsonReader in) throws IOException {
        try {
            String dateString = in.nextString();
            return dateFormat.parse(dateString).getTime();
        } catch (Exception e) {
            return null;
        }
    }
}
