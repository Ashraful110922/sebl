package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.opl.one.oplsales.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import model.LatLon;

import static android.content.Context.MODE_PRIVATE;

public class AttendViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<LatLon> mData = new ArrayList<LatLon>();
    private LayoutInflater mInflater;
    private MyAdapterListener listener;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private int working_days,present_days;
    private String flag;
    private SharedPreferences pref;

    public AttendViewAdapter(Context context, ArrayList<LatLon> mData, int working_days, int present_days, String flag, MyAdapterListener listener) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.working_days= working_days;
        this.present_days =present_days;
        this.flag =flag;
        this.listener = listener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View vi = mInflater.inflate(R.layout.atten_row, parent, false);
            return new ViewHolder(vi);
        } else if (viewType == TYPE_HEADER) {
            View vh = mInflater.inflate(R.layout.atten_summery, parent, false);
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
            pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
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
        private TextView tAttendance,tAttendNumber,currentTime,checkInfo,checkTime,checkBtn;
        private TextInputEditText etRemarks;
        VHHeader(View itemView) {
            super(itemView);
            tAttendance = (TextView)itemView.findViewById(R.id.tAttendance);
            tAttendNumber = (TextView)itemView.findViewById(R.id.tAttendNumber);
            currentTime = (TextView)itemView.findViewById(R.id.currentTime);
            checkInfo = (TextView)itemView.findViewById(R.id.checkInfo);
            checkTime = (TextView)itemView.findViewById(R.id.checkTime);
            checkBtn = (TextView) itemView.findViewById(R.id.checkBtn);
            etRemarks = (TextInputEditText) itemView.findViewById(R.id.etRemarks);

            checkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.checkBtn(v,getAdapterPosition());
                }
            });

            etRemarks.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                   listener.onTextChanged(getAdapterPosition(),s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            });

        }
        private void showSummary(){
            if (working_days>0){
                tAttendance.setText(Html.fromHtml("<big><font color='#010101'> Total Attendance</font></big><br>"+working_days+"/"+present_days+" Office Attended" ));
            }else {
                tAttendance.setText(Html.fromHtml("<big><font color='#010101'> Total Attendance</font></big><br> 0/0 Office Attended" ));
            }
           if(present_days == 1){
                tAttendNumber.setText(Html.fromHtml("<big><font color='#639be7'>"+ present_days+" </font></big>(Day)" ));
            }else  if (present_days>1){
               tAttendNumber.setText(Html.fromHtml("<big><font color='#639be7'>"+ present_days+" </font></big>(Days)" ));
           }else {
               tAttendNumber.setText(Html.fromHtml("<big><font color='#639be7'> 0 </font></big>(Day)" ));
           }
            //SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            //SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
            if(!TextUtils.isEmpty(flag) && Integer.parseInt(flag) == 2){
                checkInfo.setText(Html.fromHtml("Please click the <font color='#639be7'>CHECK IN</font> button for today attendance"));
                checkBtn.setText(context.getResources().getString(R.string.check_in));
               /* try {
                    checkTime.setText(Html.fromHtml("(In: "+date12Format.format(Objects.requireNonNull(date24Format.parse(cin_time)))+")"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
            }else if(!TextUtils.isEmpty(flag) && Integer.parseInt(flag) == 1){
                checkInfo.setText(Html.fromHtml("Please click the <font color='#639be7'>CHECK OUT</font> button for today attendance"));
                checkBtn.setText(context.getResources().getString(R.string.check_out));
               /* try {
                    checkTime.setText(Html.fromHtml("(Out: "+date12Format.format(Objects.requireNonNull(date24Format.parse(cout_time)))+")"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
            }else if(!TextUtils.isEmpty(flag) && Integer.parseInt(flag) == 0){
                checkInfo.setText(Html.fromHtml("You <font color='#639be7'>CHECKED IN && CHECKED OUT</font> today, again  'check in' on the same day"));
                checkBtn.setText(context.getResources().getString(R.string.check_in));
               /* try {
                    checkTime.setText(Html.fromHtml("(In: "+date12Format.format(Objects.requireNonNull(date24Format.parse(cin_time)))+",out: "+date12Format.format(Objects.requireNonNull(date24Format.parse(cout_time)))+")"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView monthName,monthSummary;
        private View mRodView;
        ViewHolder(View itemView) {
            super(itemView);
            monthName = (TextView) itemView.findViewById(R.id.monthName);
            monthSummary = (TextView) itemView.findViewById(R.id.monthSummary);
            mRodView =itemView;
            mRodView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.rowClick(view,getAdapterPosition());
                }
            });

        }

        private void showData(LatLon item){
            if (!TextUtils.isEmpty(item.getMonthName()))
                monthName.setText(Html.fromHtml("Attendance of "+item.getMonthName()));
            else
                monthName.setText(Html.fromHtml("Attendance of N/A"));

            if(item.getAttended() == 1){
                monthSummary.setText(Html.fromHtml(String.valueOf(item.getAttended())+" (Day)"));
            }else if(item.getAttended()>0){
                monthSummary.setText(Html.fromHtml(String.valueOf(item.getAttended())+" (Days)"));
            }else {
                monthSummary.setText(Html.fromHtml("0 (Day)"));
            }
        }
    }
    public interface MyAdapterListener {
        void checkBtn(View v, int position);
        void rowClick(View v, int position);
        void onTextChanged(int position, String charSeq);
    }

  /*  private String onTimeSet(int hour, int minute) {
        Calendar mCalen = Calendar.getInstance();;
        mCalen.set(Calendar.HOUR_OF_DAY, hour);
        mCalen.set(Calendar.MINUTE, minute);

        int hour12format_local = mCalen.get(Calendar.HOUR);
        int minute_local = mCalen.get(Calendar.MINUTE);
        int ampm = mCalen.get(Calendar.AM_PM);
        String minute1;
        if(minute_local<10){

            minute1="0"+minute_local;
        }
        else
            minute1=""+minute_local;
        String ampmStr = (ampm == 0) ? "AM" : "PM";

        if(hour12format_local==0)
            hour12format_local=12;

        return hour12format_local+":"+ minute1+" "+ampmStr;
    }*/


}