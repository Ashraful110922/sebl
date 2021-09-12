package model;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ParamEmp implements Parcelable{
    private String MIOCode;
    private String MIOName;
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

    public ParamEmp(String MIOCode, String MIOName){
        this.MIOCode = MIOCode;
        this.MIOName=MIOName;
    }


    public ParamEmp(){}

    private ParamEmp(Parcel in) {
        MIOCode = in.readString();
        MIOName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(MIOCode);
        dest.writeString(MIOName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParamEmp> CREATOR = new Creator<ParamEmp>() {
        @Override
        public ParamEmp createFromParcel(Parcel in) {
            return new ParamEmp(in);
        }
        @Override
        public ParamEmp[] newArray(int size) {
            return new ParamEmp[size];
        }
    };
}
