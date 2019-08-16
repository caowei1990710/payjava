package com.example.demo.Model;

import com.example.demo.Service.BankcardService;
import com.example.demo.Utils.Config;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by snsoft on 17/7/2017.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Wechat {
    @Id
    @GeneratedValue
    private Integer id;
    @NotEmpty(message = "微信账号为为空")
    @Column(name = "wechat_name", unique = true)
    private String wechatName;//卡号
    private String nickName;//昵称
    private String realName;//真实姓名
    private String wechatId;//
    private String ip;
    private String teleNumber;//绑定电话
    private String state;//状态
    private String type;//类型 0:银行卡1:支付宝2:虚拟卡3:云闪付4:微信
    private Double dayamount;//今日交易
    private Double daylimit;//交易限额
    private Double lowlimit;//最低交易限额
    private Double hightlimit;//最高交易限额
    private Double autowithrow;//提现限额
    private String url;//
    private String remark;
    private Double amount;
    private String qrurl;//图片url信息
    private String pid;//支付id
    private int makepic;//制作图片次数
    private int payType;//收款方式 1.动态 2.固码(循环)3.多图4.固码支付
    private int bankType;//0，内部卡1，卡商卡2,收款卡3.农信码支付宝4.农信码微信5.WD25专属卡6.云闪付7.W12专属卡,8,网银转账卡
    private String belongKsname;//所属卡商
    private int daysucces;//成功提案
    private int daynumber;//总提案号
    private int nosucces;//连续多少笔没成功
    private Double payfee;//收款手续费;
    private int payBanktype;//1.优质卡，2.中层卡3.外层卡

    public int getPayBanktype() {
        return payBanktype;
    }

    public void setPayBanktype(int payBanktype) {
        this.payBanktype = payBanktype;
    }

    public Double getPayfee() {
        return payfee;
    }

    public void setPayfee(Double payfee) {
        this.payfee = payfee;
    }

    public int getBankType() {
        return bankType;
    }

    public void setBankType(int bankType) {
        this.bankType = bankType;
    }

    public String getBelongKsname() {
        return belongKsname;
    }

    public void setBelongKsname(String belongKsname) {
        this.belongKsname = belongKsname;
    }

    public Double getLowlimit() {
        return lowlimit;
    }

    public void setLowlimit(Double lowlimit) {
        this.lowlimit = lowlimit;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getMakepic() {
        return makepic;
    }

    public void setMakepic(int makepic) {
        this.makepic = makepic;
    }

    public String getQrurl() {
        return qrurl;
    }

    public void setQrurl(String qrurl) {
        this.qrurl = qrurl;
    }

    public String getRealName() {
        return realName;
    }


    public void setRealName(String realName) {
        this.realName = realName;
    }

    //    @Value("now")
    private Date creatimte;
    private String creatUser;
    private Date lastUsetime;
    private String belongbankCard;
    private String plaftfrom;

    public Double getDayamount() {
        return dayamount;
    }

    public void setDayamount(Double dayamount) {
        this.dayamount = dayamount;
    }

    public Double getDaylimit() {
        return daylimit;
    }

    public void setDaylimit(Double daylimit) {
        this.daylimit = daylimit;
    }

    public Double getAutowithrow() {
        return autowithrow;
    }

    public void setAutowithrow(Double autowithrow) {
        this.autowithrow = autowithrow;
    }

    public String getPlaftfrom() {
        return plaftfrom;
    }

    public void setPlaftfrom(String plaftfrom) {
        this.plaftfrom = plaftfrom;
    }

    public String getBelongbankCard() {
        return belongbankCard;
    }

    public void setBelongbankCard(String belongbankCard) {
        this.belongbankCard = belongbankCard;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Date getLastUsetime() {
        return lastUsetime;
    }

    public void setLastUsetime(Date lastUsetime) {
        this.lastUsetime = lastUsetime;
    }

    public Double getHightlimit() {
        return hightlimit;
    }

    public void setHightlimit(Double hightlimit) {
        this.hightlimit = hightlimit;
    }

    public Wechat() {
        this.creatimte = new Date();
        this.creatUser = "root";
        this.type = Config.wehcat_coltype;
        this.amount = 0d;
        this.state = Config.Normal;
//        this.ip = "127.0.0.1";
        this.wechatId = "0";
        this.lastUsetime = new Date();
        this.dayamount = 0.0;
        this.daylimit = 50000.0;
        this.lowlimit = 0.0;
        this.hightlimit = 50000.0;
        this.autowithrow = 50000.0;
        this.makepic = 0;
        this.payType = 1;
        this.bankType = 0;
        this.payfee = 0.001;
        this.daysucces = 0;
        this.daynumber = 0;
        this.nosucces = 0;
        this.payBanktype = 3;
        //        this.type =
    }

    public int getDaysucces() {
        return daysucces;
    }

    public void setDaysucces(int daysucces) {
        System.out.println("daysucces:" + daysucces);
        this.daysucces = daysucces;
    }

    public int getDaynumber() {
        return daynumber;
    }

    public void setDaynumber(int daynumber) {
        System.out.println("daynumber:" + daynumber);
        this.daynumber = daynumber;
    }

    public int getNosucces() {
        return nosucces;
    }

    public void setNosucces(int nosucces) {
        System.out.println("nosucces:" + nosucces);
        this.nosucces = nosucces;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTeleNumber() {
        return teleNumber;
    }

    public void setTeleNumber(String teleNumber) {
        this.teleNumber = teleNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = BankcardService.formatDouble1(amount);
    }

    public Date getCreatimte() {
        return creatimte;
    }

    public void setCreatimte(Date creatimte) {
        this.creatimte = creatimte;
    }

    public String getCreatUser() {
        return creatUser;
    }

    public void setCreatUser(String creatUser) {
        this.creatUser = creatUser;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    @Override
    public String toString() {
        return "Wechat{" +
                "id=" + id +
                ", wechatName='" + wechatName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", wechatId='" + wechatId + '\'' +
                ", ip='" + ip + '\'' +
                ", teleNumber='" + teleNumber + '\'' +
                ", state='" + state + '\'' +
                ", type='" + type + '\'' +
                ", remark='" + remark + '\'' +
                ", amount=" + amount +
                ", creatimte=" + creatimte +
                ", creatUser='" + creatUser + '\'' +
                ", lastUsetime=" + lastUsetime +
                ", dayamount=" + dayamount +
                ", daylimit=" + daylimit +
                ", nosucces=" + nosucces +
                ", daynumber=" + daynumber +
                ", daysucces=" + daysucces +
                ", lowlimit=" + lowlimit +
                ", hightlimit=" + hightlimit +
                ", autowithrow=" + autowithrow +
                ", payType=" + payType +
                ", qrurl=" + qrurl +
                '}';
    }
}
