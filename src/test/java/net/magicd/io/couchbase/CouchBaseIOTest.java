package net.magicd.io.couchbase;

import net.magicd.io.couchbase.mock.TestData;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static net.magicd.io.couchbase.CouchBaseIO.CompressMode;
import static net.magicd.io.couchbase.CouchBaseIO.CompressMode.*;
import static org.junit.Assert.assertEquals;

/**
 * CouchbaseIO Test class
 *
 * @author Hiroshi IKEGAMI - hiroshi.ikegami at magicdrive.jp
 * @see net.magicd.io.couchbase.CouchBaseIO
 */
public class CouchBaseIOTest {

    /**
     * couchbase endpoint config file.
     */
    private static final String filename = "couchbase.yaml";

    /**
     * debug print method.
     * @param objs
     */
    private static void p(Object[] objs) {
        for (Object obj : objs) {
            System.err.println(obj.toString());
        }
    }

    /**
     * get couchBaseServerURL from filename defined CouchbaseIOTest#filename field.
     *
     * @return couchBaseServerURL
     * @see net.magicd.io.couchbase.CouchBaseIOTest#filename
     */
    public String getCouchBaseServerURL() {
        try {
            byte[] fileContentBytes = Files.readAllBytes(
                    Paths.get(
                            getClass().getClassLoader().getResource(filename).getPath()
                    )
            );

            String fileContentStr = new String(fileContentBytes, StandardCharsets.UTF_8);

            Map config = (Map) (
                    (Map) new Yaml().load(fileContentStr)
            ).get("endpoint");

            String protocol = config.get("protocol").toString(),
                    host = config.get("host").toString(),
                    port = config.get("port").toString(),
                    path = config.get("path").toString();

            return String.format("%s://%s:%d%s", protocol, host, new Integer(port), path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("test/resources/couchbase.yaml load error.", e);
        }
    }


    /**
     * put and get test utility.
     *
     * @param io
     */
    public void execTestGet(CouchBaseIO io) {
        try {
            final TestData td = new TestData() {
                public TestData init() {
                    setId(1);
                    setName("tiger");
                    setAge(10);
                    setGender("male");
                    return this;
                }
            }.init();
            io.put("foo", td);
            TestData result = io.get("foo", TestData.class);
            assertEquals(td.getId(), result.getId());
            assertEquals(td.getName(), result.getName());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * get method test: compress algorithm is default .
     *
     * @throws IOException
     */
    @Test
    public void testGetDefault() throws IOException {
        final String serverUrl = getCouchBaseServerURL(),
                bucket = "default",
                passwd = "";
        try {
            execTestGet(new CouchBaseIO(serverUrl, bucket, passwd));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException("specified url is not available.", e);
        }
    }

    /**
     * get method test: args in compress mode.
     *
     * @throws IOException
     */
    @Test
    public void testGet() throws IOException {
        final String serverUrl = getCouchBaseServerURL(),
                bucket = "default",
                passwd = "";
        try {
            for (CompressMode compressMode : new CompressMode[]{NONE, GZIP, LZO}) {
                execTestGet(new CouchBaseIO(serverUrl, bucket, passwd, compressMode));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException("specified url is not available.", e);
        }
    }

    /**
     * shutdown test.
     */
    @Test
    public void shutdownTest() {
        try {
            CouchBaseIO io = new CouchBaseIO(getCouchBaseServerURL(), "default", "");
            io.shutdown();
            assertEquals(io.isHasBeenShutDown(), true);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException("specified url is not available.", e);
        }
    }

    /**
     * constructor test.
     */
    @Test
    public void constructorTest() {
        final String serverUrl = getCouchBaseServerURL(),
                bucket = "default",
                passwd = "";
        try {
            for (CouchBaseIO couchbase : new CouchBaseIO[]{
                    new CouchBaseIO(serverUrl, bucket, passwd),
                    new CouchBaseIO(new URI(serverUrl), bucket, passwd),
                    new CouchBaseIO(new URI[]{new URI(serverUrl)}, bucket, passwd)
            }) {
                assertEquals(couchbase.isHasBeenShutDown(), false);
                couchbase.shutdown();
                assertEquals(couchbase.isHasBeenShutDown(), true);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

