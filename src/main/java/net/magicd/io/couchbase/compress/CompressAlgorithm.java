package net.magicd.io.couchbase.compress;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * compress algorithm classes interface.
 *
 * @author Hiroshi IKEGAMI - hiroshi.ikegami at magicdrive.jp
 */
public interface CompressAlgorithm {

    /**
     * encoding
     */
    public static final Charset defaultCharset = StandardCharsets.UTF_8;

    /**
     * compress String to byte[]
     *
     * @param notCompressedStr
     * @return compressedByteArray
     */
    public byte[] compress(String notCompressedStr) throws IOException;

    /**
     * decompress compressedByteArray to String
     *
     * @param compressedByteArray
     * @return decompressedStr
     */
    public String decompress(byte[] compressedByteArray) throws IOException;

    /**
     * compress String to byte[]
     *
     * @param notCompressedStr
     * @return compressedByteArray
     */
    public byte[] compress(String notCompressedStr, Charset charset) throws IOException;

    /**
     * decompress compressedByteArray to String
     *
     * @param compressedByteArray
     * @return decompressedStr
     */
    public String decompress(byte[] compressedByteArray, Charset charset) throws IOException;

    /**
     * getter algorithmName
     *
     * @return
     */
    public String getAlgorithmName();

    /**
     * getter extensionStr
     *
     * @return
     */
    public String getExtensionStr();

}
