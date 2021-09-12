package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.opl.one.oplsales.R;
import java.util.ArrayList;
import java.util.List;
import model.Brand;
import model.Temp;


public class BrandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Brand> mData = new ArrayList<Brand>();
    private LayoutInflater mInflater;
    private  MyAdapterListener listener;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public BrandAdapter(Context context, List<Brand> mData, MyAdapterListener listener) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.listener = listener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View vi = mInflater.inflate(R.layout.row_medicine, parent, false);
            return new ViewHolder(vi);
        } else if (viewType == TYPE_HEADER) {
            View vh = mInflater.inflate(R.layout.row_head, parent, false);
            return new VHHeader(vh);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder){
            Brand item = mData.get(position-1);
            ((ViewHolder) holder).showItem(item,position-1);
        }else if(holder instanceof VHHeader){

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
        VHHeader(View itemView) {
            super(itemView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtBrand;
        private RecyclerView childRecyclerView;

        private View mRodView;
        ViewHolder(View itemView) {
            super(itemView);
            txtBrand =(TextView)itemView.findViewById(R.id.txtBrand);
            childRecyclerView = (RecyclerView) itemView.findViewById(R.id.childRecyclerView);
            mRodView =itemView;
        }

        private void showItem(Brand item,int position){
            txtBrand.setText(item.getBrName());
            List<Temp> items = item.getSections();
            ChildRecyclerAdapter childRecyclerAdapter = new ChildRecyclerAdapter(context, items, new ChildRecyclerAdapter.MyListener() {
                @Override
                public void stockIncrease(View v, int position,EditText setValue, String increaseItem) {
                    if (Integer.parseInt(increaseItem)>=0){
                        int value = Integer.parseInt(increaseItem);
                        //Log.e("value",""+value);
                        setValue.setText(String.valueOf(++value));
                    }
                }

                @Override
                public void stockDecrease(View v, int position ,EditText setValue,String decreaseItem) {
                    if (Integer.parseInt(decreaseItem)>0){
                        int value = Integer.parseInt(decreaseItem);
                        //Log.e("value",""+value);
                        setValue.setText(String.valueOf(--value));
                    }
                }

                @Override
                public void saleIncrease(View v, int position, EditText setValue, String saleIncrease) {
                    if (Integer.parseInt(saleIncrease)>=0){
                        int value = Integer.parseInt(saleIncrease);
                        setValue.setText(String.valueOf(++value));
                    }
                }

                @Override
                public void saleDecrease(View v, int position, EditText setValue, String saleDecrease) {
                    if (Integer.parseInt(saleDecrease)>0){
                        int value = Integer.parseInt(saleDecrease);
                        setValue.setText(String.valueOf(--value));
                    }
                }

                @Override
                public void changeStock(int position, String number) {
                    JsonObject object =new JsonObject();
                    object.addProperty("position",position);
                    object.addProperty("adapterPosition",getAbsoluteAdapterPosition());
                    object.addProperty("stock",number);
                    listener.OnChangeValue(object);
                }

                @Override
                public void changeSales(int position, String number) {
                    JsonObject object =new JsonObject();
                    object.addProperty("position",position);
                    object.addProperty("adapterPosition",getAbsoluteAdapterPosition());
                    object.addProperty("sale",number);
                    listener.OnChangeValue(object);
                }
            });
           childRecyclerView.setAdapter(childRecyclerAdapter);
           childRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        }
    }
    public interface MyAdapterListener {
        void OnChangeValue(JsonObject reportType);
    }

}