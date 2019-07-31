package com.joytechnologies.market.Model;

public class SearchDataModel {
    String Name;
    String item;
    String latitude;
    String longitude;
    String Address;
    String Website;
    String Phone_no;
    String Offday;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String website) {
        Website = website;
    }

    public String getPhone_no() {
        return Phone_no;
    }

    public void setPhone_no(String phone_no) {
        Phone_no = phone_no;
    }

    public String getOffday() {
        return Offday;
    }

    public void setOffday(String offday) {
        Offday = offday;
    }

    public SearchDataModel() {
    }

    public SearchDataModel(String name, String item, String latitude, String longitude, String address, String website, String phone_no, String offday) {
        Name = name;
        this.item = item;
        this.latitude = latitude;
        this.longitude = longitude;
        Address = address;
        Website = website;
        Phone_no = phone_no;
        Offday = offday;
    }
}
