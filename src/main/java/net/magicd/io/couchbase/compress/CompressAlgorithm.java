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
     * @param encoding
     */
    public void setDeaultEncoding(String encoding);

    /**
     *
     * @param notCompressedStr
     * @param encoding
     * @return compressedByteArray
     */
    public byte[] compress(String notCompressedStr, String encoding) ;

    /**
     *
     * @param compressedByteArray
     * @param encoding
     * @return decompressedStr
     */
    public String decompress(byte[] compressedByteArray, String encoding) ;



    /**
     *
     * @param notCompressedStr
     * @return compressedByteArray
     */
    public byte[] compress(String notCompressedStr);


    /**
     *
     * @param compressedByteArray
     * @return decompressedStr
     */
    public String decompress(byte[] compressedByteArray) throws IOException;

}
