package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MoveData implements Parcelable{
    private String address;
    private String latitude;
    private String longitude;
    private String visitDateTime;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getVisitDateTime() {
        return visitDateTime;
    }

    public void setVisitDateTime(String visitDateTime) {
        this.visitDateTime = visitDateTime;
    }

    public MoveData(){}




    private MoveData(Parcel in) {
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        visitDateTime = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(visitDateTime);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MoveData> CREATOR = new Creator<MoveData>() {
        @Override
        public MoveData createFromParcel(Parcel in) {
            return new MoveData(in);
        }
        @Override
        public MoveData[] newArray(int size) {
            return new MoveData[size];
        }
    };
}
