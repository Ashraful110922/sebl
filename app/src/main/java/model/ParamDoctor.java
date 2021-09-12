package model;
import android.os.Parcel;
import android.os.Parcelable;

public class ParamDoctor implements Parcelable{
    private int DoctorID;
    private String DoctorName;

    public int getDoctorID() {
        return DoctorID;
    }

    public void setDoctorID(int doctorID) {
        DoctorID = doctorID;
    }

    public String getDoctorName() {
        return DoctorName;
    }

    public void setDoctorName(String doctorName) {
        DoctorName = doctorName;
    }

    public ParamDoctor(int DoctorID, String DoctorName){
        this.DoctorID = DoctorID;
        this.DoctorName=DoctorName;
    }

    public ParamDoctor(){}

    private ParamDoctor(Parcel in) {
        DoctorID = in.readInt();
        DoctorName = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(DoctorID);
        dest.writeString(DoctorName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParamDoctor> CREATOR = new Creator<ParamDoctor>() {
        @Override
        public ParamDoctor createFromParcel(Parcel in) {
            return new ParamDoctor(in);
        }
        @Override
        public ParamDoctor[] newArray(int size) {
            return new ParamDoctor[size];
        }
    };
}
