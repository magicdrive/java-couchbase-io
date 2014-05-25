package net.magicd.io.couchbase;

import net.magicd.io.couchbase.mock.TestData;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CouchBaseIOTest {

    static CouchBaseIO aio;

    static {
        try {
            aio = new CouchBaseIO(
                    "http://ubuntu-lts.local:8091/pools",
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
        aio.put("foo", td);
        TestData result = aio.get("foo", TestData.class);
        assertEquals(td.getId(), result.getId());
        assertEquals(td.getName(), result.getName());
    }
}

