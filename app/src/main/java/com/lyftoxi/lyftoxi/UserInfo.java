package com.lyftoxi.lyftoxi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UserInfo {

    private String UID;

    private String name;
    private Date dob;
    private String password;
    private Boolean activeStatus;
    private Date registrationDate;
    private String email;
    private String phNo;
    private String	activationCd;
    private String sex;
    private List<UserAddressInfo> addresses= new ArrayList<UserAddressInfo>();

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhNo() {
        return phNo;
    }

    public void setPhNo(String phNo) {
        this.phNo = phNo;
    }

    public String getActivationCd() {
        return activationCd;
    }

    public void setActivationCd(String activationCd) {
        this.activationCd = activationCd;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<UserAddressInfo> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<UserAddressInfo> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "UID='" + UID + '\'' +
                ", name='" + name + '\'' +
                ", dob=" + dob +
                ", password='" + password + '\'' +
                ", activeStatus=" + activeStatus +
                ", registrationDate=" + registrationDate +
                ", email='" + email + '\'' +
                ", phNo='" + phNo + '\'' +
                ", activationCd='" + activationCd + '\'' +
                ", sex='" + sex + '\'' +
                ", addresses=" + addresses +
                '}';
    }
}
