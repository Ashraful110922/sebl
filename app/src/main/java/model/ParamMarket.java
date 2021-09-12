package model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class ParamMarket implements Parcelable{
    private int MarketId;
    private String Code;
    private String Name;
    private String Address;
    private String Latitude;
    private String Longitude;
    private int IsScheduled;
    private ArrayList<ParamDoctor> Doctors = new ArrayList<ParamDoctor>();
    private ArrayList<ParamChemist> Chemists= new ArrayList<ParamChemist>();

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<ParamDoctor> getDoctors() {
        return Doctors;
    }

    public void setDoctors(ArrayList<ParamDoctor> doctors) {
        Doctors = doctors;
    }

    public ArrayList<ParamChemist> getChemists() {
        return Chemists;
    }

    public void setChemists(ArrayList<ParamChemist> chemists) {
        Chemists = chemists;
    }
    public int getMarketId() {
        return MarketId;
    }

    public void setMarketId(int marketId) {
        MarketId = marketId;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public int getIsScheduled() {
        return IsScheduled;
    }

    public void setIsScheduled(int isScheduled) {
        IsScheduled = isScheduled;
    }

    public ParamMarket(String Code, String Name){
        this.Code= Code;
        this.Name =Name;
    }

    public ParamMarket(){

    }

    private ParamMarket(Parcel in) {
        MarketId = in.readInt();
        Code = in.readString();
        Name = in.readString();
        Address = in.readString();
        Latitude = in.readString();
        Longitude = in.readString();
        IsScheduled = in.readInt();
        Doctors = new ArrayList<ParamDoctor>();
        in.readList(Doctors, (ClassLoader)ParamDoctor.CREATOR);
        Chemists = new ArrayList<ParamChemist>();
        in.readList(Chemists, (ClassLoader)ParamChemist.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(MarketId);
        dest.writeString(Code);
        dest.writeString(Name);
        dest.writeString(Address);
        dest.writeString(Latitude);
        dest.writeString(Longitude);
        dest.writeInt(IsScheduled);
        dest.writeTypedList(Doctors);
        dest.writeTypedList(Chemists);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParamMarket> CREATOR = new Creator<ParamMarket>() {
        @Override
        public ParamMarket createFromParcel(Parcel in) {
            return new ParamMarket(in);
        }
        @Override
        public ParamMarket[] newArray(int size) {
            return new ParamMarket[size];
        }
    };
}
