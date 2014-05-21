package net.magicd.io.couchbase;

import net.magicd.io.couchbase.CouchBaseAIO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

public class CouchBaseJSON extends CouchBaseAIO {
    public CouchBaseJSON(URI endpoint, String bucket, String password) throws IOException {
        super(endpoint, bucket, password);
    }

    /**
     * @param key
     * @param jsonObject
     * @return
     */
    public boolean put(String key, JSONObject jsonObject) {
        return put(key, jsonObject.toString());
    }

    /**
     * @param key
     * @return
     */
    public JSONArray getJSONArray(String key) {
        try {
            return new JSONArray((String) client.get(key));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param key
     * @return
     */
    public <T extends JSONObject> T get(String key, Class<T> jsonType) {
        try {
            Constructor<T> constructor = jsonType.getConstructor(String.class);
            return constructor.newInstance(client.get(key));
        } catch (NoSuchMethodException | InstantiationException |
                IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * @param key
     * @param is_gziped
     * @param jsonType
     * @param <T>
     * @return
     */
    public <T extends JSONObject> T get(String key, boolean is_gziped, Class<T> jsonType) {
        String value = client.get(key).toString();
        value = is_gziped ? Gzip.decompress(value) : value;
        try {
            Constructor<T> constructor = jsonType.getConstructor(String.class);
            return constructor.newInstance(value);
        } catch (NoSuchMethodException | InstantiationException |
                IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



}
