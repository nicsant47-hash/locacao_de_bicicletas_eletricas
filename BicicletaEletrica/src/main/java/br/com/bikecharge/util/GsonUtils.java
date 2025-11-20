package br.com.bikecharge.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonUtils {

    public static Gson buildGson() {
        DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;

        GsonBuilder gb = new GsonBuilder();

        // LocalDate
        gb.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
            public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.format(df));
            }
        });
        gb.registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                return LocalDate.parse(json.getAsString(), df);
            }
        });

        // LocalDateTime
        gb.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.format(dtf));
            }
        });
        gb.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                String s = json.getAsString().replace(" ", "T");
                return LocalDateTime.parse(s, dtf);
            }
        });

        return gb.create();
    }
}
