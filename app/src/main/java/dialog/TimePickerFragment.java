package dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.opl.one.oplsales.R;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // Get a Calendar instance
        final Calendar calendar = Calendar.getInstance();
        // Get the current hour and minute
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        /*
            Creates a new time picker dialog with the specified theme.

                TimePickerDialog(Context context, int themeResId,
                    TimePickerDialog.OnTimeSetListener listener,
                    int hourOfDay, int minute, boolean is24HourView)
         */

        // TimePickerDialog Theme : THEME_HOLO_LIGHT
        TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                AlertDialog.THEME_HOLO_LIGHT,this,hour,minute,false);

        tpd.setTitle("Select Time");
        return tpd;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        String status = "am";
        if(hourOfDay > 11)
        {
            status = "pm";
        }
        int hour_of_12_hour_format;

        if(hourOfDay > 11){
            if (hourOfDay ==12){
                hour_of_12_hour_format = 12;
            }else {
                hour_of_12_hour_format = hourOfDay - 12;
            }
        }
        else {
            hour_of_12_hour_format = hourOfDay;
        }
        TextView tv = (TextView) getActivity().findViewById(R.id.timePicker);
        tv.setText(hour_of_12_hour_format + ":" + minute + " " + status);
    }
}
