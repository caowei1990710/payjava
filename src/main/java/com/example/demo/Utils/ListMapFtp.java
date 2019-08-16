package com.example.demo.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("listMapFtp")
@Transactional
public class ListMapFtp {

    private Logger logger = LogManager.getLogger(ListMapFtp.class);

    public List<List<DtsFtpFile>> showList(String hostname, int port,
                                           String username, String password, String pathname)
            throws IOException {
        FTPClient ftpClient = new FTPClient();
        List<DtsFtpFile> listFile = new ArrayList<DtsFtpFile>();
        List<DtsFtpFile> listDirectory = new ArrayList<DtsFtpFile>();
        List<List<DtsFtpFile>> listMap = new ArrayList<List<DtsFtpFile>>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            int reply;
// 创建ftp连接
            ftpClient.connect(hostname, port);
// 登陆ftp
            ftpClient.login(username, password);
// 获得ftp反馈，判断连接状态
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
            }
            ftpClient.setControlEncoding("UTF-8");
            FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
            conf.setServerLanguageCode("zh");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.makeDirectory("123456");
// 切换到指定的目录
//            ftpClient.changeWorkingDirectory(pathname + "/");
            boolean result = ftpClient.changeWorkingDirectory(pathname);
            logger.info("切换目录:" + result);
// 获得指定目录下的文件夹和文件信息
            FTPFile[] ftpFiles = ftpClient.listFiles();

            for (FTPFile ftpFile : ftpFiles) {
                DtsFtpFile dtsFtpFile = new DtsFtpFile();
// 获取ftp文件的名称
                dtsFtpFile.setName(ftpFile.getName());
// 获取ftp文件的大小
                dtsFtpFile.setSize(ftpFile.getSize());
// 获取ftp文件的最后修改时间
                dtsFtpFile.setLastedUpdateTime(formatter.format(ftpFile
                        .getTimestamp().getTime()));
                if (ftpFile.getType() == 1 && !ftpFile.getName().equals(".")
                        && !ftpFile.getName().equals("..")) {
// mapDirectory.put(ftpFile.getName(),
// pathname+"/"+ftpFile.getName());
// 获取ftp文件的当前路劲
//                     dtsFtpFile.setLocalPath(pathname + "/" + ftpFile.getName());
                    dtsFtpFile.setLocalPath(pathname + "/" + ftpFile.getName());
                    listDirectory.add(dtsFtpFile);
                } else if (ftpFile.getType() == 0) {
// mapFile.put(ftpFile.getName(),ftpFile.getName());
//                    dtsFtpFile.setLocalPath(pathname + "/");
                    dtsFtpFile.setLocalPath(pathname + "/");
                    listFile.add(dtsFtpFile);
                }
// System.out.println("Name--->"+ftpFile.getName()+"\tTimestamp--->"+ftpFile.getTimestamp().getTime()+"\tsize--->"+ftpFile.getSize());
// double fileSize = (double) ftpFile.getSize();
// if(fileSize/(1024*1024*1024)>1){
// System.out.println(ftpFile.getName()+" size is "+df.format(fileSize/(1024*1024*1024))+"GB");
// }else if(fileSize/(1024*1024)>1){
// System.out.println(ftpFile.getName()+" size is "+df.format(fileSize/(1024*1024))+"MB");
// }else if(fileSize/1024>1){
// System.out.println(ftpFile.getName()+" size is "+df.format(fileSize/1024)+"KB");
// }else if(fileSize/1024<1){
// System.out.println(ftpFile.getName()+" size is "+ftpFile.getSize()+"B");
// }
            }
            listMap.add(listDirectory);
            listMap.add(listFile);
            ftpClient.logout();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return listMap;
    }
}
