package dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.opl.one.oplsales.R;
import com.squareup.picasso.Picasso;

import helper.RoundedTransformation;
import model.TinyUser;
import utils.AppConstant;

public class ReportDetails extends DialogFragment {
    private Context context;
    private Bundle bundle;
    private View rootView;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private LinearLayout llPlanDate,llValueView;
    private TextView btnOkDetails,tvRName,tvAddInput,tvExeAdd,tvPlanTime,tvExuDate,tvOrdVal,tvCltVal,tvDegree,tvOpin;
    private ImageView ivTakePic;
    private TinyUser user=new TinyUser();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.report_details, container, false);
        context = getActivity();
        pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();
        bundle = this.getArguments();
        initUi();
        return rootView;
    }

    private void initUi() {
        btnOkDetails =(TextView) rootView.findViewById(R.id.btnOkDetails);
        ivTakePic = (ImageView) rootView.findViewById(R.id.ivTakePic);
        tvRName = (TextView) rootView.findViewById(R.id.tvRName);
        tvAddInput = (TextView) rootView.findViewById(R.id.tvAddInput);
        tvOpin = (TextView) rootView.findViewById(R.id.tvOpin);
        tvExeAdd = (TextView) rootView.findViewById(R.id.tvExeAdd);
        tvPlanTime = (TextView) rootView.findViewById(R.id.tvPlanTime);
        tvExuDate = (TextView) rootView.findViewById(R.id.tvExuDate);
        tvOrdVal = (TextView) rootView.findViewById(R.id.tvOrdVal);
        tvCltVal = (TextView) rootView.findViewById(R.id.tvCltVal);
        llPlanDate = (LinearLayout) rootView.findViewById(R.id.llPlanDate);
        llValueView = (LinearLayout) rootView.findViewById(R.id.llValueView);
        tvDegree = (TextView) rootView.findViewById(R.id.tvDegree);

        if (bundle!=null){
            user = bundle.getParcelable("OBJECT");
            String type = bundle.getString(AppConstant.TYPE_DR_CHEMIST);
            if (type.equals("typeChemistReport")){
                tvDegree.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(user.getImageUrl())){
                    Picasso.get()
                            .load(AppConstant.BASE_URL+"visitedImage/"+user.getImageUrl())
                            .placeholder(R.drawable.opl_logo)
                            .error(R.drawable.opl_logo)
                            .transform(new RoundedTransformation(0, 0))
                            .into(ivTakePic);
                }

                if (!TextUtils.isEmpty(user.getName())){
                    tvRName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>"+user.getName()+"</font>"));
                }else {
                    tvRName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(user.getAddress())){
                    tvAddInput.setText(Html.fromHtml("<font color='#808080'><b>Address: </b></font><font color='#808080'><small>"+user.getAddress()+"</small></font>"));
                }else {
                    tvAddInput.setText(Html.fromHtml("<font color='#808080'><b>Address: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(user.getLladdress())){
                    tvExeAdd.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'><small>"+user.getLladdress()+"</small></font>"));
                }else {
                    tvExeAdd.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(user.getVisitDateTime())){
                    tvPlanTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'><small>"+user.getVisitDateTime()+"</small></font>"));
                }else {
                    tvPlanTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(user.getVisitedDateTime())){
                    tvExuDate.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'><small>"+user.getVisitedDateTime()+"</small></font>"));
                }else {
                    tvExuDate.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(user.getRemarks())){
                    tvOpin.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>"+user.getRemarks()+"</font>"));
                }else {
                    tvOpin.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>N/A</font>"));
                }
                if (user.getInvoiceAmount()>0.0f){
                    tvOrdVal.setText(Html.fromHtml("<font color='#000000'><b>Order: </b></font><font color='#808080'><small>"+user.getInvoiceAmount()+"</small></font>"));
                }else {
                    tvOrdVal.setText(Html.fromHtml("<font color='#000000'><b>Order: </b></font><font color='#808080'>"+"N/A"+"</font>"));
                }

                if (user.getCollectionAmount()>0.0f){
                    tvCltVal.setText(Html.fromHtml("<font color='#000000'><b>Collection: </b></font><font color='#808080'><small>"+user.getCollectionAmount()+"</small></font>"));
                }else {
                    tvCltVal.setText(Html.fromHtml("<font color='#000000'><b>Collection: </b></font><font color='#808080'>"+"N/A"+"</font>"));
                }

            }else if(type.equals("typeDoctorReport")){
                llValueView.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(user.getImageUrl())){
                    Picasso.get()
                            .load(AppConstant.BASE_URL+"visitedImage/"+user.getImageUrl())
                            .placeholder(R.drawable.opl_logo)
                            .error(R.drawable.opl_logo)
                            .transform(new RoundedTransformation(0, 0))
                            .into(ivTakePic);
                }

                if (!TextUtils.isEmpty(user.getName())){
                    tvRName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>"+user.getName()+"</font>"));
                }else {
                    tvRName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(user.getDegree())){
                    tvDegree.setText(Html.fromHtml("<font color='#808080'><b>Degree: </b></font><font color='#808080'>"+user.getDegree()+"</font>"));
                }else {
                    tvDegree.setText(Html.fromHtml("<font color='#808080'><b>Degree: </b></font><font color='#808080'>N/A</font>"));
                }


                if(!TextUtils.isEmpty(user.getAddress())){
                    tvAddInput.setText(Html.fromHtml("<font color='#808080'><b>Address: </b></font><font color='#808080'><small>"+user.getAddress()+"</small></font>"));
                }else {
                    tvAddInput.setText(Html.fromHtml("<font color='#808080'><b>Address: </b></font><font color='#808080'>N/A</font>"));
                }


                if (!TextUtils.isEmpty(user.getLladdress())){
                    tvExeAdd.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'><small>"+user.getLladdress()+"</small></font>"));
                }else {
                    tvExeAdd.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'>N/A</font>"));
                }


                if (!TextUtils.isEmpty(user.getVisitDateTime())){
                    tvPlanTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'><small>"+user.getVisitDateTime()+"</small></font>"));
                }else {
                    tvPlanTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(user.getVisitedDateTime())){
                    tvExuDate.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'><small>"+user.getVisitedDateTime()+"</small></font>"));
                }else {
                    tvExuDate.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(user.getRemarks())){
                    tvOpin.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>"+user.getRemarks()+"</font>"));
                }else {
                    tvOpin.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>N/A</font>"));
                }


            }else if(type.equals("typeDr")){
                llValueView.setVisibility(View.GONE);
                tvDegree.setVisibility(View.GONE);
                tvAddInput.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(user.getImageUrl())){
                    Picasso.get()
                            .load(AppConstant.BASE_URL+"visitedImage/"+user.getImageUrl())
                            .placeholder(R.drawable.opl_logo)
                            .error(R.drawable.opl_logo)
                            .transform(new RoundedTransformation(0, 0))
                            .into(ivTakePic);
                }

                if (!TextUtils.isEmpty(user.getName())){
                    tvRName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>"+user.getName()+"</font>"));
                }else {
                    tvRName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>N/A</font>"));
                }

                if(!TextUtils.isEmpty(user.getAddress())){
                    tvAddInput.setText(Html.fromHtml("<font color='#808080'><b>Address: </b><small>"+user.getAddress()+"</small></font>"));
                }else {
                    tvAddInput.setText(Html.fromHtml("<font color='#808080'><b>Address: </b>N/A</font>"));
                }

                if (!TextUtils.isEmpty(user.getLladdress())){
                    tvExeAdd.setText(Html.fromHtml("<font color='#808080'><b>Ex.Address: </b><small>"+user.getLladdress()+"</small></font>"));
                }else {
                    tvExeAdd.setText(Html.fromHtml("<font color='#808080'><b>Ex.Address: </b><small>"+user.getLladdress()+"</small></font>"));
                }

                if (!TextUtils.isEmpty(user.getVisitDateTime())){
                    tvPlanTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'><small>"+user.getVisitDateTime()+"</small></font>"));
                }else {
                    tvPlanTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(user.getVisitedDateTime())){
                    tvExuDate.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'><small>"+user.getVisitedDateTime()+"</small></font>"));
                }else {
                    tvExuDate.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(user.getRemarks())){
                    tvOpin.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>"+user.getRemarks()+"</font>"));
                }else {
                    tvOpin.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>N/A</font>"));
                }

            }else if(type.equals("typeChemist")){
                tvDegree.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(user.getImageUrl())){
                    Picasso.get()
                            .load(AppConstant.BASE_URL+"visitedImage/"+user.getImageUrl())
                            .placeholder(R.drawable.opl_logo)
                            .error(R.drawable.opl_logo)
                            .transform(new RoundedTransformation(0, 0))
                            .into(ivTakePic);
                }
                if (!TextUtils.isEmpty(user.getName())){
                    tvRName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>"+user.getName()+"</font>"));
                }else {
                    tvRName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(user.getAddress())){
                    tvAddInput.setText(Html.fromHtml("<font color='#808080'><b>Address: </b></font><font color='#808080'><small>"+user.getAddress()+"</small></font>"));
                }else {
                    tvAddInput.setText(Html.fromHtml("<font color='#808080'><b>Address: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(user.getLladdress())){
                    tvExeAdd.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'><small>"+user.getLladdress()+"</small></font>"));
                }else {
                    tvExeAdd.setText(Html.fromHtml("<font color='#808080'><b>Exe.Address: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(user.getVisitDateTime())){
                    tvPlanTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'><small>"+user.getVisitDateTime()+"</small></font>"));
                }else {
                    tvPlanTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>N/A</font>"));
                }

                if (!TextUtils.isEmpty(user.getVisitedDateTime())){
                    tvExuDate.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'><small>"+user.getVisitedDateTime()+"</small></font>"));
                }else {
                    tvExuDate.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(user.getRemarks())){
                    tvOpin.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>"+user.getRemarks()+"</font>"));
                }else {
                    tvOpin.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>N/A</font>"));
                }
                if (user.getInvoiceAmount()>0.0f){
                    tvOrdVal.setText(Html.fromHtml("<font color='#000000'><b>Order: </b></font><font color='#808080'><small>"+user.getInvoiceAmount()+"</small></font>"));
                }else {
                    tvOrdVal.setText(Html.fromHtml("<font color='#000000'><b>Order: </b></font><font color='#808080'>"+"N/A"+"</font>"));
                }

                if (user.getCollectionAmount()>0.0f){
                    tvCltVal.setText(Html.fromHtml("<font color='#000000'><b>Collection: </b></font><font color='#808080'><small>"+user.getCollectionAmount()+"</small></font>"));
                }else {
                    tvCltVal.setText(Html.fromHtml("<font color='#000000'><b>Collection: </b></font><font color='#808080'>"+"N/A"+"</font>"));
                }
            }else if(type.equals("typeEmployeeReport")){
                llValueView.setVisibility(View.GONE);
                tvDegree.setVisibility(View.GONE);
                tvAddInput.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(user.getImageUrl())){
                    Picasso.get()
                            .load(AppConstant.BASE_URL+"visitedImage/"+user.getImageUrl())
                            .placeholder(R.drawable.opl_logo)
                            .error(R.drawable.opl_logo)
                            .transform(new RoundedTransformation(0, 0))
                            .into(ivTakePic);
                }

                if (!TextUtils.isEmpty(user.getName())){
                    tvRName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>"+user.getName()+"</font>"));
                }else {
                    tvRName.setText(Html.fromHtml("<font color='#000000'><b>NAME: </b></font><font color='#808080'>N/A</font>"));
                }

                if(!TextUtils.isEmpty(user.getAddress())){
                    tvAddInput.setText(Html.fromHtml("<font color='#808080'><b>Designation: </b><small>"+user.getAddress()+"</small></font>"));
                }else {
                    tvAddInput.setText(Html.fromHtml("<font color='#808080'><b>Designation: </b>N/A</font>"));
                }

                if (!TextUtils.isEmpty(user.getLladdress())){
                    tvExeAdd.setText(Html.fromHtml("<font color='#808080'><b>Ex.Address: </b><small>"+user.getLladdress()+"</small></font>"));
                }else {
                    tvExeAdd.setText(Html.fromHtml("<font color='#808080'><b>Ex.Address: </b><small>"+user.getLladdress()+"</small></font>"));
                }

                if (!TextUtils.isEmpty(user.getVisitDateTime())){
                    tvPlanTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'><small>"+user.getVisitDateTime()+"</small></font>"));
                }else {
                    tvPlanTime.setText(Html.fromHtml("<font color='#808080'><b>Plan: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(user.getVisitedDateTime())){
                    tvExuDate.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'><small>"+user.getVisitedDateTime()+"</small></font>"));
                }else {
                    tvExuDate.setText(Html.fromHtml("<font color='#808080'><b>Exe: </b></font><font color='#808080'>N/A</font>"));
                }
                if (!TextUtils.isEmpty(user.getRemarks())){
                    tvOpin.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>"+user.getRemarks()+"</font>"));
                }else {
                    tvOpin.setText(Html.fromHtml("<font color='#808080'><b>Remarks: </b></font><font color='#808080'>N/A</font>"));
                }
            }
        }

        btnOkDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

    }




}