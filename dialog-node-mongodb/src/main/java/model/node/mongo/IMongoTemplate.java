package model.node.mongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import model.Model;
import org.bson.Document;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * Created by BSONG on 2017/9/10.
 */
public class IMongoTemplate extends Document {
    private static final Gson gson = new GsonBuilder().serializeNulls()
            .registerTypeAdapter(Model.class, (JsonSerializer<Model>) (domainModel, type, jsonSerializationContext) -> new JsonPrimitive(new String(domainModel.serialBytes())))
            .registerTypeAdapter(Model.class, (JsonDeserializer<Model>) (jsonElement, type, jsonDeserializationContext) -> {
                try {
                    return (Model) new ObjectInputStream(new ByteArrayInputStream(jsonElement.getAsString().getBytes())).readObject();
                } catch (Exception e) {
                    return null;
                }
            }).create();

    public static IMongoTemplate fromObj(Object obj) {
        return gson.fromJson(gson.toJson(obj), IMongoTemplate.class);
    }

    public <T> T getOrigin(Class<T> templateClass) {

        return gson.fromJson(gson.toJson(this), templateClass);
    }
}
