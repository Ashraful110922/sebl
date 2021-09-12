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

import model.ParamChemist;

public class ChemListAdapter extends ArrayAdapter<ParamChemist> {

    private final Context context;
    private List<ParamChemist> mList;

    public ChemListAdapter(Context context, int resourceId, List<ParamChemist> divisionList) {
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

        if (!TextUtils.isEmpty(mList.get(position).getChemistName()))
           label.setText(Html.fromHtml("<font color='#000000'><b><small>"+mList.get(position).getChemistName()+"</small></b></font><br><small><font color='#0000FF'>"+mList.get(position).getChemistID() +"</font></small><hr>"));
        else
            label.setText(Html.fromHtml("<font color='#000000'><b><small>"+mList.get(position).getChemistName()+"</small></b></font>"));

        return row;
    }

}
