package model;

import android.os.Parcel;
import android.os.Parcelable;

public class Temp implements Parcelable{
    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Temp(){}

    public Temp(String code, String name){
        this.code= code;
        this.name =name;
    }

    private Temp(Parcel in) {
        code = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Temp> CREATOR = new Creator<Temp>() {
        @Override
        public Temp createFromParcel(Parcel in) {
            return new Temp(in);
        }
        @Override
        public Temp[] newArray(int size) {
            return new Temp[size];
        }
    };
}
