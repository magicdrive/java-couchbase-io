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
 * @author Hiroshi IKEGAMI - hiroshi.ikegami at magicdrive.jp
 */
public class CouchBaseIO extends CouchbaseClient {

    /**
     * enum: compress mode
     */
    public enum CompressMode {
        LZO, GZIP, NONE
    }

    /**
     * default compress mode (lzo)
     */
    @Getter
    protected static final CompressMode defaultCompressMode = CompressMode.LZO;

    /**
     * current compress mode.
     */
    @Getter
    protected CompressMode compressMode = null;

    /**
     * algorithm class instance.
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
     * current algorithm name getter.
     *
     * @return algorithmName
     * @see net.magicd.io.couchbase.compress.CompressAlgorithm#getAlgorithmName()
     */
    public String getCompressAlgorithmName() {
        return algorithm.getAlgorithmName();
    }

    /**
     * current algorithm name getter.
     *
     * @return extensionStr
     * @see net.magicd.io.couchbase.compress.CompressAlgorithm#getExtensionStr()
     */
    public String getCompressExtensionStr() {
        return algorithm.getExtensionStr();
    }


    /**
     * constructor.
     *
     * @param endpoint
     * @param bucketName
     * @param password
     * @throws IOException
     */
    public CouchBaseIO(URI endpoint, String bucketName, String password) throws IOException {
        super(Arrays.asList(endpoint), bucketName, password);
        setupCompressAlgorithm(defaultCompressMode);
    }

    /**
     * constructor.
     *
     * @param endpoints
     * @param bucketName
     * @param password
     * @throws IOException
     */
    public CouchBaseIO(URI[] endpoints, String bucketName, String password) throws IOException {
        super(Arrays.asList(endpoints), bucketName, password);
        setupCompressAlgorithm(defaultCompressMode);
    }

    /**
     * constructor.
     *
     * @param endpoint
     * @param bucket
     * @param password
     * @throws IOException
     * @throws URISyntaxException
     */
    public CouchBaseIO(String endpoint, String bucket, String password) throws IOException, URISyntaxException {
        super(Arrays.asList(new URI(endpoint)), bucket, password);
        setupCompressAlgorithm(defaultCompressMode);
    }

    /**
     * constructor.
     *
     * @param endpoint
     * @param bucketName
     * @param password
     * @param compressMode
     * @throws IOException
     */
    public CouchBaseIO(URI endpoint, String bucketName, String password, CompressMode compressMode) throws IOException {
        super(Arrays.asList(endpoint), bucketName, password);
        setupCompressAlgorithm(compressMode);
    }

    /**
     * constructor.
     *
     * @param endpoints
     * @param bucketName
     * @param password
     * @param compressMode
     * @throws IOException
     */
    public CouchBaseIO(URI[] endpoints, String bucketName, String password, CompressMode compressMode) throws IOException {
        super(Arrays.asList(endpoints), bucketName, password);
        setupCompressAlgorithm(compressMode);
    }

    /**
     * constructor.
     *
     * @param endpoint
     * @param bucket
     * @param password
     * @param compressMode
     * @throws IOException
     * @throws URISyntaxException
     */
    public CouchBaseIO(String endpoint, String bucket, String password, CompressMode compressMode) throws IOException, URISyntaxException {
        super(Arrays.asList(new URI(endpoint)), bucket, password);
        setupCompressAlgorithm(compressMode);
    }

    /**
     * setup to CouchbaseIO#algorithm via compressMode arg.
     *
     * @param compressMode
     */
    protected void setupCompressAlgorithm(CompressMode compressMode) {
        this.compressMode = compressMode;
        switch (compressMode) {
            case GZIP:
                this.algorithm = new Gzip();
                break;
            case LZO:
                this.algorithm = new Lzo();
                break;
            case NONE:
                this.algorithm = new NoCompress();
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
     * Put POJO instance into couchbase.
     *
     * @param key
     * @param key
     * @param pojoInstance
     * @param <T>
     * @return result
     */
    public <T> boolean put(String key, T pojoInstance) throws IOException {
        return put(key, StandardCharsets.UTF_8, pojoInstance);
    }


    /**
     * Put POJO instance into couchbase.
     *
     * @param key
     * @param charset
     * @param pojoInstance
     * @param <T>
     * @return result
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
     * Get data via Couchbase, and mount on <T>klazz POJO instance.
     *
     * @param key
     * @param klazz
     * @param <T>
     * @return ArrayList<(POJO class)>
     */
    public <T> T get(String key, Class<T> klazz) throws IOException {
        return get(key, StandardCharsets.UTF_8, klazz);
    }

    /**
     * Get data via Couchbase, and mount on <T>klazz POJO instance.
     *
     * @param key
     * @param charset
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
     * key convert to couchbase real key.(binding compress-type)
     *
     * @param key
     * @return convertedKey
     */
    private String convertKey(String key) {
        return key + getCompressExtensionStr();
    }
}

