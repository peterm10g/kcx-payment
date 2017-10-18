package com.lsh.payment.core.util.ftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/11/15
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.util.ftp
 * desc:
 */
public class SFTPManager {

        Logger logger = LoggerFactory.getLogger(SFTPManager.class);

        ChannelSftp sftpClient;

        /**
         * 上传文件
         * @param url SFTP服务器hostname
         * @param port SFTP服务器端口
         * @param username SFTP登录账号
         * @param password SFTP登录密码
         * @param path SFTP服务器保存目录
         * @param localFile 要上传到SFTP服务器上的本地文件(路径及文件名)
         * @param sftpFile   上传到SFTP服务器上的文件名
         * @return 成功返回true，否则返回false
         * */
    public boolean uploadFileBySFTP(String url, int port, String username, String password, String path, String localFile, String sftpFile){
        // 初始表示上传失败
        boolean success = false;
        FileInputStream fis = null;
        try{
            // 创建服务器连接
            connectServerForSFTP(url, port, username, password, path);
            // 上传文件
            File file = new File(localFile);
            fis = new FileInputStream(file);
            sftpClient.put(fis, sftpFile);
            logger.debug("upload success!");

            success = true;
        }catch(Exception ex){
            logger.error("upload fail!", ex);
            return success;
        }finally{
            // 关闭连接
            closeConnectForSFTP();
            if(fis != null){
                try{
                    fis.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return success;
    }

    /**
     * 下载文件
     * @param url SFTP服务器hostname
     * @param port SFTP服务器端口
     * @param username SFTP登录账号
     * @param password SFTP登录密码
     * @param path SFTP服务器保存目录
     * @param sftpFile 要下载SFTP服务器上的文件
     * @param localFile   下载到本地的文件(路径及文件名)
     * @return 成功返回true，否则返回false
     * */
    public boolean downFileBySFTP(String url, int port, String username, String password, String path, String sftpFile, String localFile){
        // 初始表示上传失败
        boolean success = false;
        FileOutputStream fos = null;
        try{
            // 创建服务器连接
            connectServerForSFTP(url, port, username, password, path);
            // 下载文件
            File file = new File(localFile);
            fos = new FileOutputStream(file);
            sftpClient.get(sftpFile, fos);
            logger.debug("download success!");
            success = true;
        }catch(Exception ex){
            ex.printStackTrace();
            logger.error("download fail!", ex);
            return success;
        }finally{
            // 关闭连接
            closeConnectForSFTP();
            if(fos != null){
                try{
                    fos.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        return success;
    }

    /**
     * 创建连接
     * @param url SFTP服务器hostname
     * @param port SFTP服务器端口
     * @param username SFTP登录账号
     * @param password SFTP登录密码
     * @param path SFTP服务器保存目录
     * @throws Exception
     */
    private void connectServerForSFTP(String url, int port, String username, String password, String path) throws Exception{
        // 创建ChannelSftp对象
        sftpClient = new ChannelSftp();
        JSch jsch = new JSch();
        // 创建连接session
        Session session = jsch.getSession(username, url, port);
        session.setPassword(password);
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        session.setConfig(sshConfig);
        session.connect();
        // 创建通道
        Channel channel = session.openChannel("sftp");
        channel.connect();
        sftpClient = (ChannelSftp) channel;
        // 转到指定目录
//        sftpClient.cd(path);
    }

    /**
     * 关闭连接
     */
    private void closeConnectForSFTP(){
        try{
            sftpClient.disconnect();
            sftpClient.getSession().disconnect();
            logger.debug("disconnect success!");
        }catch(Exception ex){
            logger.error("disconnect fail!", ex);
        }
    }



}
