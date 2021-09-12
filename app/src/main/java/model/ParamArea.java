package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ParamArea implements Parcelable{
    private String AREA_CODE;
    private String AreaName;
    private ArrayList<ParamTerritory> Territories = new ArrayList<ParamTerritory>();

    public void setAREA_CODE(String AREA_CODE) {
        this.AREA_CODE = AREA_CODE;
    }

    public String getAreaName() {
        return AreaName;
    }

    public void setAreaName(String areaName) {
        AreaName = areaName;
    }

    public ArrayList<ParamTerritory> getTerritories() {
        return Territories;
    }

    public void setTerritories(ArrayList<ParamTerritory> territories) {
        Territories = territories;
    }

    public String getAREA_CODE() {
        return AREA_CODE;
    }



    public ParamArea(String AREA_CODE, String AreaName){
        this.AREA_CODE= AREA_CODE;
        this.AreaName =AreaName;
    }


    public ParamArea(){

    }


    private ParamArea(Parcel in) {
        AREA_CODE = in.readString();
        AreaName = in.readString();
        Territories = new ArrayList<ParamTerritory>();
        in.readList(Territories, (ClassLoader)ParamTerritory.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(AREA_CODE);
        dest.writeString(AreaName);
        dest.writeTypedList(Territories);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParamArea> CREATOR = new Creator<ParamArea>() {
        @Override
        public ParamArea createFromParcel(Parcel in) {
            return new ParamArea(in);
        }
        @Override
        public ParamArea[] newArray(int size) {
            return new ParamArea[size];
        }
    };
}
