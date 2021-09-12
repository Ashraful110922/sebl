package adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.opl.one.oplsales.R;
import java.util.ArrayList;
import java.util.List;
import model.TinyUser;



public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorView> implements Filterable {
    protected Context context;
    private List<TinyUser> list = new ArrayList<TinyUser>();
    private List<TinyUser> tempList;
    private MyAdapterListener listener;
    private String goneType;

    public DoctorAdapter(Context context, String goneType,List<TinyUser> list, MyAdapterListener listener) {
        this.context = context;
        this.goneType = goneType;
        this.list = list;
        tempList = new ArrayList<>(list);
        this.listener = listener;
    }

    @Override
    public Filter getFilter() {
        return doctorFilter;
    }

    class DoctorView extends RecyclerView.ViewHolder {
        private TextView doctorName,doctorSpecialist,doctorType,btnEdit;
        private View mRodView;

        DoctorView(View itemView) {
            super(itemView);
            doctorName = (TextView) itemView.findViewById(R.id.doctorName);
            doctorSpecialist = (TextView) itemView.findViewById(R.id.doctorSpecialist);
            doctorType = (TextView) itemView.findViewById(R.id.doctorType);
            btnEdit = (TextView) itemView.findViewById(R.id.btnEdit);
            mRodView = itemView;

            mRodView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.rowClick(v, getAdapterPosition());
                }
            });
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.editClick(v,getAdapterPosition());
                }
            });
        }

        private void showData(TinyUser str) {
            if (goneType.equals("gone edit"))
                btnEdit.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(str.getName())){
                doctorName.setText(Html.fromHtml("<font color='#000000'><b> Name : </b></font>"+str.getName()));
            }else {
                doctorName.setText(Html.fromHtml("<font color='#000000'><b> Name : </b></font>N/A"));
            }

            if (!TextUtils.isEmpty(str.getSpeciality())){
                doctorSpecialist.setText(Html.fromHtml("<font color='#000000'><b> Specialist : </b></font>"+str.getSpeciality()));
            }else {
                doctorSpecialist.setText(Html.fromHtml("<font color='#000000'><b> Specialist : </b></font>N/A"));
            }

            if (!TextUtils.isEmpty(str.getAddress())){
                doctorType.setText(Html.fromHtml("<font color='#000000'><b> Address : </b></font>"+str.getAddress()));
            }else {
                doctorType.setText(Html.fromHtml("<font color='#000000'><b> Address : </b></font>N/A"));
            }

            if (str.getIsexecuted() == 1){
                mRodView.setBackgroundResource(R.drawable.bg_b_border);
            }else {
                mRodView.setBackgroundResource(R.drawable.bg_b_border_select);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorView holder, int position) {
        TinyUser result = list.get(position);
        ((DoctorView) holder).showData(result);
    }

    @NonNull
    @Override
    public DoctorView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_doctor, parent, false);
        return new DoctorView(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface MyAdapterListener {
        void rowClick(View v, int position);
        void editClick(View v, int position);
    }


    private Filter doctorFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<TinyUser> filteredList = new ArrayList<TinyUser>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(tempList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (TinyUser item : tempList) {
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
