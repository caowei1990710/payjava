package com.example.demo.Utils;

/**
 * Created by snsoft on 19/9/2017.
 */
public class DtsFtpFile {
    private String name;
    private long size;
    private String lastedUpdateTime;
    private String localPath;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public String getLastedUpdateTime() {
        return lastedUpdateTime;
    }
    public void setLastedUpdateTime(String lastedUpdateTime) {
        this.lastedUpdateTime = lastedUpdateTime;
    }
    public String getLocalPath() {
        return localPath;
    }
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Override
    public String toString() {
        return "DtsFtpFile{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", lastedUpdateTime='" + lastedUpdateTime + '\'' +
                ", localPath='" + localPath + '\'' +
                '}';
    }
}