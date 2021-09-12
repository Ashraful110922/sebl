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


public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeView> {
    protected Context context;
    private List<TinyUser> list = new ArrayList<TinyUser>();
    private MyAdapterListener listener;
    private String goneType;

    public EmployeeAdapter(Context context, String goneType, List<TinyUser> list, MyAdapterListener listener) {
        this.context = context;
        this.goneType = goneType;
        this.list = list;
        this.listener = listener;
    }

    class EmployeeView extends RecyclerView.ViewHolder {
        private TextView empName,empCode,btnEditEmp;
        private View mRodView;

        EmployeeView(View itemView) {
            super(itemView);
            empName = (TextView) itemView.findViewById(R.id.empName);
            empCode = (TextView) itemView.findViewById(R.id.empCode);
            btnEditEmp = (TextView) itemView.findViewById(R.id.btnEditEmp);
            mRodView = itemView;

            mRodView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.rowClick(v, getAdapterPosition());
                }
            });
            btnEditEmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.editClick(v,getAdapterPosition());
                }
            });
        }

        private void showData(TinyUser str) {
            if (goneType.equals("gone edit"))
                btnEditEmp.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(str.getMIOName())){
                empName.setText(Html.fromHtml("<font color='#000000'><b> Name : </b></font>"+str.getMIOName()));
            }else {
                empName.setText(Html.fromHtml("<font color='#000000'><b> Name : </b></font>N/A"));
            }

            if (!TextUtils.isEmpty(str.getMIOCode())){
                empCode.setText(Html.fromHtml("<font color='#000000'><b> Code : </b></font>"+str.getMIOCode()));
            }else {
                empCode.setText(Html.fromHtml("<font color='#000000'><b> Code : </b></font>N/A"));
            }

            if (str.getIsexecuted() == 1){
                mRodView.setBackgroundResource(R.drawable.bg_b_border);
            }else {
                mRodView.setBackgroundResource(R.drawable.bg_b_border_select);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeView holder, int position) {
        TinyUser result = list.get(position);
        ((EmployeeView) holder).showData(result);
    }

    @NonNull
    @Override
    public EmployeeView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_emp, parent, false);
        return new EmployeeView(view);
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
