package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ParamDepot implements Parcelable{
    private String DEPOT_CODE;
    private String DepotName;
    private ArrayList<ParamRegion> Regions = new ArrayList<ParamRegion>();

    public String getDEPOT_CODE() {
        return DEPOT_CODE;
    }

    public void setDEPOT_CODE(String DEPOT_CODE) {
        this.DEPOT_CODE = DEPOT_CODE;
    }

    public String getDepotName() {
        return DepotName;
    }

    public void setDepotName(String depotName) {
        DepotName = depotName;
    }

    public ArrayList<ParamRegion> getRegions() {
        return Regions;
    }

    public void setRegions(ArrayList<ParamRegion> regions) {
        Regions = regions;
    }

    public ParamDepot(String DEPOT_CODE, String DepotName){
        this.DEPOT_CODE= DEPOT_CODE;
        this.DepotName =DepotName;
    }


    public ParamDepot(){

    }


    private ParamDepot(Parcel in) {
        DEPOT_CODE = in.readString();
        DepotName = in.readString();
        Regions = new ArrayList<ParamRegion>();
        in.readList(Regions, (ClassLoader)ParamRegion.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(DEPOT_CODE);
        dest.writeString(DepotName);
        dest.writeTypedList(Regions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParamDepot> CREATOR = new Creator<ParamDepot>() {
        @Override
        public ParamDepot createFromParcel(Parcel in) {
            return new ParamDepot(in);
        }
        @Override
        public ParamDepot[] newArray(int size) {
            return new ParamDepot[size];
        }
    };
}
