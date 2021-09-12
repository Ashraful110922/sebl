package adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.opl.one.oplsales.R;
import java.util.List;
import model.ParamZone;


public class ZoneAdapter extends ArrayAdapter<ParamZone> {

    private final Context context;
    private List<ParamZone> mList;

    public ZoneAdapter(Context context, int resourceId, List<ParamZone> divisionList) {
        super(context, resourceId, divisionList);
        this.mList=divisionList;
        this.context = context;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(  Context.LAYOUT_INFLATER_SERVICE );
        View row=inflater.inflate(R.layout.spinner_item, parent, false);
        TextView label=(TextView)row.findViewById(R.id.text1);
        if (!TextUtils.isEmpty(mList.get(position).getZoneName()) && position>0)
            label.setText(Html.fromHtml("<font color='#000000'><b><small>"+mList.get(position).getZoneName()+"</small></b></font><br><small><font color='#0000FF'>"+mList.get(position).getZONE_CODE() +"</font></small>"));
        else
            label.setText(Html.fromHtml("<font color='#000000'>"+mList.get(position).getZoneName()+"</font>"));
       /* if (position ==0){
            if (!TextUtils.isEmpty(mList.get(position).getZoneName())) {
                label.setText(Html.fromHtml("<font color='#000000'>"+mList.get(position).getZoneName()+"</font>"));
            }
        }else {
            if (!TextUtils.isEmpty(mList.get(position).getZoneName())) {
                label.setText(Html.fromHtml("<font color='#000000'>Zone: "+mList.get(position).getZoneName()+"</font>"));
            }
        }*/



       /* if (!TextUtils.isEmpty(mList.get(position).getName()) && TextUtils.isEmpty(mList.get(position).getAddress()) && TextUtils.isEmpty(mList.get(position).getMobile())) {
            label.setText(Html.fromHtml("<font color='#000000'>"+mList.get(position).getName()+"</font>"));
        }else if(!TextUtils.isEmpty(mList.get(position).getName()) && !TextUtils.isEmpty(mList.get(position).getAddress()) && TextUtils.isEmpty(mList.get(position).getMobile())){
            label.setText(Html.fromHtml("<font color='#000000'><b>"+mList.get(position).getName()+"</b></font><br><small>"+mList.get(position).getAddress()+"</small>"));
        }else if(!TextUtils.isEmpty(mList.get(position).getName()) && TextUtils.isEmpty(mList.get(position).getAddress()) && !TextUtils.isEmpty(mList.get(position).getMobile())){
            label.setText(Html.fromHtml("<font color='#000000'><b><small>"+mList.get(position).getName()+"</small></b></font><br><small><font color='#0000FF'>"+mList.get(position).getMobile()+"</font></small>"));
        } else {
            label.setText(Html.fromHtml("<font color='#000000'><b><small>"+mList.get(position).getName()+"</small></b></font><br><small>"+mList.get(position).getAddress() +"<br><font color='#0000FF'>"+mList.get(position).getMobile()+"</font></small>"));
        }
        if (mList.get(position).getIsScheduled() ==1 ) {
            row.setBackgroundColor(context.getResources().getColor(R.color.et_bg));
        }else {
            row.setBackgroundColor(context.getResources().getColor(R.color.icons));
        }*/
        return row;
    }

}
