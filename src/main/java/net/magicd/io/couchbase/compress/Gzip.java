package net.magicd.io.couchbase.compress;

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
    public static byte[] compress(String notCompressedStr, String encoding) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);

            byte[] decompressedBytes = notCompressedStr.getBytes();

            gzipOutputStream.write(decompressedBytes, 0, decompressedBytes.length);
            gzipOutputStream.close();

            return byteArrayOutputStream.toByteArray();
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


}
