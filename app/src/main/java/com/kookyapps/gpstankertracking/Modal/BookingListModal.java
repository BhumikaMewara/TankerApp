package com.kookyapps.gpstankertracking.Modal;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class BookingListModal implements Parcelable {
    String bookingid, distance, fromlocation, tolocation, fromtime, totime, fromaddress, toaddress;
    String fromlatitude, fromlongitude, tolatitude, tologitude;
    String phone_country_code, phone, controller_name, geofence_in_meter, message;
    String driver_name,can_start,can_accept,can_end;




    public String getDriver_name() { return driver_name; }
    public void setDriver_name(String driver_name) { this.driver_name = driver_name; }
    public String getCan_start() { return can_start; }
    public void setCan_start(String can_start) { this.can_start = can_start; }
    public String getCan_accept() { return can_accept; }
    public void setCan_accept(String can_accept) { this.can_accept = can_accept; }
    public String getCan_end() { return can_end; }
    public void setCan_end(String can_end) { this.can_end = can_end; }

    public BookingListModal(){}
    public String getFromaddress() {
        return fromaddress;
    }

    public void setFromaddress(String fromaddress) {
        this.fromaddress = fromaddress;
    }

    public String getToaddress() {
        return toaddress;
    }

    public void setToaddress(String toaddress) {
        this.toaddress = toaddress;
    }

    public String getPhone_country_code() {
        return phone_country_code;
    }

    public void setPhone_country_code(String phone_country_code) {
        this.phone_country_code = phone_country_code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getController_name() {
        return controller_name;
    }

    public void setController_name(String controller_name) {
        this.controller_name = controller_name;
    }


    public String getGeofence_in_meter() {
        return geofence_in_meter;
    }

    public void setGeofence_in_meter(String geofence_in_meter) {
        this.geofence_in_meter = geofence_in_meter;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getFromlatitude() {
        return fromlatitude;
    }

    public void setFromlatitude(String fromlatitude) {
        this.fromlatitude = fromlatitude;
    }

    public String getFromlongitude() {
        return fromlongitude;
    }

    public void setFromlongitude(String fromlongitude) {
        this.fromlongitude = fromlongitude;
    }

    public String getTolatitude() {
        return tolatitude;
    }

    public void setTolatitude(String tolatitude) {
        this.tolatitude = tolatitude;
    }

    public String getTologitude() {
        return tologitude;
    }

    public void setTologitude(String tologitude) {
        this.tologitude = tologitude;
    }


    public String getBookingid() {
        return bookingid;
    }

    public void setBookingid(String bookingid) {
        this.bookingid = bookingid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getFromlocation() {
        return fromlocation;
    }

    public void setFromlocation(String fromlocation) {
        this.fromlocation = fromlocation;
    }

    public String getTolocation() {
        return tolocation;
    }

    public void setTolocation(String tolocation) {
        this.tolocation = tolocation;
    }

    public String getFromtime() {
        return fromtime;
    }

    public void setFromtime(String fromtime) {
        this.fromtime = fromtime;
    }

    public String getTotime() {
        return totime;
    }

    public void setTotime(String totime) {
        this.totime = totime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(bookingid);
        parcel.writeString(distance);
        parcel.writeString(fromlocation);
        parcel.writeString(tolocation);
        parcel.writeString(fromtime);
        parcel.writeString(totime);
        parcel.writeString(fromlatitude);
        parcel.writeString(fromlongitude);
        parcel.writeString(tolatitude);
        parcel.writeString(tologitude);
        parcel.writeString(phone_country_code);
        parcel.writeString(phone);
        parcel.writeString(controller_name);
        parcel.writeString(geofence_in_meter);
        parcel.writeString(message);
        parcel.writeString(fromaddress);
        parcel.writeString(toaddress);
        parcel.writeString(driver_name);
        parcel.writeString(can_accept);
        parcel.writeString(can_start);
        parcel.writeString(can_end);
    }

    protected BookingListModal(Parcel in){
        bookingid = in.readString();
        distance=in.readString();
        fromlocation=in.readString();
        tolocation=in.readString();
        fromtime=in.readString();
        totime=in.readString();
        fromlatitude=in.readString();
        fromlongitude=in.readString();
        tolatitude=in.readString();
        tologitude=in.readString();
        phone_country_code=in.readString();
        phone=in.readString();
        controller_name=in.readString();
        geofence_in_meter=in.readString();
        message=in.readString();
        fromaddress=in.readString();
        toaddress=in.readString();
        driver_name=in.readString();
        can_accept=in.readString();
        can_start=in.readString();
        can_end=in.readString();
    }




    public static final Creator<BookingListModal> CREATOR = new Creator<BookingListModal>() {
        @Override
        public BookingListModal createFromParcel(Parcel parcel) {
            return new BookingListModal(parcel);
        }

        @Override
        public BookingListModal[] newArray(int i) {
            return new BookingListModal[i];
        }
    };
}
