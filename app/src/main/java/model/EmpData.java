package model;

import android.os.Parcel;
import android.os.Parcelable;

public class EmpData implements Parcelable{
    private boolean isSelected;
    private String employee_name;
    private String mobile_no;
    private String CurDesg;
    private String employeeid;
    private String location_code;
    private String location_name;
    private String area_code;
    private String area_name;
    private String territory_code;
    private String territory_name;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getCurDesg() {
        return CurDesg;
    }

    public void setCurDesg(String curDesg) {
        CurDesg = curDesg;
    }

    public String getEmployeeid() {
        return employeeid;
    }

    public void setEmployeeid(String employeeid) {
        this.employeeid = employeeid;
    }

    public String getLocation_code() {
        return location_code;
    }

    public void setLocation_code(String location_code) {
        this.location_code = location_code;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getTerritory_code() {
        return territory_code;
    }

    public void setTerritory_code(String territory_code) {
        this.territory_code = territory_code;
    }

    public String getTerritory_name() {
        return territory_name;
    }

    public void setTerritory_name(String territory_name) {
        this.territory_name = territory_name;
    }




    public EmpData(){}

    private EmpData(Parcel in) {
        isSelected = in.readByte() != 0;
        employee_name = in.readString();
        mobile_no = in.readString();
        CurDesg = in.readString();
        employeeid = in.readString();
        location_code = in.readString();
        location_name = in.readString();
        area_code = in.readString();
        area_name = in.readString();
        territory_code = in.readString();
        territory_name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeString(employee_name);
        dest.writeString(mobile_no);
        dest.writeString(CurDesg);
        dest.writeString(employeeid);
        dest.writeString(location_code);
        dest.writeString(location_name);
        dest.writeString(area_code);
        dest.writeString(area_name);
        dest.writeString(territory_code);
        dest.writeString(territory_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EmpData> CREATOR = new Creator<EmpData>() {
        @Override
        public EmpData createFromParcel(Parcel in) {
            return new EmpData(in);
        }
        @Override
        public EmpData[] newArray(int size) {
            return new EmpData[size];
        }
    };
}
