package net.magicd.io.couchbase;

import com.couchbase.client.CouchbaseClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A CouchBaseClient Wrapper.
 *
 * @author Hiroshi IKEGAMI
 * @version 0.1
 */
public class CouchBaseAIO {

    public enum Format{ JSON,STRING }

    /**
     * the couchbase client entity
     */
    protected CouchbaseClient client;

    /**
     * couchbase bucketName
     */
    protected String bucketName;

    /**
     * getter: bucketName
     *
     * @return bucketName
     */
    public String getBucketName() { return this.bucketName; }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseAIO(URI endpoint, String bucket, String password) throws IOException {
        List<URI> couchBaseEndPoints = Arrays.asList(endpoint);
        this.client = new CouchbaseClient(couchBaseEndPoints, bucket, password);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseAIO(URI[] endpoints, String bucket, String password) throws IOException {
        List<URI> couchBaseEndPoints = Arrays.asList(endpoints);
        this.client = new CouchbaseClient(couchBaseEndPoints, bucket, password);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseAIO(String endpoint, String bucket, String password) throws IOException {
        List<URI> couchBaseEndPoints;
        try {
            couchBaseEndPoints = Arrays.asList(new URI(endpoint));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
        this.client = new CouchbaseClient(couchBaseEndPoints, bucket, password);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseAIO(String[] endpoints, String bucket, String password) throws IOException {
        List<URI> couchBaseEndPoints = new ArrayList<>();
        try {
            for (String endpoint : endpoints) {
                couchBaseEndPoints.add(new URI(endpoint));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
        this.client = new CouchbaseClient(couchBaseEndPoints, bucket, password);
    }

    /**
     * destructor
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } finally {
            client.shutdown();
        }
    }

    /**
     * Put java.lang.Object instance into couchbase.
     *
     * @param key
     */
    public boolean put(String key, String str) throws IOException {
        try {
            return client.set(key, str).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException(e);
        }
    }

    /**
     * Get valueObj via couchbase.
     *
     * @param key
     * @return value
     */
    public JSONObject get(String key) throws IOException {
        try {
            return new JSONObject(client.get(key).toString());
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    /**
     * get String via couchbase
     *
     * @param key
     * @return value
     */
    public String getString(String key) { return client.get(key).toString(); }

    /**
     * Get valueObj via couchbase.
     *
     * @param key
     * @return value
     */
    public <T> T get(String key, Format format) throws IOException {
        Object value = client.get(key);
        Object result;
        switch (format) {
            case JSON:
                try {
                    result = new JSONObject(value.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new IOException(e);
                }
                break;
            case STRING:
                result = value.toString();
                break;
            default:
                throw new IOException(
                        String.format( "Format \"%s\" not supported.", format.toString() )
                );
        }
        return (T) result;
    }
}

