package adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ParseException;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.opl.one.oplsales.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import model.LatLon;
import utils.AppConstant;

public class AttendMothReport extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<LatLon> mData = new ArrayList<LatLon>();
    private LayoutInflater mInflater;
    private  MyAdapterListener listener;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private int working_days,presence,absence;
    private String monthName;

    public AttendMothReport(Context context, ArrayList<LatLon> mData, int working_days, int presence, int absence, String monthName, MyAdapterListener listener) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.listener = listener;
        this.working_days= working_days;
        this.presence = presence;
        this.absence= absence;
        this.monthName=monthName;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View vi = mInflater.inflate(R.layout.row_attend_month, parent, false);
            return new ViewHolder(vi);
        } else if (viewType == TYPE_HEADER) {
            View vh = mInflater.inflate(R.layout.report_head, parent, false);
            return new VHHeader(vh);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder){
            LatLon item = mData.get(position-1);
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
        private TextView tvMonthTitle,tvPrsDay,tvLvDay,tvAbsDay;
        VHHeader(View itemView) {
            super(itemView);
            tvMonthTitle = (TextView)itemView.findViewById(R.id.tvMonthTitle);
            tvPrsDay = (TextView)itemView.findViewById(R.id.tvPrsDay);
            tvLvDay = (TextView)itemView.findViewById(R.id.tvLvDay);
            tvAbsDay = (TextView)itemView.findViewById(R.id.tvAbsDay);
        }
        private void showSummary(){

            if(working_days == 1){
                tvMonthTitle.setText(Html.fromHtml("<big><b>"+monthName+" attendance report</b></big><br>("+ String.valueOf(working_days)+" working day)"));
            }else if(working_days>1) {
                tvMonthTitle.setText(Html.fromHtml("<big><b>"+monthName+" attendance report</b></big><br>("+ String.valueOf(working_days)+" working days)"));
            }else {
                tvMonthTitle.setText(Html.fromHtml("<big><b>"+monthName+" attendance report</b></big><br>(0 working day)"));
            }

            if(presence == 1){
                tvPrsDay.setText(Html.fromHtml("<big><font color='#4b89dc'><b>"+ String.valueOf(presence)+" </b></font></big> day present"));
            }else if(presence>1) {
                tvPrsDay.setText(Html.fromHtml("<big><font color='#4b89dc'><b>"+ String.valueOf(presence)+" </b></font></big> days present"));
            }else {
                tvPrsDay.setText(Html.fromHtml("<big><font color='#4b89dc'><b>0 </b></font></big> day present"));
            }

           /* if(leave == 1){
                tvLvDay.setText(Html.fromHtml("<big><font color='#6d6d6d'><b>"+ String.valueOf(leave)+" </b></font></big> day leave"));
            }else if(leave>1){
                tvLvDay.setText(Html.fromHtml("<big><font color='#6d6d6d'><b>"+ String.valueOf(leave)+" </b></font></big>days leave"));
            }else {
                tvLvDay.setText(Html.fromHtml("<big><font color='#6d6d6d'><b>0  </b></font></big>day leave"));
            }*/
            if(absence == 1){
                tvAbsDay.setText(Html.fromHtml("<big><font color='#fd5b4e'><b>"+ String.valueOf(absence)+" </b></font></big>day absent"));
            }else if(absence>1){
                tvAbsDay.setText(Html.fromHtml("<big><font color='#fd5b4e'><b>"+ String.valueOf(absence)+" </b></font></big>days absent"));
            }else {
                tvAbsDay.setText(Html.fromHtml("<big><font color='#fd5b4e'><b>0 </b></font></big>day absent"));
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout parentCinOut;
        private ImageView arrow;
        private TextView tvDate,tvCheckIn,tvCheckOut,tvStatus;
        private View mRodView;
        ViewHolder(View itemView) {
            super(itemView);
            parentCinOut = (LinearLayout) itemView.findViewById(R.id.parentCinOut);
            arrow =(ImageView) itemView.findViewById(R.id.arrowViw);
            tvDate =(TextView)itemView.findViewById(R.id.tvDate);
            tvCheckIn = (TextView) itemView.findViewById(R.id.tvCheckIn);
            tvCheckOut = (TextView) itemView.findViewById(R.id.tvCheckOut);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            mRodView =itemView;
            mRodView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.itemRowClick(view,getAdapterPosition());
                }
            });
        }

        private void showData(LatLon item){

            if(TextUtils.isEmpty(item.getCheck_in()) && TextUtils.isEmpty(item.getLeaves()) && !TextUtils.isEmpty(item.getStatus())){
                parentCinOut.setVisibility(View.INVISIBLE);
                parentCinOut.setVisibility(View.INVISIBLE);
                arrow.setVisibility(View.INVISIBLE);
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setText(Html.fromHtml("<b>"+item.getStatus()+"</b>"));
                tvStatus.setBackgroundColor(Color.GRAY);
                tvStatus.setTextColor(Color.WHITE);
            }else {
                parentCinOut.setVisibility(View.VISIBLE);
                arrow.setVisibility(View.VISIBLE);
                tvStatus.setVisibility(View.GONE);
                if(!TextUtils.isEmpty(item.getCheck_in())){
                    tvCheckIn.setText(Html.fromHtml( "<big>Check In</big><br><font color='#48D2A1'>"+ item.getCheck_in()+"</font>"));
                }else {
                    tvCheckIn.setVisibility(View.INVISIBLE);
                }

                if(!TextUtils.isEmpty(item.getCheck_out())){
                    tvCheckOut.setText(Html.fromHtml( "<big>Check Out</big><br><font color='#48D2A1'>"+ item.getCheck_out()+"</font>"));
                }else {
                    tvCheckOut.setVisibility(View.GONE);
                }
            }

            if (!TextUtils.isEmpty(item.getDayName()) && item.getDay()>0)
                tvDate.setText(Html.fromHtml(item.getDayName().substring(0, 3)+"<br><big><b>"+item.getDay()+"</big></b>"));
        }
    }
    public interface MyAdapterListener {
        void itemRowClick(View v, int position);
    }





}