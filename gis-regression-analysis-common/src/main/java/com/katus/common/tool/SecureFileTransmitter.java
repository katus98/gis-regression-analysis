package com.katus.common.tool;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.util.Strings;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Vector;

/**
 * @author SUN Katus, jiarui
 * @version 2.0, 2021-06-24
 */
@Slf4j
public class SecureFileTransmitter implements Closeable {
    /**
     * sftp通道
     */
    private ChannelSftp channelSftp;
    /**
     * 连接会话
     */
    private Session session;
    /**
     * SFTP 登录用户名
     */
    private final String username;
    /**
     * SFTP 登录密码
     */
    private final String password;
    /**
     * 私有密钥
     */
    private final String privateKey;
    /**
     * SFTP 服务器地址IP地址
     */
    private final String host;
    /**
     * SFTP 端口
     */
    private final int port;

    private SecureFileTransmitter(String username, String password, String privateKey, String host, Integer port) throws JSchException {
        this.username = username;
        this.password = password;
        this.privateKey = privateKey;
        this.host = host;
        this.port = port;
        this.login();
    }

    public static SecureFileTransmitterBuilder builder() {
        return new SecureFileTransmitterBuilder();
    }

    /**
     * 将文件流传输到远程位置 (覆盖)
     *
     * @param is        文件输入流
     * @param directory 远程目录
     * @param filename  远程保存的文件名
     * @throws SftpException sftp异常
     */
    public void upload(InputStream is, String directory, String filename) throws SftpException {
        if (Strings.hasLength(directory)) {
            channelSftp.cd(directory);
        }
        // 上传文件
        channelSftp.put(is, filename);
    }

    /**
     * 获取远程文件输入流
     *
     * @param directory 远程目录
     * @param filename  远程文件名
     * @return 输入流
     * @throws SftpException sftp异常
     */
    public InputStream getDownloadStream(String directory, String filename) throws SftpException {
        if (Strings.hasLength(directory)) {
            channelSftp.cd(directory);
        }
        return channelSftp.get(filename);
    }

    /**
     * 将远程文件传输至输出流
     *
     * @param directory 远程目录
     * @param filename  远程文件名
     * @param os        承接输出流
     * @throws SftpException sftp异常
     */
    public void download(String directory, String filename, OutputStream os) throws SftpException {
        if (Strings.hasLength(directory)) {
            channelSftp.cd(directory);
        }
        // 下载文件
        channelSftp.get(filename, os);
    }

    /**
     * 将远程文件传输至本地文件
     *
     * @param directory    远程目录
     * @param filename     远程文件名
     * @param saveFilePath 本地文件存储路径
     * @throws IOException   IO异常
     * @throws SftpException sftp异常
     */
    public void download(String directory, String filename, String saveFilePath) throws IOException, SftpException {
        FsManipulator fsManipulator = FsManipulatorFactory.create();
        this.download(directory, filename, fsManipulator.write(saveFilePath));
    }

    /**
     * 删除远程文件
     *
     * @param directory  远程文件所在目录
     * @param deleteFile 远程文件名
     * @throws SftpException sftp异常
     */
    public void delete(String directory, String deleteFile) throws SftpException {
        if (Strings.hasLength(directory)) {
            channelSftp.cd(directory);
        }
        channelSftp.rm(deleteFile);
    }

    /**
     * 列出远程目录下的文件
     *
     * @param directory 远程目录
     * @return 文件向量
     * @throws SftpException sftp异常
     */
    public Vector<?> ls(String directory) throws SftpException {
        return channelSftp.ls(directory);
    }

    /**
     * 重命名方法
     *
     * @param oldPath 旧的文件路径
     * @param newPath 新的文件路径
     */
    public void rename(String oldPath, String newPath) throws SftpException {
        channelSftp.rename(oldPath, newPath);
    }

    /**
     * 判断文件是否存在
     */
    public boolean isExist(String filepath) throws SftpException {
        SftpATTRS attrs = null;
        try {
            attrs = channelSftp.stat(filepath);
        } catch (Exception ignored) {
        }
        return attrs != null;
    }

    /**
     * @param directory  远程目录
     * @param directory  远程目录
     * @param folderName 新建文件夹的名称
     * @throws SftpException sftp异常
     */
    public void mkdir(String directory, String folderName) throws SftpException {
        if (Strings.hasLength(directory)) {
            channelSftp.cd(directory);
        }
        channelSftp.mkdir(folderName);
    }


    /**
     * 新建文件夹,如果文件夹不存在，则创建
     *
     * @param directory 远程目录
     * @throws SftpException sftp异常
     */
    public void mkdirs(String directory) throws SftpException {
        directory = directory.substring(1);
        String[] splits = directory.split("/");
        if (splits.length == 0) return;
        Vector<?> ls = channelSftp.ls("/");
        boolean flag = true;
        for (Object l : ls) {
            String filename = ((ChannelSftp.LsEntry) l).getFilename();
            if (filename.equals(splits[0])) {
                flag = false;
            }
        }
        if (flag) {
            channelSftp.mkdir(splits[0]);
        }
        StringBuilder currentPath = new StringBuilder();
        for (int i = 0; i < splits.length - 1; i++) {
            currentPath.append("/").append(splits[i]);
            ls = channelSftp.ls(currentPath.toString());
            channelSftp.cd(currentPath.toString());
            flag = true;
            for (Object l : ls) {
                String filename = ((ChannelSftp.LsEntry) l).getFilename();
                if (filename.equals(splits[i + 1])) {
                    flag = false;
                }
            }
            if (flag) {
                channelSftp.mkdir(splits[i + 1]);
            }
        }
    }

    public static class SecureFileTransmitterBuilder {
        private String username;
        private String password;
        private String privateKey;
        private String host;
        private int port = 22;

        public SecureFileTransmitter build() throws JSchException {
            return new SecureFileTransmitter(username, password, privateKey, host, port);
        }

        public SecureFileTransmitterBuilder username(String username) {
            this.username = username;
            return this;
        }

        public SecureFileTransmitterBuilder password(String password) {
            this.password = password;
            return this;
        }

        public SecureFileTransmitterBuilder privateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public SecureFileTransmitterBuilder host(String host) {
            this.host = host;
            return this;
        }

        public SecureFileTransmitterBuilder port(int port) {
            this.port = port;
            return this;
        }
    }

    @Override
    public void close() {
        this.logout();
    }

    /**
     * 登录并建立sftp连接
     */
    private void login() throws JSchException {
        JSch jsch = new JSch();
        if (privateKey != null) {
            // 设置私钥
            jsch.addIdentity(privateKey);
        }
        this.session = jsch.getSession(username, host, port);
        if (password != null) {
            // 设置密码
            session.setPassword(password);
        }
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        // 建立sftp连接
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        this.channelSftp = (ChannelSftp) channel;
        log.info("Login sftp server successfully!");
    }

    /**
     * 登出并关闭sftp连接
     */
    private void logout() {
        if (channelSftp != null) {
            if (channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }
}
