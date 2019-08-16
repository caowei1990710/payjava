package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by snsoft on 17/2/2018.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysoutItem {
    @Id
    @GeneratedValue
    private int sysId;
    private int id;
    private String uuid;
    private String platformUUID;
    private String platform;
    private String account;
    private String organization;
    private String beneficiaryAccount;
    private String beneficiaryOrganization;
    private String beneficiaryName;
    private String transferAmount;
    private String transferState;
    private String issueCount;
    private String balance;
    private String transferResult;
    private String transferFee;
    private String platformCallbackURL;
    private String creationTime;
    private Date creationTimes;
    private String issueTime;
    private Date issueTimes;
    private String modificationTime;
    private Date modificationTimes;

    public String getPlatformUUID() {
        return platformUUID;
    }

    public void setPlatformUUID(String platformUUID) {
        this.platformUUID = platformUUID;
    }


    @Override
    public String toString() {
        return "SysoutList{" +
                "sysId=" + sysId +
                ", id=" + id +
                ", uuid='" + uuid + '\'' +
                ", platform='" + platform + '\'' +
                ", account='" + account + '\'' +
                ", organization='" + organization + '\'' +
                ", beneficiaryAccount='" + beneficiaryAccount + '\'' +
                ", beneficiaryOrganization='" + beneficiaryOrganization + '\'' +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", transferAmount='" + transferAmount + '\'' +
                ", transferState='" + transferState + '\'' +
                ", issueCount='" + issueCount + '\'' +
                ", balance='" + balance + '\'' +
                ", transferResult='" + transferResult + '\'' +
                ", transferFee='" + transferFee + '\'' +
                ", platformCallbackURL='" + platformCallbackURL + '\'' +
                ", creationTime='" + creationTime + '\'' +
                ", creationTimes=" + creationTimes +
                ", issueTime='" + issueTime + '\'' +
                ", issueTimes=" + issueTimes +
                ", modificationTime='" + modificationTime + '\'' +
                ", modificationTimes=" + modificationTimes +
                ", platformUUID=" + platformUUID +
                '}';
    }

    public int getSysId() {
        return sysId;
    }

    public void setSysId(int sysId) {
        this.sysId = sysId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getBeneficiaryAccount() {
        return beneficiaryAccount;
    }

    public void setBeneficiaryAccount(String beneficiaryAccount) {
        this.beneficiaryAccount = beneficiaryAccount;
    }

    public String getBeneficiaryOrganization() {
        return beneficiaryOrganization;
    }

    public void setBeneficiaryOrganization(String beneficiaryOrganization) {
        this.beneficiaryOrganization = beneficiaryOrganization;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(String transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getTransferState() {
        return transferState;
    }

    public void setTransferState(String transferState) {
        this.transferState = transferState;
    }

    public String getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(String issueCount) {
        this.issueCount = issueCount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTransferResult() {
        return transferResult;
    }

    public void setTransferResult(String transferResult) {
        this.transferResult = transferResult;
    }

    public String getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(String transferFee) {
        this.transferFee = transferFee;
    }

    public String getPlatformCallbackURL() {
        return platformCallbackURL;
    }

    public void setPlatformCallbackURL(String platformCallbackURL) {
        this.platformCallbackURL = platformCallbackURL;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public Date getCreationTimes() {
        return creationTimes;
    }

    public void setCreationTimes(Date creationTimes) {
        this.creationTimes = creationTimes;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public Date getIssueTimes() {
        return issueTimes;
    }

    public void setIssueTimes(Date issueTimes) {
        this.issueTimes = issueTimes;
    }

    public String getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(String modificationTime) {
        this.modificationTime = modificationTime;
    }

    public Date getModificationTimes() {
        return modificationTimes;
    }

    public void setModificationTimes(Date modificationTimes) {
        this.modificationTimes = modificationTimes;
    }
}
