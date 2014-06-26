package net.magicd.io.couchbase.compress;

import lombok.Getter;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 */
public class NoCompress implements CompressAlgorithm {

    /**
     *
     */
    @Getter
    private String algorithmName = "nocompress";

    /**
     *
     */
    @Getter
    private String extensionStr = "";

    /**
     * @param notCompressedStr
     * @return
     * @throws IOException
     */
    @Override
    public byte[] compress(String notCompressedStr) throws IOException {
        return compress(notCompressedStr, defaultCharset);
    }

    /**
     * @param compressedByteArray
     * @return
     * @throws IOException
     */
    @Override
    public String decompress(byte[] compressedByteArray) throws IOException {
        return decompress(compressedByteArray, defaultCharset);
    }

    /**
     * @param notCompressedStr
     * @param charset
     * @return
     * @throws IOException
     */
    @Override
    public byte[] compress(String notCompressedStr, Charset charset) throws IOException {
        return notCompressedStr.getBytes(charset);
    }

    /**
     * @param compressedByteArray
     * @param charset
     * @return
     * @throws IOException
     */
    @Override
    public String decompress(byte[] compressedByteArray, Charset charset) throws IOException {
        return new String(compressedByteArray, charset);
    }

}
