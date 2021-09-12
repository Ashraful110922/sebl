package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Brand implements Parcelable{
    private String brName;
    private List<Temp> sections = new ArrayList<Temp>();

    public String getBrName() {
        return brName;
    }

    public void setBrName(String brName) {
        this.brName = brName;
    }

    public List<Temp> getSections() {
        return sections;
    }

    public void setSections(List<Temp> sections) {
        this.sections = sections;
    }

    public static Creator<Brand> getCREATOR() {
        return CREATOR;
    }

    public Brand(String brName, List<Temp> sections){
        this.brName= brName;
        this.sections =sections;
    }


    public Brand(){

    }


    private Brand(Parcel in) {
        brName = in.readString();
        sections = new ArrayList<Temp>();
        in.readList(sections, (ClassLoader)ParamRegion.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(brName);
        dest.writeTypedList(sections);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Brand> CREATOR = new Creator<Brand>() {
        @Override
        public Brand createFromParcel(Parcel in) {
            return new Brand(in);
        }
        @Override
        public Brand[] newArray(int size) {
            return new Brand[size];
        }
    };
}
