package com.katus.common.io;

import com.katus.common.util.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-05-24
 */
public interface FsManipulator {

    /**
     * 文件/目录是否存在
     *
     * @param path 路径
     * @return 是否存在
     * @throws IOException IO异常
     */
    boolean exists(String path) throws IOException;

    /**
     * 路径是否为文件
     *
     * @param path 路径
     * @return 是否为文件
     * @throws IOException IO异常
     */
    boolean isFile(String path) throws IOException;

    /**
     * 路径是否为目录
     *
     * @param path 路径
     * @return 是否为目录
     * @throws IOException IO异常
     */
    default boolean isDirectory(String path) throws IOException {
        return !this.isFile(path);
    }

    /**
     * 罗列路径下的所有子路径 (单层)
     *
     * @param path 路径
     * @return 路径数组
     * @throws IOException IO异常
     */
    String[] list(String path) throws IOException;

    /**
     * 创建目录
     *
     * @param path 目录路径
     * @return 是否成功
     * @throws IOException IO异常
     */
    boolean makeDirectory(String path) throws IOException;

    /**
     * 创建多级目录
     *
     * @param path 目录路径
     * @return 是否成功
     * @throws IOException IO异常
     */
    boolean makeDirectories(String path) throws IOException;

    /**
     * 创建文件
     *
     * @param path 文件路径
     * @return 是否成功
     * @throws IOException IO异常
     */
    boolean createFile(String path) throws IOException;

    /**
     * 级联删除所有路径
     *
     * @param paths 路径
     * @throws IOException IO异常
     */
    default void deleteAll(String... paths) throws IOException {
        for (String path : paths) {
            this.delete(path);
        }
    }

    /**
     * 级联删除路径
     *
     * @param path 路径
     * @throws IOException IO异常
     */
    void delete(String path) throws IOException;

    /**
     * 获取字节输入流
     *
     * @param path 路径
     * @return 字节输入流
     * @throws IOException IO异常
     */
    InputStream read(String path) throws IOException;

    /**
     * 获取字符输入流
     *
     * @param path    路径
     * @param charset 字符集
     * @return 字符输入流
     * @throws IOException IO异常
     */
    default Reader readAsText(String path, Charset charset) throws IOException {
        return new BufferedReader(new InputStreamReader(this.read(path), charset));
    }

    default Reader readAsText(String path) throws IOException {
        return this.readAsText(path, StandardCharsets.UTF_8);
    }

    /**
     * 获取字节输出流
     *
     * @param path 路径
     * @return 字节输出流
     * @throws IOException IO异常
     */
    OutputStream write(String path) throws IOException;

    /**
     * 获取字符输出流
     *
     * @param path    路径
     * @param charset 字符输出流
     * @return 字符输出流
     * @throws IOException IO异常
     */
    default Writer writeAsText(String path, Charset charset) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(this.write(path), charset));
    }

    default Writer writeAsText(String path) throws IOException {
        return this.writeAsText(path, StandardCharsets.UTF_8);
    }

    /**
     * 获取追加字节输出流
     *
     * @param path 路径
     * @return 追加字节输出流
     * @throws IOException IO异常
     */
    OutputStream append(String path) throws IOException;

    /**
     * 获取追加字符输出流
     *
     * @param path    路径
     * @param charset 追加字符输出流
     * @return 追加字符输出流
     * @throws IOException IO异常
     */
    default Writer appendAsText(String path, Charset charset) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(this.append(path), charset));
    }

    default Writer appendAsText(String path) throws IOException {
        return this.appendAsText(path, StandardCharsets.UTF_8);
    }

    /**
     * 复制文件
     *
     * @param src  原始路径
     * @param dest 新路径
     * @throws IOException IO异常
     */
    default void copy(String src, String dest) throws IOException {
        InputStream is = this.read(src);
        OutputStream os = this.write(dest);
        IOUtils.copyBytes(is, os, 4096, true);
    }

    /**
     * 文件重命名/文件移动
     *
     * @param src  原路径
     * @param dest 新路径
     * @return 是否成功
     * @throws IOException IO异常
     */
    boolean rename(String src, String dest) throws IOException;

    default boolean remove(String src, String dest) throws IOException {
        return this.rename(src, dest);
    }

    /**
     * 压缩多文件/目录 (默认格式)
     *
     * @param inputs 多文件/目录路径
     * @param output 压缩文件完整输出路径 (小写扩展名)
     * @throws IOException IO异常
     */
    void compress(String[] inputs, String output) throws IOException;

    /**
     * 解压文件 (默认格式), 解压后的文件会在输出路径下以目录的形式呈现, 即会在输出路径下新建与原压缩文件名称一致的目录 (不包括扩展名)
     *
     * @param input     压缩文件完整路径
     * @param outputDir 解压文件输出路径
     * @throws IOException IO异常
     */
    void decompress(String input, String outputDir) throws IOException;

    /**
     * 获取 Home 目录
     *
     * @return Home 目录
     */
    String getHomeDirectory();

    /**
     * 获取文件/目录的修改时间字符串
     *
     * @param path 路径
     * @return 时间字符串
     * @throws IOException IO异常
     */
    Date modificationTime(String path) throws IOException;

    /**
     * 获取文件大小 (单位为字节)
     *
     * @param path 文件路径
     * @return 如果是目录为目录下所有文件大小之和
     * @throws IOException IO异常
     */
    long size(String path) throws IOException;

    /**
     * 从文件中按行读取文本
     *
     * @param path    文件路径
     * @param size    读取行数
     * @param charset 解码字符集
     * @return 文本列表
     * @throws IOException IO异常
     */
    default List<String> readToLines(String path, int size, Charset charset) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(this.readAsText(path, charset));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
            if (size != -1 && lines.size() >= size) break;
        }
        IOUtils.closeAll(reader);
        return lines;
    }

    default List<String> readToLines(String path, int size) throws IOException {
        return this.readToLines(path, size, StandardCharsets.UTF_8);
    }

    default List<String> readToLines(String path) throws IOException {
        return this.readToLines(path, -1, StandardCharsets.UTF_8);
    }

    /**
     * 获取文本行迭代器
     *
     * @param path    文本文件路径
     * @param charset 字符集
     * @return 文本行迭代器
     */
    LineIterator getLineIterator(String path, Charset charset) throws IOException;

    default LineIterator getLineIterator(String path) throws IOException {
        return getLineIterator(path, StandardCharsets.UTF_8);
    }

    /**
     * 从文件中读取文本
     *
     * @param path    文件路径
     * @param charset 解码字符集
     * @return 文本
     * @throws IOException IO异常
     */
    default String readToText(String path, Charset charset) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<String> lines = this.readToLines(path, -1, charset);
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        if (!lines.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    default String readToText(String path) throws IOException {
        return this.readToText(path, StandardCharsets.UTF_8);
    }

    /**
     * 将文本写入文件
     *
     * @param path    文件路径
     * @param content 内容
     * @param charset 编码字符集
     * @throws IOException IO异常
     */
    default void writeTextToFile(String path, Collection<String> content, Charset charset) throws IOException {
        BufferedWriter writer = new BufferedWriter(this.writeAsText(path, charset));
        for (String line : content) {
            writer.write(line + "\n");
        }
        IOUtils.closeAll(writer);
    }

    default void writeTextToFile(String path, Collection<String> content) throws IOException {
        this.writeTextToFile(path, content, StandardCharsets.UTF_8);
    }
}
