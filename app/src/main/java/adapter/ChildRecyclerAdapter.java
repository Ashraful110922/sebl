package adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.opl.one.oplsales.R;
import java.util.List;
import model.Temp;

public class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.ViewHolder> {
     Context context;
     List<Temp> items;
     MyListener listener;

    public ChildRecyclerAdapter(Context context, List<Temp> items, MyListener listener) {
        this.context =context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Temp item = items.get(position);
        ((ViewHolder) holder).showItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
         TextView itemTextView,btnStockIncrease,btnStockDecrease,btnSaleIncrease,btnSaleDecrease;
         EditText etStockIncrease,etSaleDecrease;
         View mRodView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            btnStockIncrease = itemView.findViewById(R.id.btnStockIncrease);
            btnStockDecrease = itemView.findViewById(R.id.btnStockDecrease);
            btnSaleIncrease = itemView.findViewById(R.id.btnSaleIncrease);
            btnSaleDecrease = itemView.findViewById(R.id.btnSaleDecrease);
            etStockIncrease =itemView.findViewById(R.id.etStockIncrease);
            etSaleDecrease = itemView.findViewById(R.id.etSaleDecrease);
            mRodView =itemView;

            btnStockIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.stockIncrease(view, getAbsoluteAdapterPosition(),etStockIncrease,etStockIncrease.getText().toString().trim());
                }
            });

            btnStockDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.stockDecrease(view, getAbsoluteAdapterPosition(),etStockIncrease,etStockIncrease.getText().toString().trim());
                }
            });
            btnSaleIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.saleIncrease(view, getAbsoluteAdapterPosition(),etSaleDecrease,etSaleDecrease.getText().toString().trim());
                }
            });
            btnSaleDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.saleDecrease(view, getAbsoluteAdapterPosition(),etSaleDecrease,etSaleDecrease.getText().toString().trim());
                }
            });

            etStockIncrease.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try{
                        //items.get(getAbsoluteAdapterPosition()).setQty_in(Double.parseDouble(transferred_qty.getText().toString()));
                        listener.changeStock(getAbsoluteAdapterPosition(),charSequence.toString().trim());
                    }catch(NumberFormatException ex){
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            etSaleDecrease.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try{
                        //items.get(getAbsoluteAdapterPosition()).setQty_in(Double.parseDouble(transferred_qty.getText().toString()));
                        //Log.e("Check",""+charSequence);
                        listener.changeSales(getAbsoluteAdapterPosition(),charSequence.toString().trim());
                    }catch(NumberFormatException ex){
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }
        private void showItem(Temp item){
            itemTextView.setText(item.getName());

            etStockIncrease.setSingleLine(true);
            etStockIncrease.setHorizontallyScrolling(false);
            //etStockIncrease.setImeOptions(EditorInfo.IME_ACTION_DONE);

            etSaleDecrease.setSingleLine(true);
            etSaleDecrease.setHorizontallyScrolling(false);
            //etSaleDecrease.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }

    public interface MyListener {
        void stockIncrease(View v, int position,EditText setValue,String stockIncrease);
        void stockDecrease(View v, int position,EditText setValue,String stockDecrease);
        void saleIncrease(View v ,int position,EditText setValue,String saleIncrease);
        void saleDecrease(View v ,int position,EditText setValue,String saleDecrease);
        void changeStock(int position,String number);
        void changeSales(int position,String number);
    }
}
