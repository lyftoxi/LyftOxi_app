package com.lyftoxi.lyftoxi.dao;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class User {
	private String id;
	private String name;;
	private Date dob;
	private String password;
	private String userStatus;
	private Date registrationDate;
	private String email;
	private String phNo;
	private String gender;
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
	public Date getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
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

	@Override
	public String toString() {
		return "User{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", dob=" + dob +
				", password='" + password + '\'' +
				", userStatus='" + userStatus + '\'' +
				", registrationDate=" + registrationDate +
				", email='" + email + '\'' +
				", phNo='" + phNo + '\'' +
				", gender='" + gender + '\'' +
				", addresses=" + addresses +
				", carDetails=" + carDetails +
				'}';
	}
}