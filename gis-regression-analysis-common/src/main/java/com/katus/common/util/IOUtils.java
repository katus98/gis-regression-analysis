package com.katus.common.util;

import java.io.*;

/**
 * @author SUN Katus
 * @version 1.0, 2021-06-25
 */
public final class IOUtils {
    private static final int BUFFER_SIZE = 4096;

    /**
     * 执行 IO (从输入流向输出流复制字节)
     * @param is 输入流
     * @param os 输出流
     * @param bufferSize 缓冲大小 (单位为字节)
     * @throws IOException IO异常
     */
    public static void copyBytes(InputStream is, OutputStream os, int bufferSize, boolean close) throws IOException {
        PrintStream ps = os instanceof PrintStream ? (PrintStream) os : null;
        byte[] buffer = new byte[bufferSize];
        for (int bytesRead = is.read(buffer); bytesRead >= 0; bytesRead = is.read(buffer)) {
            os.write(buffer, 0, bytesRead);
            if (ps != null && ps.checkError()) {
                throw new IOException("Unable to write to print stream.");
            }
        }
        if (close) {
            closeAll(is, os);
        }
    }

    public static void copyBytes(InputStream is, OutputStream os, int bufferSize) throws IOException {
        copyBytes(is, os, bufferSize, false);
    }

    public static void copyBytes(InputStream is, OutputStream os) throws IOException {
        copyBytes(is, os, BUFFER_SIZE);
    }

    /**
     * 执行 IO (从输入流向输出流复制字符)
     * @param reader 字符输入流
     * @param writer 字符输出流
     * @param bufferSize 缓冲大小 (单位为字符)
     * @throws IOException IO异常
     */
    public static void copyChars(Reader reader, Writer writer, int bufferSize, boolean close) throws IOException {
        char[] buffer = new char[bufferSize];
        for (int bytesRead = reader.read(buffer); bytesRead >= 0; bytesRead = reader.read(buffer)) {
            writer.write(buffer, 0, bytesRead);
        }
        if (close) {
            closeAll(reader, writer);
        }
    }

    public static void copyChars(Reader reader, Writer writer, int bufferSize) throws IOException {
        copyChars(reader, writer, bufferSize, false);
    }

    public static void copyChars(Reader reader, Writer writer) throws IOException {
        copyChars(reader, writer, BUFFER_SIZE);
    }

    public static InputStream bytesToStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    public static byte[] streamToBytes(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        copyBytes(is, bos);
        return bos.toByteArray();
    }

    public static Reader charsToReader(char[] chars) {
        return new CharArrayReader(chars);
    }

    public static char[] readerToChars(Reader reader) throws IOException {
        CharArrayWriter writer = new CharArrayWriter();
        copyChars(reader, writer);
        return writer.toCharArray();
    }

    public static Reader stringToReader(String content) {
        return new StringReader(content);
    }

    public static String readerToString(Reader reader) throws IOException {
        StringWriter writer = new StringWriter();
        copyChars(reader, writer);
        return writer.toString();
    }

    public static BufferedInputStream toBufferedInputStream(InputStream is) {
        return is instanceof BufferedInputStream ? (BufferedInputStream) is : new BufferedInputStream(is);
    }

    public static BufferedOutputStream toBufferedOutputStream(OutputStream os) {
        return os instanceof BufferedOutputStream ? (BufferedOutputStream) os : new BufferedOutputStream(os);
    }

    public static BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public static BufferedWriter toBufferedWriter(Writer writer) {
        return writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer);
    }

    public static Reader streamToReader(InputStream is) {
        return new BufferedReader(new InputStreamReader(is));
    }

    public static Writer streamToWriter(OutputStream os) {
        return new BufferedWriter(new OutputStreamWriter(os));
    }

    /**
     * 关闭可关闭对象
     * @param closeables 可关闭对象
     */
    public static void closeAll(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
