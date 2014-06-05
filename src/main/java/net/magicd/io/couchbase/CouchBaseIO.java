package net.magicd.io.couchbase;

import com.couchbase.client.CouchbaseClient;
import lombok.Getter;
import net.magicd.io.couchbase.compress.Lzo;
import org.codehaus.jackson.map.ObjectMapper;

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
 * @author Hiroshi IKEGAMI \<hiroshi.ikegami@magicdrive.jp\>
 * @version 0.1
 */
public class CouchBaseIO {

    public enum JsonType {MULTI, SINGLE}

    /**
     * the couchbase client entity
     */
    protected CouchbaseClient client;

    /**
     * well shutdowned?
     */
    @Getter
    private boolean hasBeenShutDown = false;

    /**
     * couchbase bucketName
     */
    @Getter
    protected String bucketName;

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(URI endpoint, String bucket, String password)
            throws IOException {
        List<URI> couchBaseEndPoints = Arrays.asList(endpoint);
        this.client = new CouchbaseClient(couchBaseEndPoints, bucket, password);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(URI[] endpoints, String bucket, String password)
            throws IOException {
        List<URI> couchBaseEndPoints = Arrays.asList(endpoints);
        this.client = new CouchbaseClient(couchBaseEndPoints, bucket, password);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(String endpoint, String bucket, String password)
            throws IOException {
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
    public CouchBaseIO(String[] endpoints, String bucket, String password)
            throws IOException {
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
            if (!isHasBeenShutDown()) { shutdown(); }
        }
    }

    /**
     * shutdown client
     */
    public void shutdown() {
        client.shutdown();
        this.hasBeenShutDown = true;
    }

    /**
     * Put java.lang.Object instance into couchbase.
     *
     * @param key
     */
    public <T> boolean put(String key, T value) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return client.set(
                    convertKey(key), Lzo.compress( mapper.writeValueAsString(value) )
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException(e);
        }
    }

    /**
     * @param key
     * @param klazz
     * @param <T>
     * @return ArrayList<(POJO class)>
     */
    public <T> T get(String key, Class<T> klazz) {
        try {
            String value = Lzo.decompress((byte[])client.get(convertKey(key)));
            ObjectMapper mapper = new ObjectMapper();
            T data = mapper.readValue(Lzo.decompress((byte[])client.get(convertKey(key))), klazz);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @param key
     * @return
     */
    private String convertKey(String key) { return key+Lzo.getExtensionStr(); }

}

