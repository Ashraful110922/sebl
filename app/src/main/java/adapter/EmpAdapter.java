package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.opl.one.oplsales.R;
import java.util.ArrayList;
import model.EmpData;


public class EmpAdapter extends RecyclerView.Adapter<EmpAdapter.ViewHolder> {
    private Context context;
    private ArrayList<EmpData> mData = new ArrayList<EmpData>();
    private LayoutInflater mInflater;
    private  MyAdapterListener listener;
    // data is passed into the constructor
    public EmpAdapter(Context context, ArrayList<EmpData> mData, MyAdapterListener listener) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_emp, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmpData item = mData.get(position);
        holder.showItem(item);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkEmp;
        private View mRodView;
        ViewHolder(View itemView) {
            super(itemView);
            checkEmp = (CheckBox) itemView.findViewById(R.id.checkEmp);

            mRodView =itemView;


            checkEmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.itemCheck(view,getAdapterPosition());
                }
            });
        }


        private void showItem(EmpData item){


        }
    }
    public interface MyAdapterListener {
        void itemCheck(View v, int position);
    }

}