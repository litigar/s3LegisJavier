package liti.s3Legis;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class BidirectionalDateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date> {

	private static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

	public JsonElement serialize(Date src, Type typeOfSrc,
			JsonSerializationContext context) {
		return new JsonPrimitive(format.format(src));
	}


	public Date deserialize(JsonElement json, Type typeOfSrc,
			JsonDeserializationContext context) throws JsonParseException {

        try {
            return format.parse(json.getAsJsonPrimitive().getAsString());
        } catch (ParseException exception) {
            throw new JsonParseException(exception.getMessage());
        }
	}

}