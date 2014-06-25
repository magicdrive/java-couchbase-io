package net.magicd.io.couchbase.compress;

import java.io.IOException;

/**
 * compress utility interface.
 *
 *
 * @author Hiroshi IKEGAMI \<hiroshi.ikegami@magicdrive.jp\>
 *
 */
public interface CompressAlgorithm {

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
     * @return
     */
    public String getAlgorithmName();

    /**
     *
     * @return
     */
    public String getExtensionStr();

}
