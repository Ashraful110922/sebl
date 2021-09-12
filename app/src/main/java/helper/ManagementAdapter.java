package helper;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.opl.one.oplsales.MainActivity;
import com.opl.one.oplsales.R;
import java.util.List;

import fragment.DoctorMgt;
import fragment.DoctorReport;
import fragment.MarketMgt;
import fragment.ReportDoctor;
import fragment.ReportEmp;
import fragment.ReportMio;
import interfac.CommunicatorFragmentInterface;
import model.ItemType;
import utils.AppConstant;

public class ManagementAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<ItemType> dashList;
    private CommunicatorFragmentInterface myCommunicator;
    private DrawerLayout drawerLayout;

    public ManagementAdapter(Activity mActivity, List<ItemType> dashList, DrawerLayout drawerLayout) {
        this.dashList = dashList;
        this.mActivity = mActivity;
        this.drawerLayout = drawerLayout;

        try {
            myCommunicator = (CommunicatorFragmentInterface) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + "OnFragmentInteractionListener");
        }

    }

    @Override
    public int getCount() {
        return dashList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        convertView = inflater.inflate(R.layout.row_simple,parent,false);

        final TextView itemName = (TextView) convertView.findViewById(R.id.itemName);
        final ImageView itemTag = (ImageView) convertView.findViewById(R.id.itemTag);

        ItemType itemType=dashList.get(position);
        itemName.setText(itemType.getItemType());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(itemName.getText().toString().trim().equals("EMPLOYEE REPORT")){
                  myCommunicator.removeAllFragment();
                  drawerLayout.closeDrawer(GravityCompat.START);
                  ReportEmp emp = new ReportEmp();
                  Bundle empBundle= new Bundle();
                  empBundle.putString(AppConstant.TYPE_REPORT,"Emp");
                  emp.setArguments(empBundle);
                  myCommunicator.setContentFragment(emp,true);
              }else if(itemName.getText().toString().trim().equals("MIO REPORT")){
                  myCommunicator.removeAllFragment();
                  drawerLayout.closeDrawer(GravityCompat.START);
                  ReportMio mioReport = new ReportMio();
                  Bundle mioBundle= new Bundle();
                  mioBundle.putString(AppConstant.TYPE_REPORT,"Mio");
                  mioReport.setArguments(mioBundle);
                  myCommunicator.setContentFragment(mioReport,true);
              }else if(itemName.getText().toString().trim().equals("DOCTOR REPORT")){
                  myCommunicator.removeAllFragment();
                  drawerLayout.closeDrawer(GravityCompat.START);
                  ReportDoctor doctor = new ReportDoctor();
                  Bundle dBundle = new Bundle();
                  dBundle.putString(AppConstant.TYPE_DR_CHEMIST,"typeDr");
                  doctor.setArguments(dBundle);
                  myCommunicator.setContentFragment(doctor,true);

              }else if(itemName.getText().toString().trim().equals("CHEMIST REPORT")){
                  myCommunicator.removeAllFragment();
                  drawerLayout.closeDrawer(GravityCompat.START);
                  ReportDoctor chemist = new ReportDoctor();
                  Bundle cBundle = new Bundle();
                  cBundle.putString(AppConstant.TYPE_DR_CHEMIST,"typeChemist");
                  chemist.setArguments(cBundle);
                  myCommunicator.setContentFragment(chemist,true);
              }else if(itemName.getText().toString().trim().equals("DOCTOR LIST")){
                  myCommunicator.removeAllFragment();
                  drawerLayout.closeDrawer(GravityCompat.START);
                  myCommunicator.setContentFragment(new DoctorMgt(),true);
              }else if(itemName.getText().toString().trim().equals("CHEMIST LIST")){
                  myCommunicator.removeAllFragment();
                  drawerLayout.closeDrawer(GravityCompat.START);
                  myCommunicator.setContentFragment(new DoctorReport(),true);
              }else if(itemName.getText().toString().trim().equals("MARKET LIST")){
                  myCommunicator.removeAllFragment();
                  drawerLayout.closeDrawer(GravityCompat.START);
                  myCommunicator.setContentFragment(new MarketMgt(),true);
              }else if(itemName.getText().toString().trim().equals("CURRENT LOCATION")){
                  myCommunicator.removeAllFragment();
                  drawerLayout.closeDrawer(GravityCompat.START);
                  ReportMio mio = new ReportMio();
                  Bundle clBundle= new Bundle();
                  clBundle.putString(AppConstant.TYPE_REPORT,"CurrentLocation");
                  mio.setArguments(clBundle);
                  myCommunicator.setContentFragment(mio,true);
              }else if(itemName.getText().toString().trim().equals("ROAD MAP")){
                  myCommunicator.removeAllFragment();
                  drawerLayout.closeDrawer(GravityCompat.START);
                  ReportMio mioRoad = new ReportMio();
                  Bundle roadBundle= new Bundle();
                  roadBundle.putString(AppConstant.TYPE_REPORT,"RoadLocation");
                  mioRoad.setArguments(roadBundle);
                  myCommunicator.setContentFragment(mioRoad,true);
              }
            }
        });
        return convertView;
    }

}
