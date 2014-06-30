package net.magicd.io.couchbase.compress;

import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
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
     * compress String with gzip format.
     *
     * @param notCompressedStr
     * @return compressedGzipString
     */
    @Override
    public byte[] compress(String notCompressedStr) throws IOException {
        return compress(notCompressedStr, defaultCharset);
    }

    /**
     * decompress gzipedByteArray.
     *
     * @param compressedByteArray
     * @return decompressedGzipString
     */
    @Override
    public String decompress(byte[] compressedByteArray) throws IOException {
        return decompress(compressedByteArray, defaultCharset);
    }

    /**
     * compress String with gzip format.
     *
     * @param notCompressedStr
     * @param charset
     * @return
     * @throws IOException
     */
    @Override
    public byte[] compress(String notCompressedStr, Charset charset) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);

        byte[] decompressedBytes = notCompressedStr.getBytes(charset);

        gzipOutputStream.write(decompressedBytes, 0, decompressedBytes.length);
        gzipOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * decompress gzipedByteArray.
     *
     * @param compressedByteArray
     * @param charset
     * @return
     * @throws IOException
     */
    @Override
    public String decompress(byte[] compressedByteArray, Charset charset) throws IOException {
        GZIPInputStream gzipInputStream = new GZIPInputStream(
                new ByteArrayInputStream(compressedByteArray)
        );
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] buf = new byte[16384];
        int len;
        while ((len = gzipInputStream.read(buf)) > 0) {
            buffer.write(buf, 0, len);
        }
        gzipInputStream.close();
        buffer.close();

        return new String(buffer.toByteArray(), charset);
    }
}
