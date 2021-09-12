package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ParamRegion implements Parcelable{
    private String REGION_CODE;
    private String RegionName;
    private ArrayList<ParamArea> Areas = new ArrayList<ParamArea>();


    public ParamRegion(String REGION_CODE, String RegionName){
        this.REGION_CODE= REGION_CODE;
        this.RegionName =RegionName;
    }

    public String getREGION_CODE() {
        return REGION_CODE;
    }

    public void setREGION_CODE(String REGION_CODE) {
        this.REGION_CODE = REGION_CODE;
    }

    public String getRegionName() {
        return RegionName;
    }

    public void setRegionName(String regionName) {
        RegionName = regionName;
    }

    public ArrayList<ParamArea> getAreas() {
        return Areas;
    }

    public void setAreas(ArrayList<ParamArea> areas) {
        Areas = areas;
    }

    public ParamRegion(){

    }


    private ParamRegion(Parcel in) {
        REGION_CODE = in.readString();
        RegionName = in.readString();
        Areas = new ArrayList<ParamArea>();
        in.readList(Areas, (ClassLoader)ParamArea.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(REGION_CODE);
        dest.writeString(RegionName);
        dest.writeTypedList(Areas);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParamRegion> CREATOR = new Creator<ParamRegion>() {
        @Override
        public ParamRegion createFromParcel(Parcel in) {
            return new ParamRegion(in);
        }
        @Override
        public ParamRegion[] newArray(int size) {
            return new ParamRegion[size];
        }
    };
}
