package com.example.demo.Controller;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class FTPUtils {

    private  FTPClient ftp;
    private final static String ENCODE = "GBK";
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, ENCODE);
            } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    public FTPUtils(String addr, int port, String username, String password) throws IOException {
        ftp = new FTPClient();
        ftp.connect(addr,port);
        ftp.login(username,password);
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        int reply;
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
        }
    }
    /**
     *
     * @param path 上传到ftp服务器哪个路径下     
     * @return
     * @throws Exception
     */
    public void connect(String path) throws Exception {
        ftp.changeWorkingDirectory(path);
    }

    public boolean makeDirectory(String path) throws IOException {
        return ftp.makeDirectory(path);
    }



    /**
     *
     * @param file 上传的文件或文件夹  
     * @throws Exception
     */
    public void upload(File file,String name,String picture) throws Exception{
        if(file.isDirectory()){
            ftp.makeDirectory(file.getName());
            ftp.changeWorkingDirectory(file.getName());
            String[] files = file.list();
            for (int i = 0; i < files.length; i++) {
                File file1 = new File(file.getPath()+"\\"+files[i] );
                if(file1.isDirectory()){
                    upload(file1,name,picture);
                    ftp.changeToParentDirectory();
                }else{
                    File file2 = new File(file.getPath()+"\\"+files[i]);
                    FileInputStream input = new FileInputStream(file2);
                    ftp.changeWorkingDirectory(name);
                    ftp.storeFile(picture, input);
                    input.close();
                }
            }
        }else{
            File file2 = new File(file.getPath());
            FileInputStream input = new FileInputStream(file2);
            ftp.changeWorkingDirectory(name);
            boolean result = ftp.storeFile(picture, input);
            System.out.println(result);
            input.close();
        }
    }


    public void destroy() throws IOException {
        if(ftp != null){
            ftp.disconnect();
            ftp = null;
        }
    }


    public static void main(String[] args) throws Exception{
        FTPUtils t = new FTPUtils("测试IP", 21, "账户名", "密码");
        boolean isDirectory = t.makeDirectory("upload/123");
        System.out.println("创建目录是否成功： ======================" + isDirectory);
        t.connect("upload/123");
        File file = new File("F:\\test.txt");
        t.upload(file,"text","");
    }
}    