package mcteamgestapp.momo.com.mcteamgestapp.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Riccardo Rossi on 17/07/2016.
 */
@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private TextView mTextViewDate;
    private String mStringDate;
    static final DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    public DatePickerFragment(TextView textViewDate) {
        this.mTextViewDate = textViewDate;
        mStringDate = textViewDate.getText().toString();
    }

    public DatePickerFragment withCurrentDay() {
        return withCustomDate(df.format(new Date()));
    }

    public DatePickerFragment withCustomDate(String customDate) {
        mStringDate = customDate;
        return this;
    }

    private boolean isDateValid(String stringDate) {
        try {
            Date date = df.parse(stringDate);
            if (!stringDate.equals(df.format(date)))
                return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Usa la data selezionata dal form

        if (!isDateValid(mStringDate)) { //Se data in ingresso non valida, setto la data di oggi
            this.withCurrentDay();
        }

        String toBeSplitted = mStringDate;
        String[] dayMonthYear = toBeSplitted.split("-");

        int day = Integer.parseInt(dayMonthYear[0]);
        int month = Integer.parseInt(dayMonthYear[1]) - 1; //Mese parte da 0
        int year = Integer.parseInt(dayMonthYear[2]);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        DecimalFormat mFormat = new DecimalFormat("00");

        mTextViewDate.setText(mFormat.format(day) + "-" + mFormat.format(month + 1) + "-" + year);
    }
}
