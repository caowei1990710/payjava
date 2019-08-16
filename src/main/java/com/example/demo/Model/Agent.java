package com.example.demo.Model;

import com.example.demo.Controller.WechatController;
import com.example.demo.Service.BankcardService;
import com.example.demo.Utils.Config;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by snsoft on 16/5/2018.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Agent {
    private static final Logger logger = LoggerFactory.getLogger(WechatController.class);
    @Id
    @GeneratedValue
    private int id;
    @NotEmpty(message = "账号为空")
    @Column(name = "name", unique = true)
    private String name;
    private String password;
    private Date crateTime;
    private Date lastLoginTime;
    private String lastLoginIp;
    private String createrUser;
    @Column(name = "payfee", nullable = false)
    private Double itemPayfee;
    @Column(name = "wechatpayfee", nullable = false)
    private Double wechatpayfee;//微信返点
    @Column(name = "yunshanpayfee", nullable = false)
    private Double yunshanpayfee;//云闪付返点
    @Column(name = "banktranfee", nullable = false)
    private Double banktranfee;//网银转账
    private String state;
    private String remark;
    private String callbackurl;
    private String bankCard;
    private String bankCardName;
    private Double amount;
    private Double lockMoney;
    private String bankCardType;
    private String sign;
    private String agentName;//所属代理
    private String paySafe;//谷歌验证
    private String paySecret;//谷歌验证密码
    private String payqr;//条形码
    private String payword;//取款密码
    private String payType;//付款类型0：红包；1：万用码；2：宝转卡
    private int proposalLockTime;//提案锁定时间
    private int withrowAmountlow;//提现金额
    private int withrowAmounthigh;//提现金额
    private String canAlipay;//能收支付宝
    private String canWechat;//能收微信
    private String canYunshan; //能收云闪付
    private String canBanktran;//能网银转账
    private Double payless;//收款最低限额
    private String ip;//来源ip
    private String ipContent;//来源地址
    private String code;//谷歌验证码
    private int payBanktype;//1.优质卡，2.中层卡，3.外层卡
    public String getCode() {
        return code;
    }

    public int getPayBanktype() {
        return payBanktype;
    }

    public void setPayBanktype(int payBanktype) {
        this.payBanktype = payBanktype;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getIpContent() {
        return ipContent;
    }

    public void setIpContent(String ipContent) {
        this.ipContent = ipContent;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Double getYunshanpayfee() {
        return yunshanpayfee;
    }

    public void setYunshanpayfee(Double yunshanpayfee) {
        this.yunshanpayfee = yunshanpayfee;
    }

    public String getCanYunshan() {
        return canYunshan;
    }

    public void setCanYunshan(String canYunshan) {
        this.canYunshan = canYunshan;
    }

    public Double getPayless() {
        return payless;
    }

    public void setPayless(Double payless) {
        this.payless = payless;
    }

    public String getCanAlipay() {
        return canAlipay;
    }

    public void setCanAlipay(String canAlipay) {
        this.canAlipay = canAlipay;
    }

    public String getCanWechat() {
        return canWechat;
    }

    public void setCanWechat(String canWechat) {
        this.canWechat = canWechat;
    }

    public int getWithrowAmountlow() {
        return withrowAmountlow;
    }

    public void setWithrowAmountlow(int withrowAmountlow) {
        this.withrowAmountlow = withrowAmountlow;
    }

    public int getWithrowAmounthigh() {
        return withrowAmounthigh;
    }

    public void setWithrowAmounthigh(int withrowAmounthigh) {
        this.withrowAmounthigh = withrowAmounthigh;
    }

    public int getProposalLockTime() {
        return proposalLockTime;
    }

    public void setProposalLockTime(int proposalLockTime) {
        this.proposalLockTime = proposalLockTime;
    }

    public Double getWechatpayfee() {
        return wechatpayfee;
    }

    public void setWechatpayfee(Double wechatpayfee) {
        this.wechatpayfee = wechatpayfee;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayword() {
        return payword;
    }

    public void setPayword(String payword) {
        this.payword = payword;
    }

    public String getPayqr() {
        return payqr;
    }

    public void setPayqr(String payqr) {
        this.payqr = payqr;
    }

    public String getPaySecret() {
        return paySecret;
    }

    public void setPaySecret(String paySecret) {
        this.paySecret = paySecret;
    }

    public String getPaySafe() {
        return paySafe;
    }

    public void setPaySafe(String paySafe) {
        this.paySafe = paySafe;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getSign() {
        return sign;
    }

    public Double getBanktranfee() {
        return banktranfee;
    }

    public void setBanktranfee(Double banktranfee) {
        this.banktranfee = banktranfee;
    }

    public String getCanBanktran() {
        return canBanktran;
    }

    public void setCanBanktran(String canBanktran) {
        this.canBanktran = canBanktran;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", crateTime=" + crateTime +
                ", lastLoginTime=" + lastLoginTime +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                ", createrUser='" + createrUser + '\'' +
                ", itemPayfee=" + itemPayfee +
                ", state='" + state + '\'' +
                ", remark='" + remark + '\'' +
                ", callbackurl='" + callbackurl + '\'' +
                ", bankCard='" + bankCard + '\'' +
                ", bankCardName='" + bankCardName + '\'' +
                ", amount=" + amount +
                ", lockMoney=" + lockMoney +
                ", bankCardType='" + bankCardType + '\'' +
                ", sign='" + sign + '\'' +
                ", agentName='" + agentName + '\'' +
                ", wechatpayfee='" + wechatpayfee + '\'' +
                ", payword='" + payType + '\'' +
                ", yunshanpayfee='" + yunshanpayfee + '\'' +
                '}';
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Double getAmount() {

        return amount;
    }

    public void setAmount(Double amount) {
        logger.error(this.name + "读取之前金额:" + this.amount);
        logger.error(this.name + "设置了代理金额:" + amount);
        this.amount = BankcardService.formatDouble1(amount);
    }

    public Double getLockMoney() {
        return lockMoney;
    }

    public void setLockMoney(Double lockMoney) {
        this.lockMoney = lockMoney;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankCardName() {
        return bankCardName;
    }

    public void setBankCardName(String bankCardName) {
        this.bankCardName = bankCardName;
    }

    public String getBankCardType() {
        return bankCardType;
    }

    public void setBankCardType(String bankCardType) {
        this.bankCardType = bankCardType;
    }

    public String getCallbackurl() {
        return callbackurl;
    }

    public void setCallbackurl(String callbackurl) {
        this.callbackurl = callbackurl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCrateTime() {
        return crateTime;
    }

    public void setCrateTime(Date crateTime) {
        this.crateTime = crateTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getCreaterUser() {
        return createrUser;
    }

    public void setCreaterUser(String createrUser) {
        this.createrUser = createrUser;
    }

//    public Double getitemPayfee() {
//        return itemPayfee;
//    }

//    public void setPayfee(Double payfee) {
//        this.itemPayfee = payfee;
//    }

    public Double getItemPayfee() {
        return itemPayfee;
    }

    public void setItemPayfee(Double itemPayfee) {
        this.itemPayfee = itemPayfee;
    }

    public Agent() {
        this.crateTime = new Date();
//        this.payfee = 0.02;
//        this.state = Config.Normal;
        this.amount = 0.0;
        this.lockMoney = 0.0;
//        this.callbackurl = "http://www.autopay8.me/api/addWechatDepositRecord";
        this.paySafe = "off";
        this.withrowAmountlow = 1000;
        this.withrowAmounthigh = 50000;
        this.canAlipay = "1";
        this.canWechat = "1";
        this.canBanktran = "1";
        this.banktranfee = 0.008;
        this.payless = 300.0d;
        this.payBanktype = 3;
    }
}
