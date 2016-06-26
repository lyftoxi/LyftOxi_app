package com.lyftoxi.lyftoxi.singletons;

import android.graphics.Bitmap;

import com.lyftoxi.lyftoxi.CarInfo;
import com.lyftoxi.lyftoxi.SessionManager;
import com.lyftoxi.lyftoxi.dao.Car;
import com.lyftoxi.lyftoxi.dao.User;
import com.lyftoxi.lyftoxi.dao.UserAddress;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CurrentUserInfo {
    private static CurrentUserInfo ourInstance = new CurrentUserInfo();

    public static CurrentUserInfo getInstance() {
        return ourInstance;
    }

    private CurrentUserInfo() {
    }
    private String id;
    private String name;;
    private Date dob;
    private String password;
    private Boolean activeStatus;
    private Date registrationDate;
    private String email;
    private String phNo;
    private String	activationCd;
    private String gender;
    private String profilePicPath;
    private List<UserAddress> addresses=new ArrayList<UserAddress>();
    private List<Car> carDetails=new ArrayList<Car>();
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public Boolean getActiveStatus() {
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
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public List<UserAddress> getAddresses() {
        return addresses;
    }
    public void setAddresses(List<UserAddress> addresses) {
        this.addresses = addresses;
    }
    public List<Car> getCarDetails() {
        return carDetails;
    }
    public void setCarDetails(List<Car> carDetails) {
        this.carDetails = carDetails;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    @Override
    public String toString() {
        return "CurrentUserInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dob=" + dob +
                ", password='" + password + '\'' +
                ", activeStatus=" + activeStatus +
                ", registrationDate=" + registrationDate +
                ", email='" + email + '\'' +
                ", phNo='" + phNo + '\'' +
                ", activationCd='" + activationCd + '\'' +
                ", gender='" + gender + '\'' +
                ", profilePicPath=" + profilePicPath +
                ", addresses=" + addresses +
                ", carDetails=" + carDetails +
                '}';
    }

    public Car getCarByNumber(String carNumber)
    {
        Car car = null;
        for(Car tmpCar : carDetails)
        {
            if(carNumber.equals(tmpCar.getCarNo()))
            { car = tmpCar;
                break;
            }
        }
        return car;
    }

    public static void reset()
    {
        ourInstance=new CurrentUserInfo();
    }


}
