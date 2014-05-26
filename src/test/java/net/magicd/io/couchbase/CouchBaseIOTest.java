package net.magicd.io.couchbase;

import net.magicd.io.couchbase.mock.TestData;
import org.ho.yaml.Yaml;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CouchBaseIOTest {

    private CouchBaseIO io;

    private static void p(Object obj) {
        System.err.println(obj.toString());
    }

    @Before
    public void init() {

        try {
            byte[] fileContentBytes = Files.readAllBytes(
                    Paths.get(
                            getClass().getClassLoader().getResource("couchbase.yaml").getPath()
                    )
            );
            String fileContentStr = new String(fileContentBytes, StandardCharsets.UTF_8);


            Map<String, String> config = (Map<String, String>) (
                    (Map) Yaml.load(fileContentStr)
            ).get("endpoint");

            String protocol = config.get("protocol"),
                   host = config.get("host"),
                   port = config.get("port"),
                   path = config.get("path");

            io = new CouchBaseIO(
                    String.format("%s://%s:%s%s", protocol, host, port, path),
                    "default",
                    ""
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("specified url not available.");
        }
    }

    @Test
    public void testGet() throws IOException {
        TestData td = new TestData() {
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
    }
}

