package adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.opl.one.oplsales.R;
import java.util.ArrayList;
import java.util.List;
import model.ParamMarket;
import model.TinyUser;


public class ChemistAdapter extends RecyclerView.Adapter<ChemistAdapter.MarketView> implements Filterable {
    protected Context context;
    private List<ParamMarket> list = new ArrayList<ParamMarket>();
    private List<ParamMarket> tempList;
    private MyAdapterListener listener;

    public ChemistAdapter(Context context, List<ParamMarket> list, MyAdapterListener listener) {
        this.context = context;
        this.list = list;
        tempList = new ArrayList<>(list);
        this.listener = listener;
    }

    @Override
    public Filter getFilter() {
        return marketFilter;
    }

    class MarketView extends RecyclerView.ViewHolder {
        private TextView mktName,mktAddress,mktCode,btnMktEdit;
        private View mRodView;

        MarketView(View itemView) {
            super(itemView);
            mktName = (TextView) itemView.findViewById(R.id.mktName);
            mktAddress = (TextView) itemView.findViewById(R.id.mktAddress);
            mktCode = (TextView) itemView.findViewById(R.id.mktCode);
            btnMktEdit = (TextView) itemView.findViewById(R.id.btnMktEdit);
            mRodView = itemView;

            mRodView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.rowClick(v, getAdapterPosition());
                }
            });
            btnMktEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.editClick(v,getAdapterPosition());
                }
            });
        }

        private void showData(ParamMarket str) {
            if (!TextUtils.isEmpty(str.getName())){
                mktName.setText(Html.fromHtml("<font color='#000000'><b> Name : </b></font>"+str.getName()));
            }else {
                mktName.setText(Html.fromHtml("<font color='#000000'><b> Name : </b></font>N/A"));
            }

            if (!TextUtils.isEmpty(str.getAddress())){
                mktAddress.setText(Html.fromHtml("<font color='#000000'><b> Address: </b></font>"+str.getAddress()));
            }else {
                mktAddress.setText(Html.fromHtml("<font color='#000000'><b> Address: </b></font>N/A"));
            }

            if (!TextUtils.isEmpty(str.getCode())){
                mktCode.setText(Html.fromHtml("<font color='#000000'><b> Code: </b></font>"+str.getCode()));
            }else {
                mktCode.setText(Html.fromHtml("<font color='#000000'><b> Code: </b></font>N/A"));
            }

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MarketView holder, int position) {
        ParamMarket result = list.get(position);
        ((MarketView) holder).showData(result);
    }

    @NonNull
    @Override
    public MarketView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_market, parent, false);
        return new MarketView(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface MyAdapterListener {
        void rowClick(View v, int position);
        void editClick(View v, int position);
    }


    private Filter marketFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ParamMarket> filteredList = new ArrayList<ParamMarket>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(tempList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ParamMarket item : tempList) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
