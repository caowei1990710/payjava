package com.example.demo.Controller;

/**
 * Created by snsoft on 18/7/2017.
 */
public class KmConfig {
    //主机ip
    private String FtpHost = "";
    //端口号
    private int FtpPort;
    //ftp用户名
    private String FtpUser = "";
    //ftp密码
    private String FtpPassword = "";
    //ftp中的目录
    private String FtpPath = "";
    public String getFtpHost() {
        return FtpHost;
    }
    public void setFtpHost(String ftpHost) {
        FtpHost = ftpHost;
    }
    public int getFtpPort() {
        return FtpPort;
    }
    public void setFtpPort(int ftpPort) {
        FtpPort = ftpPort;
    }
    public String getFtpUser() {
        return FtpUser;
    }
    public void setFtpUser(String ftpUser) {
        FtpUser = ftpUser;
    }
    public String getFtpPassword() {
        return FtpPassword;
    }
    public void setFtpPassword(String ftpPassword) {
        FtpPassword = ftpPassword;
    }
    public String getFtpPath() {
        return FtpPath;
    }
    public void setFtpPath(String ftpPath) {
        FtpPath = ftpPath;
    }



}




