package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.justraspberry.jobdeal.Util;

import java.util.TimeZone;

public class User implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("zip")
    @Expose
    private String zip;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("bankid")
    @Expose
    private String bankid;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("rate")
    @Expose
    private RateInfo rateInfo = new RateInfo();
    @SerializedName("roleId")
    @Expose
    private Integer role;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("bankId")
    @Expose
    private String bankId;
    @SerializedName("subscription")
    @Expose
    private String subscription;
    @SerializedName("myInfo")
    @Expose
    private MyInfo myInfo;
    @SerializedName("notificationCount")
    @Expose
    private Integer notificationCount = 0;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("aboutMe")
    @Expose
    private String aboutMe;
    @SerializedName("timezone")
    @Expose
    private String timezone;


    public final static Parcelable.Creator<User> CREATOR = new Creator<User>() {


        @SuppressWarnings({
                "unchecked"
        })
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return (new User[size]);
        }

    };

    protected User(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.surname = ((String) in.readValue((String.class.getClassLoader())));
        this.mobile = ((String) in.readValue((String.class.getClassLoader())));
        this.email = ((String) in.readValue((String.class.getClassLoader())));
        this.address = ((String) in.readValue((String.class.getClassLoader())));
        this.zip = ((String) in.readValue((String.class.getClassLoader())));
        this.city = ((String) in.readValue((String.class.getClassLoader())));
        this.country = ((String) in.readValue((String.class.getClassLoader())));
        this.locale = ((String) in.readValue((String.class.getClassLoader())));
        this.active = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.bankid = ((String) in.readValue((String.class.getClassLoader())));
        this.createdAt = ((String) in.readValue((String.class.getClassLoader())));
        this.password = ((String) in.readValue((String.class.getClassLoader())));
        this.rateInfo = ((RateInfo) in.readValue((RateInfo.class.getClassLoader())));
        this.role = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.avatar = ((String) in.readValue((String.class.getClassLoader())));
        this.currency = ((String) in.readValue((String.class.getClassLoader())));
        this.bankId = ((String) in.readValue((String.class.getClassLoader())));
        this.subscription = ((String) in.readValue((String.class.getClassLoader())));
        this.myInfo = ((MyInfo) in.readValue((MyInfo.class.getClassLoader())));
        this.notificationCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.uid = ((String) in.readValue((String.class.getClassLoader())));
        this.code = ((String) in.readValue((String.class.getClassLoader())));
        this.aboutMe = ((String) in.readValue((String.class.getClassLoader())));
        this.timezone = ((String) in.readValue((String.class.getClassLoader())));
    }

    public User() {
        this.timezone = TimeZone.getDefault().getID();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User withName(String name) {
        this.name = name;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public User withSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public User withMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User withAddress(String address) {
        this.address = address;
        return this;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public User withZip(String zip) {
        this.zip = zip;
        return this;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public User withCity(String city) {
        this.city = city;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public User withCountry(String country) {
        this.country = country;
        return this;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public User withLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User withActive(Boolean active) {
        this.active = active;
        return this;
    }

    public String getBankid() {
        return bankid;
    }

    public void setBankid(String bankid) {
        this.bankid = bankid;
    }

    public User withBankid(String bankid) {
        this.bankid = bankid;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getFullName() {
        return getName() + " " + getSurname();
    }

    public RateInfo getRateInfo() {
        return rateInfo;
    }

    public void setRateInfo(RateInfo rateInfo) {
        this.rateInfo = rateInfo;
    }

    public MyInfo getMyInfo() {
        return myInfo;
    }

    public void setMyInfo(MyInfo myInfo) {
        this.myInfo = myInfo;
    }

    public Integer getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(Integer notificationCount) {
        this.notificationCount = notificationCount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(surname);
        dest.writeValue(mobile);
        dest.writeValue(email);
        dest.writeValue(address);
        dest.writeValue(zip);
        dest.writeValue(city);
        dest.writeValue(country);
        dest.writeValue(locale);
        dest.writeValue(active);
        dest.writeValue(bankid);
        dest.writeValue(createdAt);
        dest.writeValue(password);
        dest.writeValue(rateInfo);
        dest.writeValue(role);
        dest.writeValue(avatar);
        dest.writeValue(currency);
        dest.writeValue(bankId);
        dest.writeValue(subscription);
        dest.writeValue(myInfo);
        dest.writeValue(notificationCount);
        dest.writeValue(uid);
        dest.writeValue(code);
        dest.writeValue(aboutMe);
        dest.writeValue(timezone);
    }

    public int describeContents() {
        return 0;
    }

}
