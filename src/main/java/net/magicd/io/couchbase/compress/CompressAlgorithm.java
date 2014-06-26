package net.magicd.io.couchbase.compress;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * compress utility interface.
 *
 *
 * @author Hiroshi IKEGAMI \<hiroshi.ikegami@magicdrive.jp\>
 *
 */
public interface CompressAlgorithm {

    /**
     * encoding
     */
    public static final Charset defaultCharset = StandardCharsets.UTF_8;

    /**
     *
     * @param notCompressedStr
     * @return compressedByteArray
     */
    public byte[] compress(String notCompressedStr) throws IOException;

    /**
     *
     * @param compressedByteArray
     * @return decompressedStr
     */
    public String decompress(byte[] compressedByteArray) throws IOException;

    /**
     *
     * @param notCompressedStr
     * @return compressedByteArray
     */
    public byte[] compress(String notCompressedStr, Charset charset) throws IOException;

    /**
     *
     * @param compressedByteArray
     * @return decompressedStr
     */
    public String decompress(byte[] compressedByteArray, Charset charset) throws IOException;

    /**
     *
     * @return
     */
    public String getAlgorithmName();

    /**
     *
     * @return
     */
    public String getExtensionStr();

}
