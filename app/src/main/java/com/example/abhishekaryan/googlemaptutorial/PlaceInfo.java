package com.example.abhishekaryan.googlemaptutorial;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {

    private String name;
    private String address;
    private String phoneNbr;
    private LatLng latlng;

    public PlaceInfo() {
    }

    public PlaceInfo(String name, String address, String phoneNbr, LatLng latlng) {
        this.name = name;
        this.address = address;
        this.phoneNbr = phoneNbr;
        this.latlng = latlng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNbr() {
        return phoneNbr;
    }

    public void setPhoneNbr(String phoneNbr) {
        this.phoneNbr = phoneNbr;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNbr='" + phoneNbr + '\'' +
                ", latlng=" + latlng +
                '}';
    }
}
