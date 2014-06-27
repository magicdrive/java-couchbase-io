package net.magicd.io.couchbase.compress;

import lombok.Getter;
import org.anarres.lzo.LzoCompressor;
import org.anarres.lzo.LzoLibrary;
import org.anarres.lzo.LzopInputStream;
import org.anarres.lzo.LzopOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Lzo compressor
 *
 * @author Hiroshi IKEGAMI \<hiroshi.ikegami@magicdrive.jp\>
 */
public class Lzo implements CompressAlgorithm {

    /**
     * buffer size
     */
    private static final int bufferSize = 4096;

    /**
     * algorithmName
     */
    @Getter
    private final String algorithmName = "lzo";

    /**
     * extensionStr
     */
    @Getter
    private final String extensionStr = ".lzo";


    /**
     * constructor
     */
    public Lzo() {
    }

    /**
     * @param value
     * @return
     * @throws IOException
     */
    public byte[] compress(String value) throws IOException {
        return compress(value, defaultCharset);
    }

    /**
     *
     * @param notCompressedStr
     * @return
     * @throws IOException
     */
    public String decompress(byte[] notCompressedStr) throws IOException {
        return decompress(notCompressedStr, defaultCharset);
    }

    /**
     *
     * @param notCompressedStr
     * @param charset
     * @return
     * @throws IOException
     */
    @Override
    public byte[] compress(String notCompressedStr, Charset charset) throws IOException {
        LzoCompressor compressor = LzoLibrary.getInstance().newCompressor(null, null);
        byte[] row_data = notCompressedStr.getBytes(charset);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        LzopOutputStream lzopOutputStream = new LzopOutputStream(
                byteArrayOutputStream, compressor, bufferSize
        );
        lzopOutputStream.write(row_data);
        if (lzopOutputStream != null) {
            lzopOutputStream.close();
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     *
     * @param compressedByteArray
     * @param charset
     * @return
     * @throws IOException
     */
    @Override
    public String decompress(byte[] compressedByteArray, Charset charset) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedByteArray);
        LzopInputStream lzopInputStream = new LzopInputStream(byteArrayInputStream);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int ir;
        byte[] data = new byte[16384];

        while ((ir = lzopInputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, ir);
        }
        buffer.flush();

        return new String(buffer.toByteArray(), charset);
    }
}

