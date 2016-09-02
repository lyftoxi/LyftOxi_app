package com.lyftoxi.lyftoxi.dao;

import java.util.Date;

public class TakeRide {
    private String id;
    private String shareRideObjId;
    private Date rideTime;
    private int fare;
    private String ownerObjId;
    private String ownerName;
    private String ownerMobileNo;
    private Location source;
    private Location destination;
    private String interestedUserObjId;
    private String interestedUserName;
    private String interestedUserMobileNo;
    private String shareRideStatus = "A";
    private String interestedUserGender;
    private Date interestedUserDob;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShareRideObjId() {
        return shareRideObjId;
    }

    public void setShareRideObjId(String shareRideObjId) {
        this.shareRideObjId = shareRideObjId;
    }

    public Date getRideTime() {
        return rideTime;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }

    public void setRideTime(Date rideTime) {
        this.rideTime = rideTime;
    }

    public String getOwnerObjId() {
        return ownerObjId;
    }

    public void setOwnerObjId(String ownerObjId) {
        this.ownerObjId = ownerObjId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerMobileNo() {
        return ownerMobileNo;
    }

    public void setOwnerMobileNo(String ownerMobileNo) {
        this.ownerMobileNo = ownerMobileNo;
    }

    public Location getSource() {
        return source;
    }

    public void setSource(Location source) {
        this.source = source;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public String getInterestedUserObjId() {
        return interestedUserObjId;
    }

    public void setInterestedUserObjId(String interestedUserObjId) {
        this.interestedUserObjId = interestedUserObjId;
    }

    public String getInterestedUserName() {
        return interestedUserName;
    }

    public void setInterestedUserName(String interestedUserName) {
        this.interestedUserName = interestedUserName;
    }

    public String getInterestedUserMobileNo() {
        return interestedUserMobileNo;
    }

    public void setInterestedUserMobileNo(String interestedUserMobileNo) {
        this.interestedUserMobileNo = interestedUserMobileNo;
    }

    public String getShareRideStatus() {
        return shareRideStatus;
    }

    public void setShareRideStatus(String shareRideStatus) {
        this.shareRideStatus = shareRideStatus;
    }

    public String getInterestedUserGender() {
        return interestedUserGender;
    }

    public void setInterestedUserGender(String interestedUserGender) {
        this.interestedUserGender = interestedUserGender;
    }

    public Date getInterestedUserDob() {
        return interestedUserDob;
    }

    public void setInterestedUserDob(Date interestedUserDob) {
        this.interestedUserDob = interestedUserDob;
    }

    @Override
    public String toString() {
        return "TakeRide{" +
                "id='" + id + '\'' +
                ", shareRideObjId='" + shareRideObjId + '\'' +
                ", rideTime=" + rideTime +
                ", fare=" + fare +
                ", ownerObjId='" + ownerObjId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", ownerMobileNo='" + ownerMobileNo + '\'' +
                ", source=" + source +
                ", destination=" + destination +
                ", interestedUserObjId='" + interestedUserObjId + '\'' +
                ", interestedUserName='" + interestedUserName + '\'' +
                ", interestedUserMobileNo='" + interestedUserMobileNo + '\'' +
                ", shareRideStatus='" + shareRideStatus + '\'' +
                ", interestedUserGender='" + interestedUserGender + '\'' +
                ", interestedUserDob=" + interestedUserDob +
                '}';
    }
}
