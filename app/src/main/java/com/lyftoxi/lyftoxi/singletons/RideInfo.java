package com.lyftoxi.lyftoxi.singletons;

import com.lyftoxi.lyftoxi.CarInfo;
import com.lyftoxi.lyftoxi.UserInfo;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;


public class RideInfo {

    private UserInfo rideOf;
    private String id, name, sourceName, viaName, DestinationName, userMessage;
    private CarInfo car;
    private LatLng source,via,destination;
    private Date starTime;
    private int fare, interestedUserCount, daysToRepeat;
    private boolean interested;
    private String status;

    private RideInfo()
    {

    }

    private static RideInfo instance = new RideInfo();

    public static RideInfo getInstance()
    {
        return instance;
    }

    public UserInfo getRideOf() {
        return rideOf;
    }

    public void setRideOf(UserInfo rideOf) {
        this.rideOf = rideOf;
    }

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

    public CarInfo getCar() {
        return car;
    }

    public void setCar(CarInfo car) {
        this.car = car;
    }

    public LatLng getSource() {
        return source;
    }

    public void setSource(LatLng source) {
        this.source = source;
    }

    public LatLng getVia() {
        return via;
    }

    public void setVia(LatLng via) {
        this.via = via;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public Date getStarTime() {
        return starTime;
    }

    public void setStarTime(Date starTime) {
        this.starTime = starTime;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getViaName() {
        return viaName;
    }

    public void setViaName(String viaName) {
        this.viaName = viaName;
    }

    public String getDestinationName() {
        return DestinationName;
    }

    public void setDestinationName(String destinationName) {
        DestinationName = destinationName;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }

    public boolean isInterested() {
        return interested;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getInterestedUserCount() {
        return interestedUserCount;
    }

    public void setInterestedUserCount(int interestedUserCount) {
        this.interestedUserCount = interestedUserCount;
    }

    public int getDaysToRepeat() {
        return daysToRepeat;
    }

    public void setDaysToRepeat(int daysToRepeat) {
        this.daysToRepeat = daysToRepeat;
    }

    @Override
    public String toString() {
        return "RideInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", viaName='" + viaName + '\'' +
                ", DestinationName='" + DestinationName + '\'' +
                ", userMessage='" + userMessage + '\'' +
                ", car=" + car +
                ", source=" + source +
                ", via=" + via +
                ", destination=" + destination +
                ", starTime=" + starTime +
                ", fare=" + fare +
                ", interestedUserCount=" + interestedUserCount +
                ", daysToRepeat=" + daysToRepeat +
                ", interested=" + interested +
                ", status='" + status + '\'' +
                '}';
    }

    public static void reset()
    {
        instance=new RideInfo();
    }


}
