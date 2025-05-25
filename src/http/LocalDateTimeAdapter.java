package http;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime.format(dtf));
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        //return LocalDateTime.parse(jsonReader.nextString(), dtf);
        try {
            String value = jsonReader.nextString();
            if (value.equals("null")) {
                return null;
            }
            return LocalDateTime.parse(value, dtf);
        } catch (IllegalStateException exception) {
            jsonReader.nextNull();
            return null;
        }
    }
}

