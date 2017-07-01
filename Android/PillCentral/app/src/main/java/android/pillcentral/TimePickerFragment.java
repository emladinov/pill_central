package android.pillcentral;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Created by Admin on 6/30/2017.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_DARK,this,  hour, minute, false);
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        TextView timeset = (TextView) getActivity().findViewById(R.id.timeset);
        TextView ampm = (TextView) getActivity().findViewById(R.id.AMPM);

        //Get the AM or PM for current time
        String aMpM = "AM";
        String min = "00";
        if(hourOfDay >11)
        {
            aMpM = "PM";
        }

        //Make the 24 hour time format to 12 hour time format
        int currentHour;
        if(hourOfDay>11)
        {
            currentHour = hourOfDay - 12;
        }
        else
        {
            currentHour = hourOfDay;
        }
        if(minute == 0)
        {
                min = "00";
        }
        else
        {
            min = String.valueOf(minute);
        }

        timeset.setText( String.valueOf(currentHour)+ ":" + min);
        ampm.setText(aMpM);

    }
}
