package net.magicd.io.kvs.couchbase;

import com.couchbase.client.CouchbaseClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

    /**
     * the couchbase client entity
     */
    private CouchbaseClient client;

    /**
     * default constructor.
     *
     * @throws IOException
     */
    public CouchBaseAIO(String bucket, String password) throws IOException {
        List<URI> couchBaseEndPoints = null;
        try {
            couchBaseEndPoints = Arrays.asList(
                    new URI("http://ubuntu-lts.local:8091/pools")
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        client = new CouchbaseClient(couchBaseEndPoints, bucket, password);
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
     * put
     *
     * @param key
     * @param value
     * @return
     */
    public boolean put(String key, String value) {
        try {
            return client.set(key, value).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get
     *
     * @param key
     */
    public Object get(String key) {
        return client.get(key);
    }


}
