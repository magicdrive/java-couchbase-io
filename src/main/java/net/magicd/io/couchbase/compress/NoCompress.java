package net.magicd.io.couchbase.compress;

import lombok.Getter;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * no compress algorithm class
 *
 * don't compress and decompress, but String <-> byteArray convert.
 *
 *
 * @author Hiroshi IKEGAMI \<hiroshi.ikegami@magicdrive.jp\>
 */
public class NoCompress implements CompressAlgorithm {

    /**
     * algorithm name
     */
    @Getter
    private String algorithmName = "nocompress";

    /**
     * extensin string
     *
     */
    @Getter
    private String extensionStr = "";

    /**
     * don't compress, return String#getBytes
     *
     * @param notCompressedStr
     * @return byte[]
     * @throws IOException
     */
    @Override
    public byte[] compress(String notCompressedStr) throws IOException {
        return compress(notCompressedStr, defaultCharset);
    }

    /**
     * don't decompress, return new String(byte[])
     *
     * @param compressedByteArray
     * @return String
     * @throws IOException
     */
    @Override
    public String decompress(byte[] compressedByteArray) throws IOException {
        return decompress(compressedByteArray, defaultCharset);
    }

    /**
     * don't compress, return String#getBytes
     *
     * @param notCompressedStr
     * @param charset
     * @return byte[]
     * @throws IOException
     */
    @Override
    public byte[] compress(String notCompressedStr, Charset charset) throws IOException {
        return notCompressedStr.getBytes(charset);
    }

    /**
     * don't decompress, return new String(byte[])
     *
     * @param compressedByteArray
     * @param charset
     * @return String
     * @throws IOException
     */
    @Override
    public String decompress(byte[] compressedByteArray, Charset charset) throws IOException {
        return new String(compressedByteArray, charset);
    }

}
