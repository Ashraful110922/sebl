package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class ParamData implements Parcelable{
    private boolean status;
    private String id;
    private String name;
    private String message;
    private ArrayList<Temp> Search= new ArrayList<Temp>();
    private ArrayList<ParamZone> zones = new ArrayList<ParamZone>();
    private ArrayList<ParamSchedule> Schedules = new ArrayList<ParamSchedule>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ParamZone> getZones() {
        return zones;
    }

    public ArrayList<ParamSchedule> getSchedules() {
        return Schedules;
    }

    public void setSchedules(ArrayList<ParamSchedule> schedules) {
        Schedules = schedules;
    }

    public void setZones(ArrayList<ParamZone> zones) {
        this.zones = zones;
    }

    public ArrayList<Temp> getSearch() {
        return Search;
    }

    public void setSearch(ArrayList<Temp> search) {
        Search = search;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ParamData(String id, String name){
        this.id= id;
        this.name =name;
    }


    public ParamData(){}


    private ParamData(Parcel in) {
        id = in.readString();
        name = in.readString();
        message = in.readString();
        status = in.readInt() == 1;
        Search = new ArrayList<Temp>();
        in.readList(Search,(ClassLoader)Temp.CREATOR);

        Schedules = new ArrayList<ParamSchedule>();
        in.readList(Schedules,(ClassLoader)ParamSchedule.CREATOR);

        zones = new ArrayList<ParamZone>();
        in.readList(zones, (ClassLoader)ParamZone.CREATOR);


    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(message);
        dest.writeInt(status ? 1 : 0);
        dest.writeTypedList(Search);
        dest.writeTypedList(zones);
        dest.writeTypedList(Schedules);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParamData> CREATOR = new Creator<ParamData>() {
        @Override
        public ParamData createFromParcel(Parcel in) {
            return new ParamData(in);
        }
        @Override
        public ParamData[] newArray(int size) {
            return new ParamData[size];
        }
    };
}
