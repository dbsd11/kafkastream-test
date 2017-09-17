package model.node.mongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import model.Model;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.Set;

/**
 * Created by BSONG on 2017/9/10.
 */
@Document(collection = "#{T(model.node.mongo.CollectionNameHolder).get()}")
@AccessType(AccessType.Type.PROPERTY)
public class IMongoDocument extends org.bson.Document {
    private static final Gson gson = new GsonBuilder().serializeNulls()
            .registerTypeHierarchyAdapter(Model.class, (JsonSerializer<Model>) (domainModel, type, jsonSerializationContext) -> new JsonPrimitive(new String(domainModel.serialBytes())))
            .registerTypeHierarchyAdapter(Model.class, (JsonDeserializer<Model>) (jsonElement, type, jsonDeserializationContext) -> {
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

    @ReadingConverter
    public static class DocumentConverter implements GenericConverter{

        public static final DocumentConverter DEFAULT = new DocumentConverter();

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(org.bson.Document.class, IMongoDocument.class));

        }

        @Override
        public Object convert(@Nullable Object o, TypeDescriptor typeDescriptor, TypeDescriptor typeDescriptor1) {
            return null;
        }
    }
}
