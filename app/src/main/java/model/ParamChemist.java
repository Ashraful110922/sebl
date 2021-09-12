package model;
import android.os.Parcel;
import android.os.Parcelable;

public class ParamChemist implements Parcelable{
    private int ChemistID;
    private String ChemistName;

    public int getChemistID() {
        return ChemistID;
    }

    public void setChemistID(int chemistID) {
        ChemistID = chemistID;
    }

    public String getChemistName() {
        return ChemistName;
    }

    public void setChemistName(String chemistName) {
        ChemistName = chemistName;
    }

    public ParamChemist(int ChemistID, String ChemistName){
        this.ChemistID = ChemistID;
        this.ChemistName=ChemistName;
    }

    public ParamChemist(){}

    private ParamChemist(Parcel in) {
        ChemistID = in.readInt();
        ChemistName = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ChemistID);
        dest.writeString(ChemistName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParamChemist> CREATOR = new Creator<ParamChemist>() {
        @Override
        public ParamChemist createFromParcel(Parcel in) {
            return new ParamChemist(in);
        }
        @Override
        public ParamChemist[] newArray(int size) {
            return new ParamChemist[size];
        }
    };
}
