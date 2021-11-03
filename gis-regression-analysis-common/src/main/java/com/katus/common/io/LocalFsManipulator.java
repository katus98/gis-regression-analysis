package com.katus.common.io;

import com.katus.common.util.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author SUN Katus
 * @version 1.0, 2021-05-25
 */
public class LocalFsManipulator implements FsManipulator {
    private static LocalFsManipulator INSTANCE;

    private LocalFsManipulator() {}

    static LocalFsManipulator getInstance() {
        if (INSTANCE == null) {
            synchronized (LocalFsManipulator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalFsManipulator();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public boolean exists(String path) {
        return new File(path).exists();
    }

    @Override
    public boolean isFile(String path) {
        return new File(path).isFile();
    }

    @Override
    public String[] list(String path) {
        File[] files = new File(path).listFiles();
        if (files == null) {
            files = new File[0];
        }
        String[] list = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            list[i] = files[i].getAbsolutePath();
        }
        return list;
    }

    @Override
    public boolean makeDirectory(String path) {
        return new File(path).mkdir();
    }

    @Override
    public boolean makeDirectories(String path) {
        return new File(path).mkdirs();
    }

    @Override
    public boolean createFile(String path) throws IOException {
        return new File(path).createNewFile();
    }

    @Override
    public void delete(String path) {
        new File(path).delete();
    }

    @Override
    public InputStream read(String path) throws IOException {
        return new FileInputStream(path);
    }

    @Override
    public OutputStream write(String path) throws IOException {
        return new FileOutputStream(path);
    }

    @Override
    public OutputStream append(String path) throws IOException {
        return new FileOutputStream(path, true);
    }

    @Override
    public boolean rename(String src, String dest) {
        return new File(src).renameTo(new File(dest));
    }

    @Override
    public void compress(String[] inputs, String output) throws IOException {
        if (inputs.length <= 0)
            return;
        if (!output.endsWith(".zip")) {
            output += ".zip";
        }
        ZipOutputStream zos = new ZipOutputStream(this.write(output));
        for (String input : inputs) {
            this.compressToZip(new File(input), zos, "");
        }
        zos.finish();
        IOUtils.closeAll(zos);
    }

    @Override
    public void decompress(String input, String outputDir) throws IOException {
        this.decompressFromZip(new ZipFile(input), outputDir);
    }

    @Override
    public String getHomeDirectory() {
        return System.getProperty("user.home");
    }

    @Override
    public Date modificationTime(String path) {
        return new Date(new File(path).lastModified());
    }

    @Override
    public long size(String path) {
        if (isFile(path)) {
            return new File(path).length();
        } else {
            String[] files = list(path);
            long totalSize = 0;
            for (String file: files) {
                totalSize += size(file);
            }
            return totalSize;
        }
    }

    @Override
    public LineIterator getLineIterator(String path, Charset charset) throws IOException {
        return new LocalLineIterator(this.readAsText(path, charset));
    }

    private void compressToZip(File file, ZipOutputStream zos, String prefix) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                files = new File[0];
            }
            if (files.length == 0) {
                // Java的zip实现中存在缺陷, 目录判断只根据结尾是否为斜杠, 反斜杠无法识别, 故此处不使用File.separator自动匹配
                zos.putNextEntry(new ZipEntry(prefix + file.getName() + "/"));
                zos.closeEntry();
            } else {
                for (File childFile : files) {
                    this.compressToZip(childFile, zos, prefix + file.getName() + File.separator);
                }
            }
        } else {
            InputStream is = new FileInputStream(file);
            zos.putNextEntry(new ZipEntry(prefix + file.getName()));
            IOUtils.copyBytes(is, zos);
            zos.closeEntry();
            IOUtils.closeAll(is);
        }
    }

    private void decompressFromZip(ZipFile zipFile, String targetDir) throws IOException {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                String dirPath = targetDir + File.separator + entry.getName();
                if (!this.exists(dirPath)) {
                    this.makeDirectories(dirPath);
                }
            } else {
                File targetFile = new File(targetDir + File.separator + entry.getName());
                String parentPath = targetFile.getParent();
                if (!this.exists(parentPath)) {
                    this.makeDirectories(parentPath);
                }
                InputStream is = zipFile.getInputStream(entry);
                OutputStream os = new FileOutputStream(targetFile);
                IOUtils.copyBytes(is, os, 4096, true);
            }
        }
    }
}
