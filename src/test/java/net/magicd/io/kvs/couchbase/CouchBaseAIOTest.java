package net.magicd.io.kvs.couchbase;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CouchBaseAIOTest {

    @Test
    public void testGet() throws IOException {
        CouchBaseAIO aio = new CouchBaseAIO("default", "");
        aio.put("foo", "bar");
        String result =  aio.get("foo").toString();
        System.out.println(result);
        assertEquals("bar",result);
    }

}
