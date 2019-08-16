package com.example.demo.Model;

/**
 * Created by snsoft on 22/5/2018.
 */
public class MoneyUpdate {
    private String name;
    private Double changeamount;
    private String type;
    private String remark;
    private String platfrom;
    private String createUser;

    public String getName() {
        return name;
    }

    public String getPlatfrom() {
        return platfrom;
    }

    public void setPlatfrom(String platfrom) {
        this.platfrom = platfrom;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Double getChangeamount() {
        return changeamount;
    }

    public void setChangeamount(Double changeamount) {
        this.changeamount = changeamount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "MoneyUpdate{" +
                "name='" + name + '\'' +
                ", changeamount=" + changeamount +
                ", type='" + type + '\'' +
                ", remark='" + remark + '\'' +
                ", platfrom='" + platfrom + '\'' +
                ", createUser='" + createUser + '\'' +
                '}';
    }

    public MoneyUpdate() {
    }
}
