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

public class CouchBaseIOTest {

    private static final String filename = "couchbase.yaml";

    private static void p(Object[] objs) {
        for (Object obj : objs) {
            System.err.println(obj.toString());
        }
    }

    /**
     * @return couchBaseServerURL
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
            throw new RuntimeException("specified url not available.", e);
        }
    }

    /**
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
            throw new RuntimeException("specified url not available.", e);
        }
    }

    /**
     *
     */
    @Test
    public void testShutdown() {
        try {
            CouchBaseIO io = new CouchBaseIO(getCouchBaseServerURL(), "default", "");
            io.shutdown();
            assertEquals(io.isHasBeenShutDown(), true);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException("specified url not available.", e);
        }
    }

    /**
     *
     */
    @Test
    public void constructorTest() {
        final String serverUrl = getCouchBaseServerURL(),
                bucket = "default",
                passwd = "";
        try {
            for (CouchBaseIO v : new CouchBaseIO[]{
                    new CouchBaseIO(serverUrl, bucket, passwd),
                    new CouchBaseIO(new URI(serverUrl), bucket, passwd),
                    new CouchBaseIO(new URI[]{new URI(serverUrl)}, bucket, passwd)
            }) {
                assertEquals(v.isHasBeenShutDown(), false);
                v.shutdown();
                assertEquals(v.isHasBeenShutDown(), true);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

