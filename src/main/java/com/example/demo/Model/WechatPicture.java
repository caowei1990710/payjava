package com.example.demo.Model;

import com.example.demo.Utils.WechatEnume;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by snsoft on 15/9/2017.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class WechatPicture {
    @Id
    @GeneratedValue
    private Integer id;
    @NotEmpty(message = "微信账号为为空")
    @Column(name = "wechat_name", unique = true)
    private String wechatName;
    private Date creatTime;
    private Date updateTime;
    private String state;//"未生成"，"已生成"，"已确定"
    private String opeater;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWechatName() {
        return wechatName;
    }

    public void setWechatName(String wechatName) {
        this.wechatName = wechatName;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOpeater() {
        return opeater;
    }

    public void setOpeater(String opeater) {
        this.opeater = opeater;
    }

    public WechatPicture() {
        this.creatTime = new Date();
        this.updateTime = new Date();
        this.state = WechatEnume.wechat_uncrete;
        this.opeater = "root";
    }

    @Override
    public String toString() {
        return "WechatPicture{" +
                "id=" + id +
                ", wechatName='" + wechatName + '\'' +
                ", creatTime=" + creatTime +
                ", updateTime=" + updateTime +
                ", state='" + state + '\'' +
                ", opeater='" + opeater + '\'' +
                '}';
    }
}
