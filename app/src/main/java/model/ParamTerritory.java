package model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class ParamTerritory implements Parcelable{
    private String TERRITORY_CODE;
    private String TerritoryName;
    private ArrayList<ParamMio> Mios = new ArrayList<ParamMio>();
    private ArrayList<ParamMarket> Markets = new ArrayList<>();

    public String getTERRITORY_CODE() {
        return TERRITORY_CODE;
    }

    public void setTERRITORY_CODE(String TERRITORY_CODE) {
        this.TERRITORY_CODE = TERRITORY_CODE;
    }

    public String getTerritoryName() {
        return TerritoryName;
    }

    public void setTerritoryName(String territoryName) {
        TerritoryName = territoryName;
    }

    public ArrayList<ParamMio> getMios() {
        return Mios;
    }

    public void setMios(ArrayList<ParamMio> mios) {
        Mios = mios;
    }

    public ArrayList<ParamMarket> getMarkets() {
        return Markets;
    }

    public void setMarkets(ArrayList<ParamMarket> markets) {
        Markets = markets;
    }

    public ParamTerritory(String TERRITORY_CODE, String TerritoryName){
        this.TERRITORY_CODE= TERRITORY_CODE;
        this.TerritoryName =TerritoryName;
    }


    public ParamTerritory(){

    }


    private ParamTerritory(Parcel in) {
        TERRITORY_CODE = in.readString();
        TerritoryName = in.readString();
        Mios = new ArrayList<ParamMio>();
        in.readList(Mios, (ClassLoader)ParamMio.CREATOR);
        Markets = new ArrayList<ParamMarket>();
        in.readList(Markets,(ClassLoader)ParamMio.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TERRITORY_CODE);
        dest.writeString(TerritoryName);
        dest.writeTypedList(Mios);
        dest.writeTypedList(Markets);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParamTerritory> CREATOR = new Creator<ParamTerritory>() {
        @Override
        public ParamTerritory createFromParcel(Parcel in) {
            return new ParamTerritory(in);
        }
        @Override
        public ParamTerritory[] newArray(int size) {
            return new ParamTerritory[size];
        }
    };
}
