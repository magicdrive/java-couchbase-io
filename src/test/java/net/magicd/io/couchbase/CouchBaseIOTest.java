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
        TestData td = new TestData();
        td.setId(1);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10000; i++) { sb.append("hello:"); }

        td.setName(sb.toString());
        aio.put("foo", td);
        TestData result = aio.get("foo", TestData.class);
        System.err.println(result.getName());
        System.err.println("the String is " + result.getName().getBytes().length + "byte.");
        assertEquals(1, result.getId());
        assertEquals(sb.toString(), result.getName());
    }
}

