package net.magicd.io.kvs.couchbase;

import com.couchbase.client.CouchbaseClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CouchBaseAIO {

    public static void main(String[] args)
            throws URISyntaxException, IOException,
                   ExecutionException, InterruptedException {

        // (Subset) of nodes in the cluster to establish a connection
        List<URI> hosts = Arrays.asList(
                new URI("http://127.0.0.1:8091/pools")
        );

        // Name of the Bucket to connect to
        String bucket = "default";

        // Password of the bucket (empty) string if none
        String password = "";

        // Connect to the Cluster
        CouchbaseClient client = new CouchbaseClient(hosts, bucket, password);

        // Store a Document
        client.set("my-first-document", "Hello Couchbase!").get();

        // Retreive the Document and print it
        System.out.println(client.get("my-first-document"));

        // Shutting down properly
        client.shutdown();
    }
}
