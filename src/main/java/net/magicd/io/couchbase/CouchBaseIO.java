package net.magicd.io.couchbase;

import com.couchbase.client.CouchbaseClient;
import lombok.Getter;
import net.magicd.io.couchbase.compress.CompressAlgorithm;
import net.magicd.io.couchbase.compress.Gzip;
import net.magicd.io.couchbase.compress.Lzo;
import net.magicd.io.couchbase.compress.NoCompress;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * A CouchBaseClient Wrapper.
 *
 * @author Hiroshi IKEGAMI \<hiroshi.ikegami@magicdrive.jp\>
 * @version 0.1
 */
public class CouchBaseIO extends CouchbaseClient {

    /**
     *
     */
    public enum CompressMode {
        LZO, GZIP, NONE
    }

    /**
     *
     */
    @Getter
    protected static final CompressMode defaultCompressMode = CompressMode.LZO;

    /**
     *
     */
    @Getter
    protected CompressMode compressMode = null;

    /**
     *
     */
    protected CompressAlgorithm algorithm = null;

    /**
     * well shutdown?
     */
    @Getter
    protected boolean hasBeenShutDown = false;

    /**
     * couchbase bucketName
     */
    @Getter
    protected String bucketName;

    /**
     * @return
     */
    public String getCompressAlgorithmName() {
        return algorithm.getAlgorithmName();
    }

    /**
     * @return
     */
    public String getCompressExtensionStr() {
        return algorithm.getExtensionStr();
    }


    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(URI endpoint, String bucketName, String password) throws IOException {
        super(Arrays.asList(endpoint), bucketName, password);
        setupCompressAlgorithm(defaultCompressMode);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(URI[] endpoints, String bucketName, String password) throws IOException {
        super(Arrays.asList(endpoints), bucketName, password);
        setupCompressAlgorithm(defaultCompressMode);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(String endpoint, String bucket, String password) throws IOException, URISyntaxException {
        super(Arrays.asList(new URI(endpoint)), bucket, password);
        setupCompressAlgorithm(defaultCompressMode);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(URI endpoint, String bucketName, String password, CompressMode compressMode) throws IOException {
        super(Arrays.asList(endpoint), bucketName, password);
        setupCompressAlgorithm(compressMode);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(URI[] endpoints, String bucketName, String password, CompressMode compressMode) throws IOException {
        super(Arrays.asList(endpoints), bucketName, password);
        setupCompressAlgorithm(compressMode);
    }

    /**
     * constructor.
     *
     * @throws IOException
     */
    public CouchBaseIO(String endpoint, String bucket, String password, CompressMode compressMode) throws IOException, URISyntaxException {
        super(Arrays.asList(new URI(endpoint)), bucket, password);
        setupCompressAlgorithm(compressMode);
    }

    /**
     * @param compressMode
     */
    protected void setupCompressAlgorithm(CompressMode compressMode) {
        this.compressMode = compressMode;
        switch (compressMode) {
            case GZIP:
                algorithm = new Gzip();
                break;
            case LZO:
                algorithm = new Lzo();
                break;
            case NONE:
                algorithm = new NoCompress();
                break;
        }
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
                this.shutdown();
            }
        }
    }

    /**
     * shutdown client
     */
    public void shutdown() {
        super.shutdown();
        this.hasBeenShutDown = true;
    }

    /**
     * Put java.lang.Object instance into couchbase.
     *
     * @param key
     */
    public <T> boolean put(String key, T pojoInstance) throws IOException {
        return put(key, StandardCharsets.UTF_8, pojoInstance);
    }


    /**
     * Put java.lang.Object instance into couchbase.
     *
     * @param key
     */
    public <T> boolean put(String key, Charset charset, T pojoInstance) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return set(
                    convertKey(key), algorithm.compress(mapper.writeValueAsString(pojoInstance), charset)
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
    public <T> T get(String key, Class<T> klazz) throws IOException {
        return get(key, StandardCharsets.UTF_8, klazz);
    }

    /**
     * @param key
     * @param klazz
     * @param <T>
     * @return ArrayList<(POJO class)>
     */
    public <T> T get(String key, Charset charset, Class<T> klazz) throws IOException {
        String value = algorithm.decompress((byte[]) get(convertKey(key)), charset);
        ObjectMapper mapper = new ObjectMapper();
        T data = mapper.readValue(value, klazz);
        return data;
    }

    /**
     * @param key
     * @return
     */
    private String convertKey(String key) {
        return key + getCompressExtensionStr();
    }
}

