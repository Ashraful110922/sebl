package helper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import com.opl.one.oplsales.R;
import java.util.Calendar;


public class DMYPDialog extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private int selectYear,selectMonth;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.month_year_picker_dialog, null);
        final NumberPicker dayPicker= (NumberPicker) dialog.findViewById(R.id.picker_day);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);


        String[] arrayString= new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};

        int day = cal.get(Calendar.DAY_OF_MONTH);
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        dayPicker.setValue(day);

        int year = cal.get(Calendar.YEAR);
        cal.add(Calendar.YEAR, -10);
        yearPicker.setMinValue(cal.get(Calendar.YEAR));

        cal.add(Calendar.YEAR, 15);
        yearPicker.setMaxValue(cal.get(Calendar.YEAR));
        yearPicker.setValue(year);

        //new Code for Year
        selectYear = year;
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldYear, int newYear) {
                selectYear = newYear;
                if((selectMonth==1) && (selectYear % 4 == 0 && (selectYear % 100 != 0 || selectYear % 400 == 0))){
                    dayPicker.setMaxValue(29);
                }else if( selectMonth == 3 || selectMonth == 5|| selectMonth==8 || selectMonth==10 ){
                    dayPicker.setMaxValue(30);
                }else if(selectMonth == 0 || selectMonth == 2 || selectMonth == 4 ||selectMonth ==6 ||selectMonth == 7 || selectMonth==9 ||selectMonth == 11){
                    dayPicker.setMaxValue(31);
                }else {
                    dayPicker.setMaxValue(28);
                }
            }
        });
        //new Code for Month
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setDisplayedValues(arrayString);
        monthPicker.setValue(cal.get(Calendar.MONTH));
        selectMonth = cal.get(Calendar.MONTH);
        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldMonth, int newMonth) {
                selectMonth = newMonth;
                if((selectMonth==1) && (selectYear % 4 == 0 && (selectYear % 100 != 0 || selectYear % 400 == 0))){
                    dayPicker.setMaxValue(29);
                }else if( selectMonth == 3 || selectMonth == 5|| selectMonth==8 || selectMonth==10 ){
                    dayPicker.setMaxValue(30);
                }else if(selectMonth == 0 || selectMonth == 2 || selectMonth == 4 ||selectMonth ==6 ||selectMonth == 7 || selectMonth==9 ||selectMonth == 11){
                    dayPicker.setMaxValue(31);
                }else {
                    dayPicker.setMaxValue(28);
                }
            }
        });

        builder.setView(dialog).setPositiveButton("SET DATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), dayPicker.getValue());
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DMYPDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
