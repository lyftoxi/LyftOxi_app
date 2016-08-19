package com.lyftoxi.lyftoxi;


public class CarInfo {
    private String carNo;
    private String carColor;
    private String carModel;
    private String carBrand;
    private int noOfSeat;
    private boolean acAvailable;
    private boolean airbagAvailable;
    private boolean musicAvailable;
    private boolean smokingAllowed;
    private boolean luggageAllowed;
    private boolean showDeleteButton = true;
    private boolean showProgress = false;
    public String getCarNo() {
        return carNo;
    }
    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }
    public String getCarColor() {
        return carColor;
    }
    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }
    public String getCarModel() {
        return carModel;
    }
    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }
    public String getCarBrand() {
        return carBrand;
    }
    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }
    public int getNoOfSeat() {
        return noOfSeat;
    }
    public void setNoOfSeat(int noOfSeat) {
        this.noOfSeat = noOfSeat;
    }
    public boolean isAcAvailable() {
        return acAvailable;
    }
    public void setAcAvailable(boolean acAvailable) {
        this.acAvailable = acAvailable;
    }
    public boolean isAirbagAvailable() {
        return airbagAvailable;
    }
    public void setAirbagAvailable(boolean airbagAvailable) {
        this.airbagAvailable = airbagAvailable;
    }
    public boolean isMusicAvailable() {
        return musicAvailable;
    }
    public void setMusicAvailable(boolean musicAvailable) {
        this.musicAvailable = musicAvailable;
    }
    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }
    public void setSmokingAllowed(boolean smokingAllowed) {
        this.smokingAllowed = smokingAllowed;
    }
    public boolean isLuggageAllowed() {
        return luggageAllowed;
    }
    public void setLuggageAllowed(boolean luggageAllowed) {
        this.luggageAllowed = luggageAllowed;
    }

    public boolean isShowDeleteButton() {
        return showDeleteButton;
    }

    public void setShowDeleteButton(boolean showDeleteButton) {
        this.showDeleteButton = showDeleteButton;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    @Override
    public String toString() {
        return "CarInfo{" +
                "carNo='" + carNo + '\'' +
                ", carColor='" + carColor + '\'' +
                ", carModel='" + carModel + '\'' +
                ", carBrand='" + carBrand + '\'' +
                ", noOfSeat=" + noOfSeat +
                ", acAvailable=" + acAvailable +
                ", airbagAvailable=" + airbagAvailable +
                ", musicAvailable=" + musicAvailable +
                ", smokingAllowed=" + smokingAllowed +
                ", luggageAllowed=" + luggageAllowed +
                ", showDeleteButton=" + showDeleteButton +
                ", showProgress=" + showProgress +
                '}';
    }
}
