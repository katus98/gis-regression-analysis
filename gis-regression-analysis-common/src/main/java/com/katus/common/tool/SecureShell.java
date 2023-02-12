package com.katus.common.tool;

import com.katus.common.util.Strings;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;
import com.trilead.ssh2.StreamGobbler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

/**
 * @author HU Linshu, SUN Katus
 * @version 1.0, 2021-05-18
 */
public class SecureShell implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(SecureShell.class);
    private static final String ENCODE_SET = "export LC_CTYPE=zh_CN.UTF-8;";
    private static final int CMD_LENGTH_LIMIT = 10000;
    private final Connection connection;

    public SecureShell(String host, String username, String password) throws IOException {
        this(host, username, password, 22);
    }

    public SecureShell(String host, String username, String password, int port) throws IOException {
        this.connection = new Connection(host, port);
        // Make sure the connection is opened.
        connection.connect();
        boolean isAuthenticated = connection.authenticateWithPassword(username, password);
        if (!isAuthenticated) {
            throw new IOException("Authentication failed.");
        }
    }

    /**
     * SSH 运行命令
     *
     * @param cmd        命令字符串
     * @param tempShFile 临时脚本文件存放位置(远程位置)
     * @return 是否执行成功
     * @throws IOException IO异常
     */
    public Boolean run(String cmd, String tempShFile) throws IOException {
        Session session = connection.openSession();
        long startTime = System.currentTimeMillis();
        logger.info("Waiting for cmd (length: {}): [{}]", cmd.length(), cmd);
        if (cmd.length() > CMD_LENGTH_LIMIT) {
            String tempSh = Paths.get(tempShFile, Strings.generateUuid() + ".sh").toString();
            session.execCommand("rm " + tempSh + ";echo \"" + cmd + ";echo \\$?\"" + " > " + tempSh +
                    "&&chmod 777 " + tempSh +
                    "&&" + ENCODE_SET + tempSh +
                    ";rm " + tempSh);
        } else {
            session.execCommand(ENCODE_SET + cmd + ";echo $?");
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStdout())));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        Boolean result = sb.toString().endsWith("0\n");
        if (result) {
            logger.info("Cmd[{}] finished, and spends {}ms.", cmd, System.currentTimeMillis() - startTime);
        } else {
            logger.info("Cmd[{}] failed, and spends {}ms.", cmd, System.currentTimeMillis() - startTime);
        }
        session.close();
        return result;
    }

    /**
     * SSH 运行命令并获取输出内容
     *
     * @param cmd        命令字符串
     * @param tempShFile 临时脚本文件存放位置(本机)
     * @return 命令返回内容
     * @throws IOException IO异常
     */
    public String runWithOutput(String cmd, String tempShFile) throws IOException {
        Session session = connection.openSession();
        long startTime = System.currentTimeMillis();
        logger.info("Waiting for cmd (length: {}): [{}]", cmd.length(), cmd);
        if (cmd.length() > CMD_LENGTH_LIMIT) {
            String tempSh = Paths.get(tempShFile, Strings.generateUuid() + ".sh").toString();
            session.execCommand("rm " + tempSh + ";echo " + cmd + " > " + tempSh + "&&chmod 777 " + tempSh + "&&" + ENCODE_SET + tempSh);
        } else {
            session.execCommand(ENCODE_SET + cmd);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStdout())))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            logger.error("Failed to load ssh stdout.", e);
        }
        logger.info("Cmd[{}] finished, and spends {}ms.", cmd, System.currentTimeMillis() - startTime);
        session.close();
        return sb.toString();
    }

    @Override
    public void close() {
        this.connection.close();
    }
}
