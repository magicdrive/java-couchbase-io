package net.magicd.io.couchbase;

import com.couchbase.client.CouchbaseClient;
import lombok.Getter;
import net.magicd.io.couchbase.compress.Lzo;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    /**
     *
     */
    public enum CompressMode {
        LZO, GZIP
    }

    /**
     * the couchbase client entity
     */
    protected CouchbaseClient client;

    /**
     * well shutdown?
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
            throw new IOException(e);
        }
        this.client = new CouchbaseClient(couchBaseEndPoints, bucket, password);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(List<String> endpoints, String bucket, String password)
            throws IOException {
        List<URI> couchBaseEndPoints = new ArrayList<>();
        try {
            for (String endpoint : endpoints) {
                couchBaseEndPoints.add(new URI(endpoint));
            }
        } catch (URISyntaxException e) {
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
            if (!isHasBeenShutDown()) {
                shutdown();
            }
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
    public <T> boolean put(String key, Charset charset, T pojoInstance) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return client.set(
                    convertKey(key), new Lzo(charset).compress(mapper.writeValueAsString(pojoInstance))
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException(e);
        }
    }

    /**
     * Put java.lang.Object instance into couchbase.
     *
     * @param key
     */
    public <T> boolean put(String key, T pojoInstance) throws IOException {
        return  put(key, StandardCharsets.UTF_8, pojoInstance);
    }

    /**
     * @param key
     * @param klazz
     * @param <T>
     * @return ArrayList<(POJO class)>
     */
    public <T> T get(String key, Class<T> klazz) throws IOException {
        return get(key, StandardCharsets.UTF_8, klazz);
    }

    /**
     * @param key
     * @param klazz
     * @param <T>
     * @return ArrayList<(POJO class)>
     */
    public <T> T get(String key, Charset charSet, Class<T> klazz) throws IOException {
        String value = new Lzo(charSet).decompress((byte[]) client.get(convertKey(key)));
        ObjectMapper mapper = new ObjectMapper();
        T data = mapper.readValue(value, klazz);
        return data;
    }

    /**
     * @param key
     * @return
     */
    private static String convertKey(String key) {
        return key + new Lzo().getExtensionStr();
    }

}

