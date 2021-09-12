package model;

import android.os.Parcel;
import android.os.Parcelable;

public class LatLon implements Parcelable{
    private int id;
    private String name;
    private String lat_lng;
    private String sales_id;
    private float collection_amount;
    private String collection_date;
    private String collection_note;
    private String date;
    private String from_date;
    private String to_date;
    private boolean assigned;
    private int Attended;
    private String MonthName;
    private int MonthNumber;
    private int Year;
    private int day;
    private String dayName;
    private String check_in;
    private String check_out;
    private String holiday;
    private String leaves;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat_lng() {
        return lat_lng;
    }

    public void setLat_lng(String lat_lng) {
        this.lat_lng = lat_lng;
    }

    public String getSales_id() {
        return sales_id;
    }

    public void setSales_id(String sales_id) {
        this.sales_id = sales_id;
    }

    public float getCollection_amount() {
        return collection_amount;
    }

    public void setCollection_amount(float collection_amount) {
        this.collection_amount = collection_amount;
    }

    public String getCollection_date() {
        return collection_date;
    }

    public void setCollection_date(String collection_date) {
        this.collection_date = collection_date;
    }

    public String getCollection_note() {
        return collection_note;
    }

    public void setCollection_note(String collection_note) {
        this.collection_note = collection_note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public int getAttended() {
        return Attended;
    }

    public void setAttended(int attended) {
        Attended = attended;
    }

    public String getMonthName() {
        return MonthName;
    }

    public void setMonthName(String monthName) {
        MonthName = monthName;
    }

    public int getMonthNumber() {
        return MonthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        MonthNumber = monthNumber;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }


    public String getCheck_in() {
        return check_in;
    }

    public void setCheck_in(String check_in) {
        this.check_in = check_in;
    }

    public String getCheck_out() {
        return check_out;
    }

    public void setCheck_out(String check_out) {
        this.check_out = check_out;
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    public String getLeaves() {
        return leaves;
    }

    public void setLeaves(String leaves) {
        this.leaves = leaves;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LatLon(){}

    public LatLon(int id,String name){
        this.id= id;
        this.name =name;
    }

    private LatLon(Parcel in) {
        id = in.readInt();
        name = in.readString();
        lat_lng = in.readString();
        sales_id = in.readString();
        collection_amount = in.readFloat();
        collection_date = in.readString();
        collection_note = in.readString();
        date = in.readString();
        assigned = in.readInt() == 1;
        from_date = in.readString();
        to_date = in.readString();
        Attended = in.readInt();
        MonthName = in.readString();
        MonthNumber = in.readInt();
        Year = in.readInt();
        day = in.readInt();
        dayName=in.readString();
        check_in = in.readString();
        check_out = in.readString();
        holiday = in.readString();
        leaves = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(lat_lng);
        dest.writeString(sales_id);
        dest.writeFloat(collection_amount);
        dest.writeString(collection_date);
        dest.writeString(collection_note);
        dest.writeString(date);
        dest.writeInt(assigned ? 1 : 0);
        dest.writeString(from_date);
        dest.writeString(to_date);
        dest.writeInt(Attended);
        dest.writeString(MonthName);
        dest.writeInt(MonthNumber);
        dest.writeInt(Year);
        dest.writeInt(day);
        dest.writeString(dayName);
        dest.writeString(check_in);
        dest.writeString(check_out);
        dest.writeString(holiday);
        dest.writeString(leaves);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LatLon> CREATOR = new Creator<LatLon>() {
        @Override
        public LatLon createFromParcel(Parcel in) {
            return new LatLon(in);
        }
        @Override
        public LatLon[] newArray(int size) {
            return new LatLon[size];
        }
    };
}
