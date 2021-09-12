package model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class ParamZone implements Parcelable{
    private String ZONE_CODE;
    private String ZoneName;
    private ArrayList<ParamDepot> Depots = new ArrayList<ParamDepot>();

    public String getZONE_CODE() {
        return ZONE_CODE;
    }

    public void setZONE_CODE(String ZONE_CODE) {
        this.ZONE_CODE = ZONE_CODE;
    }

    public String getZoneName() {
        return ZoneName;
    }

    public void setZoneName(String zoneName) {
        ZoneName = zoneName;
    }

    public ArrayList<ParamDepot> getDepots() {
        return Depots;
    }

    public void setDepots(ArrayList<ParamDepot> depots) {
        Depots = depots;
    }

    public ParamZone(String ZONE_CODE, String ZoneName){
        this.ZONE_CODE= ZONE_CODE;
        this.ZoneName =ZoneName;
    }

    public ParamZone(){

    }

    private ParamZone(Parcel in) {
        ZONE_CODE = in.readString();
        ZoneName = in.readString();
        Depots = new ArrayList<ParamDepot>();
        in.readList(Depots, (ClassLoader)ParamDepot.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ZONE_CODE);
        dest.writeString(ZoneName);
        dest.writeTypedList(Depots);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParamZone> CREATOR = new Creator<ParamZone>() {
        @Override
        public ParamZone createFromParcel(Parcel in) {
            return new ParamZone(in);
        }
        @Override
        public ParamZone[] newArray(int size) {
            return new ParamZone[size];
        }
    };
}
