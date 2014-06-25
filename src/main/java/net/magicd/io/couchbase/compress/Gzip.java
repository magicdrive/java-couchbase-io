package net.magicd.io.couchbase.compress;

import lombok.Getter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Gzip class
 */
public class Gzip implements CompressAlgorithm {

    /**
     * algorithmName
     */
    @Getter
    private final String algorithmName = "gzip";

    /**
     * extensionStr
     */
    @Getter
    private final String extensionStr = ".gz";

    /**
     * charSetEncoding
     */
    private Charset charset = StandardCharsets.UTF_8;

    /**
     * constructor
     */
    public Gzip() {
    }

    /**
     * constructor
     */
    public Gzip(Charset charset) {
        this.charset = charset;
    }

    /**
     * compress String by gzip
     *
     * @param notCompressedStr
     * @return compressedGzipString
     */
    @Override
    public byte[] compress(String notCompressedStr) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);

        byte[] decompressedBytes = notCompressedStr.getBytes(this.charset);

        gzipOutputStream.write(decompressedBytes, 0, decompressedBytes.length);
        gzipOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * decompress gzipString
     *
     * @param compressedByteArray
     * @return decompressedGzipString
     */
    @Override
    public String decompress(byte[] compressedByteArray) throws IOException {
        GZIPInputStream gzipInputStream = new GZIPInputStream(
                new ByteArrayInputStream(compressedByteArray)
        );
        OutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[16384];
        int len;
        while ((len = gzipInputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        gzipInputStream.close();
        out.close();

        return out.toString();
    }
}
