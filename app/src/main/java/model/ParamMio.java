package model;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ParamMio implements Parcelable{

    private String EMP_ID;
    private String EMPLOYEE_NAME;
    private String MIOCode;
    private String MIOName;
    private String Latitude;
    private String Longitude;
    private String LLAddress;
    private String DateTime;
    private ArrayList<ParamMarket> Markets = new ArrayList<>();


    public String getEMP_ID() {
        return EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getEMPLOYEE_NAME() {
        return EMPLOYEE_NAME;
    }

    public void setEMPLOYEE_NAME(String EMPLOYEE_NAME) {
        this.EMPLOYEE_NAME = EMPLOYEE_NAME;
    }

    public String getMIOCode() {
        return MIOCode;
    }

    public void setMIOCode(String MIOCode) {
        this.MIOCode = MIOCode;
    }

    public String getMIOName() {
        return MIOName;
    }

    public void setMIOName(String MIOName) {
        this.MIOName = MIOName;
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

    public String getLLAddress() {
        return LLAddress;
    }

    public void setLLAddress(String LLAddress) {
        this.LLAddress = LLAddress;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public ArrayList<ParamMarket> getMarkets() {
        return Markets;
    }

    public void setMarkets(ArrayList<ParamMarket> markets) {
        Markets = markets;
    }

    public ParamMio(String EMP_ID, String EMPLOYEE_NAME){
        this.EMP_ID = EMP_ID;
        this.EMPLOYEE_NAME=EMPLOYEE_NAME;
    }

    public ParamMio(){}


    private ParamMio(Parcel in) {
        EMP_ID = in.readString();
        EMPLOYEE_NAME = in.readString();

        MIOCode = in.readString();
        MIOName = in.readString();
        Latitude = in.readString();
        Longitude = in.readString();
        LLAddress = in.readString();
        DateTime = in.readString();

        Markets = new ArrayList<ParamMarket>();
        in.readList(Markets,(ClassLoader)ParamMio.CREATOR);

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(EMP_ID);
        dest.writeString(EMPLOYEE_NAME);

        dest.writeString(MIOCode);
        dest.writeString(MIOName);
        dest.writeString(Latitude);
        dest.writeString(Longitude);
        dest.writeString(LLAddress);
        dest.writeString(DateTime);
        dest.writeTypedList(Markets);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParamMio> CREATOR = new Creator<ParamMio>() {
        @Override
        public ParamMio createFromParcel(Parcel in) {
            return new ParamMio(in);
        }
        @Override
        public ParamMio[] newArray(int size) {
            return new ParamMio[size];
        }
    };
}
