package net.magicd.io.couchbase;

import com.couchbase.client.CouchbaseClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

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
public class CouchBaseIO {

    public enum JsonType {MULTI, SINGLE}

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
    public String getBucketName() {
        return this.bucketName;
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(URI endpoint, String bucket, String password) throws IOException {
        List<URI> couchBaseEndPoints = Arrays.asList(endpoint);
        this.client = new CouchbaseClient(couchBaseEndPoints, bucket, password);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(URI[] endpoints, String bucket, String password) throws IOException {
        List<URI> couchBaseEndPoints = Arrays.asList(endpoints);
        this.client = new CouchbaseClient(couchBaseEndPoints, bucket, password);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(String endpoint, String bucket, String password) throws IOException {
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
    public CouchBaseIO(String[] endpoints, String bucket, String password) throws IOException {
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
    public <T> boolean put(String key, T value) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return client.set(key, mapper.writeValueAsString(value)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException(e);
        }
    }

    /**
     *
     * @param key
     * @param ignored
     * @param <T>
     * @return ArrayList<(POJO class)>
     */
    public <T> ArrayList<T> getMultiValue(String key, Class<T> ignored) throws IOException {
        try {
            String value = client.get(key).toString();
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<T> data =
                    mapper.readValue(value, new TypeReference<ArrayList<T>>() {});
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    /**
     *
     * @param key
     * @param ignored
     * @param <T>
     * @return ArrayList<(POJO class)>
     */
    public <T> T get(String key, Class<T> ignored) {
        try {
            String value = client.get(key).toString();
            System.err.println(ignored.getCanonicalName());
            ObjectMapper mapper = new ObjectMapper();
            T data =
                    mapper.readValue(value, ignored);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
