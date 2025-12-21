package com.luixtech.utilities.lang;

import java.util.ArrayList;
import java.util.List;

public abstract class ByteUtils {
    public static List<Byte> toList(byte[] array) {
        if (array == null) {
            return null;
        }

        List<Byte> list = new ArrayList<Byte>(array.length);

        for (byte value : array) {
            list.add(value);
        }

        return list;
    }

    public static byte[] toArray(List<Byte> list) {
        if (list == null) {
            return null;
        }

        byte[] array = new byte[list.size()];

        int index = 0;
        for (byte value : list) {
            array[index++] = value;
        }

        return array;
    }


    /**
     * Convert a long value to 8 bytes and put them into the byte array starting at the given offset (big-endian).
     *
     * @param value long value
     * @param bytes target byte array
     * @param off   start offset
     */
    public static void long2bytes(long value, byte[] bytes, int off) {
        bytes[off + 7] = (byte) value;
        bytes[off + 6] = (byte) (value >>> 8);
        bytes[off + 5] = (byte) (value >>> 16);
        bytes[off + 4] = (byte) (value >>> 24);
        bytes[off + 3] = (byte) (value >>> 32);
        bytes[off + 2] = (byte) (value >>> 40);
        bytes[off + 1] = (byte) (value >>> 48);
        bytes[off] = (byte) (value >>> 56);
    }

    /**
     * Convert 8 bytes starting at the given offset in a byte array to a long value (big-endian).
     *
     * @param bytes source byte array
     * @param off   start offset
     */
    public static long bytes2long(byte[] bytes, int off) {
        return ((bytes[off + 7] & 0xFFL)) + ((bytes[off + 6] & 0xFFL) << 8) + ((bytes[off + 5] & 0xFFL) << 16)
                + ((bytes[off + 4] & 0xFFL) << 24) + ((bytes[off + 3] & 0xFFL) << 32) + ((bytes[off + 2] & 0xFFL) << 40)
                + ((bytes[off + 1] & 0xFFL) << 48) + (((long) bytes[off]) << 56);
    }

    /**
     * Convert an int value to 4 bytes and put them into the byte array starting at the given offset (big-endian).
     *
     * @param value int value
     * @param bytes target byte array
     * @param off   start offset
     */
    public static void int2bytes(int value, byte[] bytes, int off) {
        bytes[off + 3] = (byte) value;
        bytes[off + 2] = (byte) (value >>> 8);
        bytes[off + 1] = (byte) (value >>> 16);
        bytes[off] = (byte) (value >>> 24);
    }

    /**
     * Convert 4 bytes starting at the given offset in a byte array to an int value (big-endian).
     *
     * @param bytes source byte array
     * @param off   start offset
     */
    public static int bytes2int(byte[] bytes, int off) {
        return ((bytes[off + 3] & 0xFF)) + ((bytes[off + 2] & 0xFF) << 8) + ((bytes[off + 1] & 0xFF) << 16) + ((bytes[off]) << 24);
    }

    /**
     * Convert a short value to 2 bytes and put them into the byte array starting at the given offset (big-endian).
     *
     * @param value short value
     * @param bytes target byte array
     * @param off   start offset
     */
    public static void short2bytes(short value, byte[] bytes, int off) {
        bytes[off + 1] = (byte) value;
        bytes[off] = (byte) (value >>> 8);
    }

    /**
     * Convert 2 bytes starting at the given offset in a byte array to a short value (big-endian).
     *
     * @param b   source byte array
     * @param off start offset
     */
    public static short bytes2short(byte[] b, int off) {
        return (short) (((b[off + 1] & 0xFF)) + ((b[off] & 0xFF) << 8));
    }
}
