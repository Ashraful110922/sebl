package adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.opl.one.oplsales.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model.TinyUser;


public class DrExecuteAdapter extends RecyclerView.Adapter<DrExecuteAdapter.DoctorView> {
    protected Context context;
    private List<TinyUser> list = new ArrayList<TinyUser>();
    private MyAdapterListener listener;

    public DrExecuteAdapter(Context context, List<TinyUser> list, MyAdapterListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    class DoctorView extends RecyclerView.ViewHolder {
        private TextView tvVDT,tvDCName,tvAdd,tvOpinion;
        private LinearLayout colorRow;
        private View mRodView;

        DoctorView(View itemView) {
            super(itemView);
            tvVDT = (TextView) itemView.findViewById(R.id.tvVDT);
            tvDCName = (TextView) itemView.findViewById(R.id.tvDCName);
            tvAdd = (TextView) itemView.findViewById(R.id.tvAdd);
            tvOpinion = (TextView) itemView.findViewById(R.id.tvOpinion);
            colorRow = (LinearLayout) itemView.findViewById(R.id.colorRow);
            mRodView = itemView;

            mRodView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.rowClick(v, getAdapterPosition());
                }
            });
        }

        private void showData(TinyUser str) {
            if (!TextUtils.isEmpty(str.getName()))
            tvDCName.setText(str.getName());

            if (!TextUtils.isEmpty(str.getVisitDateTime()))
                tvVDT.setText(convertDateTime(str.getVisitDateTime()));

            if (!TextUtils.isEmpty(str.getAddress()))
                tvAdd.setText(str.getAddress());

            if (!TextUtils.isEmpty(str.getRemarks()))
                tvOpinion.setText(str.getRemarks());

            if (str.getIsexecuted() == 1){
                colorRow.setBackgroundColor(context.getResources().getColor(R.color.et_bg));
            }else {
                colorRow.setBackgroundColor(context.getResources().getColor(R.color.icons));
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dr_execute, parent, false);
        return new DoctorView(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface MyAdapterListener {
        void rowClick(View v, int position);
    }


    private String convertDateTime(String dateTime){
        //SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        SimpleDateFormat output = new SimpleDateFormat("dd-MMM.’yy hh:mm a", Locale.getDefault());
        Date d = null;
        try {
            d = input.parse(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (d==null){
            return "Date: N/A";
        } else return output.format(d);
    }
}
