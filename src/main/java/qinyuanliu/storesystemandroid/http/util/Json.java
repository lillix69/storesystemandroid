package qinyuanliu.storesystemandroid.http.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by lillix on 6/26/17.
 */
public final class Json {
    private Json() {
    }

    public static String encode(StringMap map) {
        return (new Gson()).toJson(map.map());
    }

    public static String encode(Object obj) {
        return (new GsonBuilder()).serializeNulls().create().toJson(obj);
    }

    public static <T> T decode(String json, Class<T> classOfT) {
//String newstr = json.replace("null","\"\"");

        return (new Gson()).fromJson(json, classOfT);


    }

    public static StringMap decode(String json) {
        Type t = (new TypeToken() {
        }).getType();
        Map x = (Map)(new Gson()).fromJson(json, t);
        return new StringMap(x);
    }
}
