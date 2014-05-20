package net.magicd.io.kvs.couchbase;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Gzip class
 */
public class Gzip {

    /**
     * compress String by gzip
     *
     * @param notCompressedStr
     * @param encoding
     * @return compressedGzipString
     */
    public static String compress(String notCompressedStr, String encoding) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);

            byte[] decompressedBytes = notCompressedStr.getBytes();

            gzipOutputStream.write(decompressedBytes, 0, decompressedBytes.length);
            gzipOutputStream.close();

            return byteArrayToString(byteArrayOutputStream.toByteArray(), encoding);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * decompress gzipString
     *
     * @param gzipString
     * @return decompressedGzipString
     */
    public static String decompress(String gzipString, String encoding) {
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(
                    new ByteArrayInputStream(gzipString.getBytes(encoding))
            );
            OutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            gzipInputStream.close();
            out.close();

            return out.toString();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * convert byte[] to java.lang.String
     *
     * @param bytes
     * @param encoding
     * @return string
     */
    private static String byteArrayToString(byte[] bytes, String encoding) {
        try {
            return new String(bytes, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * compress String by gzip
     *
     * @see net.magicd.io.kvs.couchbase.Gzip#compressUTF_8(String)
     * @param notCompressedString
     * @return compressedGzipString
     */
    public static String compress(String notCompressedString) {
        return compressUTF_8(notCompressedString);
    }

    /**
     * decompress gzipString
     *
     * @see net.magicd.io.kvs.couchbase.Gzip#decompressUTF_8(String)
     * @param gzipString
     * @return decompressedGzipString
     */
    public static String decompress(String gzipString) {
        return decompressUTF_8(gzipString);
    }

    /**
     * compress String(UTF-8) by gzip
     *
     * @see net.magicd.io.kvs.couchbase.Gzip#compress(String, String)
     * @param notCompressedString
     * @return compressedGzipString
     */
    public static String compressUTF_8(String notCompressedString) {
        return compress(notCompressedString, "UTF-8");
    }

    /**
     * decompress gzipString(UTF-8)
     *
     * @see net.magicd.io.kvs.couchbase.Gzip#decompress(String, String)
     * @param gzipString
     * @return decompressedGzipString
     */
    public static String decompressUTF_8(String gzipString) {
        return decompress(gzipString, "UTF-8");
    }
}
