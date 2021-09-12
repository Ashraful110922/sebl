package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.opl.one.oplsales.R;
import java.util.List;
import model.TinyUser;

public class RosterAdapter extends ArrayAdapter<TinyUser> {

    private final Context context;
    private List<TinyUser> mList;

    public RosterAdapter(Context context, int resourceId, List<TinyUser> divisionList) {
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
        label.setText(mList.get(position).getName());

       /* if (position == 0) {//Special style for dropdown header
            label.setTextColor(context.getResources().getColor(R.color.primary_text));
        }*/

        return row;
    }

}
