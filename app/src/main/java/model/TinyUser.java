package model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class TinyUser implements Parcelable{
   /* private String user_email;*/
    @SerializedName("Id")
    private int id;
    private int doctorId;
    private int chemistID;
    private String MIOCode;
    private String MIOName;
    private int DoctorID;
    private String DoctorNo;
    private String DoctorName;
    private String ChemistNo;
    private String ChemistName;
    private String code;
    private String name;
    private String mobile;
    private String address;
    private String remarks;
    private String visitDateTime;
    private String visitedDateTime;
    private String lladdress;
    private String imageUrl;
    private int isexecuted;
    private int isScheduled;
    private String speciality;
    private String latitude;
    private String longitude;
    private String propritor;
    private String degree;
    private String designation;
    private String institude;
    private String noOfPatient;
    private float invoiceAmount;
    private float collectionAmount;
    private String marketCode;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getChemistID() {
        return chemistID;
    }

    public void setChemistID(int chemistID) {
        this.chemistID = chemistID;
    }

    public String getMIOCode() {
        return MIOCode;
    }

    public void setMIOCode(String MIOCode) {
        this.MIOCode = MIOCode;
    }

    public String getMIOName() {
        return MIOName;
    }

    public void setMIOName(String MIOName) {
        this.MIOName = MIOName;
    }

    public int getDoctorID() {
        return DoctorID;
    }

    public void setDoctorID(int doctorID) {
        DoctorID = doctorID;
    }

    public String getDoctorNo() {
        return DoctorNo;
    }

    public void setDoctorNo(String doctorNo) {
        DoctorNo = doctorNo;
    }

    public String getDoctorName() {
        return DoctorName;
    }

    public void setDoctorName(String doctorName) {
        DoctorName = doctorName;
    }

    public String getChemistNo() {
        return ChemistNo;
    }

    public void setChemistNo(String chemistNo) {
        ChemistNo = chemistNo;
    }

    public String getChemistName() {
        return ChemistName;
    }

    public void setChemistName(String chemistName) {
        ChemistName = chemistName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getVisitDateTime() {
        return visitDateTime;
    }

    public void setVisitDateTime(String visitDateTime) {
        this.visitDateTime = visitDateTime;
    }

    public String getVisitedDateTime() {
        return visitedDateTime;
    }

    public void setVisitedDateTime(String visitedDateTime) {
        this.visitedDateTime = visitedDateTime;
    }

    public String getLladdress() {
        return lladdress;
    }

    public void setLladdress(String lladdress) {
        this.lladdress = lladdress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getIsexecuted() {
        return isexecuted;
    }

    public void setIsexecuted(int isexecuted) {
        this.isexecuted = isexecuted;
    }

    public int getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(int isScheduled) {
        this.isScheduled = isScheduled;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPropritor() {
        return propritor;
    }

    public void setPropritor(String propritor) {
        this.propritor = propritor;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getInstitude() {
        return institude;
    }

    public void setInstitude(String institude) {
        this.institude = institude;
    }

    public String getNoOfPatient() {
        return noOfPatient;
    }

    public void setNoOfPatient(String noOfPatient) {
        this.noOfPatient = noOfPatient;
    }

    public float getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(float invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public float getCollectionAmount() {
        return collectionAmount;
    }

    public void setCollectionAmount(float collectionAmount) {
        this.collectionAmount = collectionAmount;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }



    public TinyUser(){}


   /* public TinyUser(String user_email, String name, String email, String photo) {
        this.user_email = user_email;
        this.name = name;
        this.email = email;
        this.photo = photo;
    }*/

    public TinyUser(int id, String name) {
        this.id = id;
        this.name = name;
    }



    public TinyUser(int id, int isvisited,String name,String address) {
        this.id = id;
        this.isexecuted = isvisited;
        this.name = name;
        this.address = address;
    }


    private TinyUser(Parcel in) {
        id = in.readInt();
        doctorId = in.readInt();
        chemistID = in.readInt();
        code = in.readString();
        name = in.readString();
        mobile = in.readString();
        address = in.readString();
        remarks = in.readString();
        visitDateTime = in.readString();
        visitedDateTime = in.readString();
        lladdress = in.readString();
        imageUrl = in.readString();
        speciality = in.readString();
        MIOCode = in.readString();
        DoctorID = in.readInt();
        DoctorNo = in.readString();
        DoctorName = in.readString();
        ChemistNo = in.readString();
        ChemistName = in.readString();
        isexecuted = in.readInt();
        isScheduled = in.readInt();
        latitude = in.readString();
        longitude = in.readString();
        propritor = in.readString();
        degree = in.readString();
        designation = in.readString();
        institude = in.readString();
        noOfPatient = in.readString();
        invoiceAmount = in.readFloat();
        collectionAmount = in.readFloat();
        marketCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(doctorId);
        dest.writeInt(chemistID);
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(mobile);
        dest.writeString(address);
        dest.writeString(remarks);
        dest.writeString(visitDateTime);
        dest.writeString(visitedDateTime);
        dest.writeString(lladdress);
        dest.writeString(imageUrl);
        dest.writeString(speciality);
        dest.writeString(MIOCode);
        dest.writeInt(DoctorID);
        dest.writeString(DoctorNo);
        dest.writeString(DoctorName);
        dest.writeString(ChemistNo);
        dest.writeString(ChemistName);
        dest.writeInt(isexecuted);
        dest.writeInt(isScheduled);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(propritor);
        dest.writeString(degree);
        dest.writeString(MIOName);
        dest.writeString(designation);
        dest.writeString(institude);
        dest.writeString(noOfPatient);
        dest.writeFloat(invoiceAmount);
        dest.writeFloat(collectionAmount);
        dest.writeString(marketCode);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TinyUser> CREATOR = new Creator<TinyUser>() {
        @Override
        public TinyUser createFromParcel(Parcel in) {
            return new TinyUser(in);
        }
        @Override
        public TinyUser[] newArray(int size) {
            return new TinyUser[size];
        }
    };
}
