package model;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class ParamSchedule implements Parcelable{

    private int MarketScheduleID;
    private int RosterID;
    private String VisitDate;
    private String VisitTime;
    private String Opinion;
    private String ScheduleNo;
    private ArrayList<ParamZone> Zones = new ArrayList<>();


    public int getMarketScheduleID() {
        return MarketScheduleID;
    }

    public void setMarketScheduleID(int marketScheduleID) {
        MarketScheduleID = marketScheduleID;
    }

    public int getRosterID() {
        return RosterID;
    }

    public void setRosterID(int rosterID) {
        RosterID = rosterID;
    }

    public String getVisitDate() {
        return VisitDate;
    }

    public void setVisitDate(String visitDate) {
        VisitDate = visitDate;
    }

    public String getVisitTime() {
        return VisitTime;
    }

    public void setVisitTime(String visitTime) {
        VisitTime = visitTime;
    }

    public String getOpinion() {
        return Opinion;
    }

    public void setOpinion(String opinion) {
        Opinion = opinion;
    }

    public ArrayList<ParamZone> getZones() {
        return Zones;
    }

    public void setZones(ArrayList<ParamZone> zones) {
        Zones = zones;
    }

    public ParamSchedule(int MarketScheduleID, String ScheduleNo){
        this.MarketScheduleID = MarketScheduleID;
        this.ScheduleNo=ScheduleNo;
    }

    public String getScheduleNo() {
        return ScheduleNo;
    }

    public void setScheduleNo(String scheduleNo) {
        ScheduleNo = scheduleNo;
    }

    public ParamSchedule(){}


    private ParamSchedule(Parcel in) {
        MarketScheduleID= in.readInt();
        RosterID = in.readInt();
        VisitDate = in.readString();
        VisitTime = in.readString();
        Opinion = in.readString();
        ScheduleNo= in.readString();
        Zones = new ArrayList<ParamZone>();
        in.readList(Zones,(ClassLoader) ParamSchedule.CREATOR);

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(MarketScheduleID);
        dest.writeInt(RosterID);
        dest.writeString(VisitDate);
        dest.writeString(VisitTime);
        dest.writeString(Opinion);
        dest.writeString(ScheduleNo);
        dest.writeTypedList(Zones);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParamSchedule> CREATOR = new Creator<ParamSchedule>() {
        @Override
        public ParamSchedule createFromParcel(Parcel in) {
            return new ParamSchedule(in);
        }
        @Override
        public ParamSchedule[] newArray(int size) {
            return new ParamSchedule[size];
        }
    };
}
