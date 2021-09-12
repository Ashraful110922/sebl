package adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import model.TinyUser;

public class MethodAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context context;
    private ArrayList<TinyUser> itemList = new ArrayList<TinyUser>();

    public MethodAdapter(Context context, ArrayList<TinyUser> itemList) {
        this.itemList=itemList;
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return itemList.size();

    }

    public Object getItem(int i)
    {
        return itemList.get(i);
    }
    public long getItemId(int i)
    {
        return (long)i;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setPadding(10, 10, 10, 10);
        txt.setTextSize(14);
        txt.setGravity(Gravity.LEFT);
        txt.setText(itemList.get(position).getName());
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        TextView txt = new TextView(context);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(10, 10, 10, 10);
        txt.setTextSize(14);
        txt.setText(itemList.get(i).getName());
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }

}
