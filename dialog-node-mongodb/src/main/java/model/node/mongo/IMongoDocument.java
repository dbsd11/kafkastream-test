package model.node.mongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import model.Model;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * Created by BSONG on 2017/9/10.
 */
@Document(collection = "#{T(model.node.mongo.CollectionNameHolder).get()}")
public class IMongoDocument extends org.bson.Document {
    private static final Gson gson = new GsonBuilder().serializeNulls()
            .registerTypeAdapter(Model.class, (JsonSerializer<Model>) (domainModel, type, jsonSerializationContext) -> new JsonPrimitive(new String(domainModel.serialBytes())))
            .registerTypeAdapter(Model.class, (JsonDeserializer<Model>) (jsonElement, type, jsonDeserializationContext) -> {
                try {
                    return (Model) new ObjectInputStream(new ByteArrayInputStream(jsonElement.getAsString().getBytes())).readObject();
                } catch (Exception e) {
                    return null;
                }
            }).create();

    public static IMongoDocument fromObj(Object obj) {
        return gson.fromJson(gson.toJson(obj), IMongoDocument.class);
    }

    public <T> T getOrigin(Class<T> templateClass) {

        return gson.fromJson(gson.toJson(this), templateClass);
    }
}
