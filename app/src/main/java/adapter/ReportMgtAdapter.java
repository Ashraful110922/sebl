package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.opl.one.oplsales.R;
import java.util.ArrayList;
import model.TinyUser;

import static android.content.Context.MODE_PRIVATE;

public class ReportMgtAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<TinyUser> mData = new ArrayList<TinyUser>();
    private LayoutInflater mInflater;
    private MyAdapterListener listener;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String type;


    public ReportMgtAdapter(Context context,String type, ArrayList<TinyUser> mData, MyAdapterListener listener) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.listener = listener;
        this.type =type;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View vi = mInflater.inflate(R.layout.row_report_mgt, parent, false);
            return new ViewHolder(vi);
        } else if (viewType == TYPE_HEADER) {
            View vh = mInflater.inflate(R.layout.row_header, parent, false);
            return new VHHeader(vh);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder){
            pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
            editor = pref.edit();
            TinyUser item = mData.get(position-1);
            ((ViewHolder) holder).showData(item);
         }else if(holder instanceof VHHeader){
            ((VHHeader) holder).showSummary();

        }
    }

    @Override
    public int getItemCount() {
        return mData.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

            return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private class VHHeader extends RecyclerView.ViewHolder{
        private TextView titleText;
        VHHeader(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
        }
        private void showSummary(){
            if (type.equals("typeDoctorReport")){
                titleText.setText(context.getResources().getString(R.string.mio_wise_dr));
            }else if(type.equals("typeChemistReport")){
                titleText.setText(context.getResources().getString(R.string.mio_wise_chem));
            }else if(type.equals("typeDr")){
                titleText.setText(context.getResources().getString(R.string.dr_visit));
            }else if (type.equals("typeChemist")){
                titleText.setText(context.getResources().getString(R.string.chem_visit));
            }else if(type.equals("typeEmployeeReport")){
                titleText.setText(context.getResources().getString(R.string.emp_visit));
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRDrName,tvRDrDegree,tvRDrAddress,llAddress,tvVisitTime,tvVisitedTime,tvOValue,tvCValue,tvRemarks;
        private LinearLayout rDrComInfo,rDrCheInfo;
        private View mRodView;
        ViewHolder(View itemView) {
            super(itemView);
            tvRDrName = (TextView) itemView.findViewById(R.id.tvRDrName);
            tvRDrDegree = (TextView) itemView.findViewById(R.id.tvRDrDegree);
            tvRDrAddress = (TextView) itemView.findViewById(R.id.tvRDrAddress);
            llAddress = (TextView) itemView.findViewById(R.id.llAddress);
            tvVisitTime = (TextView) itemView.findViewById(R.id.tvVisitTime);
            tvVisitedTime = (TextView) itemView.findViewById(R.id.tvVisitedTime);
            tvRemarks = (TextView) itemView.findViewById(R.id.tvRemarks);
            tvOValue = (TextView) itemView.findViewById(R.id.tvOValue);
            tvCValue = (TextView) itemView.findViewById(R.id.tvCValue);
            rDrComInfo = (LinearLayout) itemView.findViewById(R.id.rDrComInfo);
            rDrCheInfo = (LinearLayout) itemView.findViewById(R.id.rDrCheInfo);
            mRodView =itemView;
            mRodView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.rowClick(view,getAdapterPosition());
                }
            });
        }

        private void showData(TinyUser item){
            if (type.equals("typeDoctorReport")){
                rDrCheInfo.setVisibility(View.GONE);
                rDrComInfo.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(item.getName())){
                    tvRDrName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>"+item.getName()+"</font>"));
                }else {
                    tvRDrName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getDegree())){
                    tvRDrDegree.setText(Html.fromHtml("<font color='#808080'><b>Degree: </b></font><font color='#808080'>"+item.getDegree()+"</font>"));
                }else {
                    tvRDrDegree.setText(Html.fromHtml("<font color='#808080'><b>Degree: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getAddress())){
                    tvRDrAddress.setText(Html.fromHtml("<font color='#808080'><b>Address: </b></font><font color='#808080'>"+item.getAddress()+"</font>"));
                }else {
                    tvRDrAddress.setText(Html.fromHtml("<font color='#808080'><b>Address: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getLladdress())){
                    llAddress.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'>"+item.getLladdress()+"</font>"));
                }else {
                    llAddress.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getVisitDateTime())){
                    tvVisitTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>"+item.getVisitDateTime()+"</font>"));
                }else {
                    tvVisitTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getVisitedDateTime())){
                    tvVisitedTime.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>"+item.getVisitedDateTime()+"</font>"));
                }else {
                    tvVisitedTime.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getRemarks())){
                    tvRemarks.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>"+item.getRemarks()+"</font>"));
                }else {
                    tvRemarks.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>N/A</font>"));
                }

            }else if(type.equals("typeDr")){
                rDrCheInfo.setVisibility(View.GONE);
                rDrComInfo.setVisibility(View.VISIBLE);
                tvRDrAddress.setVisibility(View.GONE);
                tvRDrDegree.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(item.getName()))
                    tvRDrName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>"+item.getName()+"</font>"));
                if (!TextUtils.isEmpty(item.getVisitDateTime()))
                    tvVisitTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>"+item.getVisitDateTime()+"</font>"));
                if (!TextUtils.isEmpty(item.getVisitedDateTime()))
                    tvVisitedTime.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>"+item.getVisitedDateTime()+"</font>"));
                if (!TextUtils.isEmpty(item.getLladdress()))
                    llAddress.setText(Html.fromHtml("<font color='#808080'>"+item.getLladdress()+"</font>"));
                if (!TextUtils.isEmpty(item.getRemarks()))
                    tvRemarks.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>"+item.getRemarks()+"</font>"));

            }else if(type.equals("typeChemist")){
                tvRDrDegree.setVisibility(View.GONE);
                //rDrCheInfo.setVisibility(View.VISIBLE);
                //rDrComInfo.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(item.getName())){
                    tvRDrName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>"+item.getName()+"</font>"));
                }else {
                    tvRDrName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(item.getAddress())){
                    tvRDrAddress.setText(Html.fromHtml("<font color='#808080'><b>Address: </b></font><font color='#808080'>"+item.getAddress()+"</font>"));
                }else {
                    tvRDrAddress.setText(Html.fromHtml("<font color='#808080'><b>Address: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(item.getLladdress())){
                    llAddress.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'>"+item.getLladdress()+"</font>"));
                }else {
                    llAddress.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(item.getVisitDateTime())){
                    tvVisitTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>"+item.getVisitDateTime()+"</font>"));
                }else {
                    tvVisitTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getVisitedDateTime())){
                    tvVisitedTime.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>"+item.getVisitedDateTime()+"</font>"));
                }else {
                    tvVisitedTime.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getRemarks())){
                    tvRemarks.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>"+item.getRemarks()+"</font>"));
                }else {
                    tvRemarks.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>N/A</font>"));
                }

                if (item.getInvoiceAmount()>0.0f){
                    tvOValue.setText(Html.fromHtml("<font color='#000000'><b>Order: </b></font><font color='#808080'>"+item.getInvoiceAmount()+"</font>"));
                }else {
                    tvOValue.setText(Html.fromHtml("<font color='#000000'><b>Order: </b></font><font color='#808080'>"+"N/A"+"</font>"));
                }

                if (item.getCollectionAmount()>0.0f){
                    tvCValue.setText(Html.fromHtml("<font color='#000000'><b>Collection: </b></font><font color='#808080'>"+item.getCollectionAmount()+"</font>"));
                }else {
                    tvCValue.setText(Html.fromHtml("<font color='#000000'><b>Collection: </b></font><font color='#808080'>"+"N/A"+"</font>"));
                }

            }else if(type.equals("typeChemistReport")){
                tvRDrDegree.setVisibility(View.GONE);
                rDrCheInfo.setVisibility(View.VISIBLE);

                if (!TextUtils.isEmpty(item.getName())){
                    tvRDrName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>"+item.getName()+"</font>"));
                }else {
                    tvRDrName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(item.getAddress())){
                    tvRDrAddress.setText(Html.fromHtml("<font color='#808080'><b>Address: </b></font><font color='#808080'>"+item.getAddress()+"</font>"));
                }else {
                    tvRDrAddress.setText(Html.fromHtml("<font color='#808080'><b>Address: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(item.getLladdress())){
                    llAddress.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'>"+item.getLladdress()+"</font>"));
                }else {
                    llAddress.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(item.getVisitDateTime())){
                    tvVisitTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>"+item.getVisitDateTime()+"</font>"));
                }else {
                    tvVisitTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getVisitedDateTime())){
                    tvVisitedTime.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>"+item.getVisitedDateTime()+"</font>"));
                }else {
                    tvVisitedTime.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getRemarks())){
                    tvRemarks.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>"+item.getRemarks()+"</font>"));
                }else {
                    tvRemarks.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>N/A</font>"));
                }

                if (item.getInvoiceAmount()>0.0f){
                    tvOValue.setText(Html.fromHtml("<font color='#000000'><b>Order: </b></font><font color='#808080'>"+item.getInvoiceAmount()+"</font>"));
                }else {
                    tvOValue.setText(Html.fromHtml("<font color='#000000'><b>Order: </b></font><font color='#808080'>"+"N/A"+"</font>"));
                }

                if (item.getCollectionAmount()>0.0f){
                    tvCValue.setText(Html.fromHtml("<font color='#000000'><b>Collection: </b></font><font color='#808080'>"+item.getCollectionAmount()+"</font>"));
                }else {
                    tvCValue.setText(Html.fromHtml("<font color='#000000'><b>Collection: </b></font><font color='#808080'>"+"N/A"+"</font>"));
                }
            }else if(type.equals("typeEmployeeReport")){
                rDrCheInfo.setVisibility(View.GONE);
                rDrComInfo.setVisibility(View.VISIBLE);
                tvRDrAddress.setVisibility(View.GONE);
                tvRDrDegree.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(item.getName())){
                    tvRDrName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>"+item.getName()+"</font>"));
                }else {
                    tvRDrName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getLladdress())){
                    llAddress.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'>"+item.getLladdress()+"</font>"));
                }else {
                    llAddress.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getVisitDateTime())){
                    tvVisitTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>"+item.getVisitDateTime()+"</font>"));
                }else {
                    tvVisitTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getVisitedDateTime())){
                    tvVisitedTime.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>"+item.getVisitedDateTime()+"</font>"));
                }else {
                    tvVisitedTime.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(item.getRemarks())){
                    tvRemarks.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>"+item.getRemarks()+"</font>"));
                }else {
                    tvRemarks.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>N/A</font>"));
                }
            }

        }
    }
    public interface MyAdapterListener {
        void rowClick(View v, int position);

    }




}