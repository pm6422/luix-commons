package com.luixtech.utilities.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class ZipUtils {

    public static byte[] gzip(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(data);
            gzip.finish();
            return bos.toByteArray();
        }
    }

    public static byte[] unGzip(byte[] data) throws IOException {
        try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data))) {
            byte[] buf = new byte[2048];
            int size;
            ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length + 1024);
            while ((size = gzip.read(buf, 0, buf.length)) != -1) {
                bos.write(buf, 0, size);
            }
            return bos.toByteArray();
        }
    }
}
