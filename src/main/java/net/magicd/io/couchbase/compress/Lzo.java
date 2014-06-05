package net.magicd.io.couchbase.compress;

import lombok.Getter;
import org.anarres.lzo.LzoCompressor;
import org.anarres.lzo.LzoLibrary;
import org.anarres.lzo.LzopInputStream;
import org.anarres.lzo.LzopOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * LZO compressor
 *
 * @author Hiroshi IKEGAMI \<hiroshi.ikegami@magicdrive.jp\>
 */
public class Lzo {

    /** buffer size */
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    /** algorithmName */
    @Getter
    private static final String algorithmName = "lzo";

    /** extensionStr */
    @Getter
    private static final String extensionStr = ".lzo";


    /** constructor */
    private Lzo() {}

    /**
     * @param value
     * @return
     * @throws IOException
     */
    public static byte[] compress(String value) throws IOException {
        LzoCompressor compressor = LzoLibrary.getInstance().newCompressor(null, null);
        byte[] row_data = value.getBytes();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        LzopOutputStream lzopOutputStream = new LzopOutputStream(
                byteArrayOutputStream, compressor, DEFAULT_BUFFER_SIZE
        );
        lzopOutputStream.write(row_data);
        if (lzopOutputStream != null) {
            lzopOutputStream.close();
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * @param value
     * @return
     * @throws IOException
     */
    public static String decompress(byte[] value) throws IOException {
        ByteArrayInputStream lzo_fin = new ByteArrayInputStream(value);
        LzopInputStream lzo_zin = new LzopInputStream(lzo_fin);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int ir;
        byte[] data = new byte[16384];

        while ((ir = lzo_zin.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, ir);
        }
        buffer.flush();

        return new String(buffer.toByteArray());
    }
}
