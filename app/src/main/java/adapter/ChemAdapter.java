package adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.opl.one.oplsales.R;

import java.util.ArrayList;
import java.util.List;

import model.TinyUser;


public class ChemAdapter extends RecyclerView.Adapter<ChemAdapter.ChemView> {
    protected Context context;
    private List<TinyUser> list = new ArrayList<TinyUser>();
    private List<TinyUser> tempList;
    private MyAdapterListener listener;
    private String goneType;

    public ChemAdapter(Context context, String goneType, List<TinyUser> list, MyAdapterListener listener) {
        this.context = context;
        this.goneType= goneType;
        this.list = list;
        tempList = new ArrayList<>(list);
        this.listener = listener;

    }

   /* @Override
    public Filter getFilter() {
        return chemistFilter;
    }*/

    class ChemView extends RecyclerView.ViewHolder {
        private TextView tvChemName,tvChemAddress,tvChemMobile,btnChem;
        private View mRodView;

        ChemView(View itemView) {
            super(itemView);
            tvChemName = (TextView) itemView.findViewById(R.id.tvChemName);
            tvChemAddress = (TextView) itemView.findViewById(R.id.tvChemAddress);
            tvChemMobile = (TextView) itemView.findViewById(R.id.tvChemMobile);
            btnChem = (TextView) itemView.findViewById(R.id.btnChem);
            mRodView = itemView;

            mRodView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.rowClick(v, getAdapterPosition());
                }
            });

            btnChem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.rowEdit(v,getAdapterPosition());
                }
            });
        }

        private void showData(TinyUser str) {
            if (goneType.equals("gone edit"))
                btnChem.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(str.getChemistName())){
                tvChemName.setText(Html.fromHtml("<font color='#000000'><b> Name : </b></font>"+str.getChemistName()));
            }else {
                tvChemName.setText(Html.fromHtml("<font color='#000000'><b> Name : </b></font>N/A"));
            }

            if (!TextUtils.isEmpty(str.getAddress())){
                tvChemAddress.setText(Html.fromHtml("<font color='#000000'><b> Address : </b></font>"+str.getAddress()));
            }else {
                tvChemAddress.setText(Html.fromHtml("<font color='#000000'><b> Address : </b></font>N/A"));
            }
            if (!TextUtils.isEmpty(str.getChemistNo())){
                tvChemMobile.setText(Html.fromHtml("<font color='#000000'><b> No. : </b></font>"+str.getChemistNo()));
            }else {
                tvChemMobile.setText(Html.fromHtml("<font color='#000000'><b> No. : </b></font>N/A"));
            }


        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChemView holder, int position) {
        TinyUser result = list.get(position);
        ((ChemView) holder).showData(result);
    }

    @NonNull
    @Override
    public ChemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chem, parent, false);
        return new ChemView(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface MyAdapterListener {
        void rowClick(View v, int position);
        void rowEdit(View v, int position);
    }



    private Filter chemistFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<TinyUser> filteredList = new ArrayList<TinyUser>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(tempList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (TinyUser item : tempList) {
                    if (item.getChemistName().toLowerCase().contains(filterPattern)) {
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
