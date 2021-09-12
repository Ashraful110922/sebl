package adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.opl.one.oplsales.R;
import java.util.ArrayList;
import java.util.List;
import model.TinyUser;


public class DrAdapter extends RecyclerView.Adapter<DrAdapter.DoctorView> {
    protected Context context;
    private List<TinyUser> list = new ArrayList<TinyUser>();
    private MyAdapterListener listener;
    private String goneType;

    public DrAdapter(Context context, String goneType, List<TinyUser> list, MyAdapterListener listener) {
        this.context = context;
        this.goneType = goneType;
        this.list = list;
        this.listener = listener;
    }

    class DoctorView extends RecyclerView.ViewHolder {
        private TextView drName,drAddress,drId,btnEditDr;
        private View mRodView;

        DoctorView(View itemView) {
            super(itemView);
            drName = (TextView) itemView.findViewById(R.id.drName);
            drAddress = (TextView) itemView.findViewById(R.id.drAddress);
            drId = (TextView) itemView.findViewById(R.id.drId);
            btnEditDr = (TextView) itemView.findViewById(R.id.btnEditDr);
            mRodView = itemView;

            mRodView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.rowClick(v, getAdapterPosition());
                }
            });
            btnEditDr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.editClick(v,getAdapterPosition());
                }
            });
        }

        private void showData(TinyUser str) {
            if (goneType.equals("gone edit"))
                btnEditDr.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(str.getDoctorName())){
                drName.setText(Html.fromHtml("<font color='#000000'><b> Name : </b></font>"+str.getDoctorName()));
            }else {
                drName.setText(Html.fromHtml("<font color='#000000'><b> Name : </b></font>N/A"));
            }

            if (!TextUtils.isEmpty(str.getAddress())){
                drAddress.setText(Html.fromHtml("<font color='#000000'><b> Address : </b></font>"+str.getAddress()));
            }else {
                drAddress.setText(Html.fromHtml("<font color='#000000'><b> Address : </b></font>N/A"));
            }

            if (str.getDoctorID()>0){
                drId.setText(Html.fromHtml("<font color='#000000'><b> Id : </b></font>"+str.getDoctorID()));
            }else {
                drId.setText(Html.fromHtml("<font color='#000000'><b> Id : </b></font>N/A"));
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dr, parent, false);
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
}
