package in.co.theshipper.www.shipper_customer.Fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import in.co.theshipper.www.shipper_customer.Constants;
import in.co.theshipper.www.shipper_customer.Helper;
import in.co.theshipper.www.shipper_customer.R;

/**
 * Created by Ashish on 7/21/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DatePickerDialog extends DialogFragment {

    View v;
    ImageView popup;
    Dialog dialog;
    DatePicker datePicker;
    TimePicker timePicker;
    AlertDialog.Builder alertDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(getActivity() != null) {

            alertDialog = new AlertDialog.Builder(getActivity());
            View view = getActivity().getLayoutInflater().inflate(R.layout.datetime_picker_fragment, null);
            alertDialog.setView(view);
            alertDialog.setCancelable(false);
            alertDialog.setTitle(Constants.Title.BOOKING_DATETIME);
            datePicker = (DatePicker) view.findViewById(R.id.date_picker);
            timePicker = (TimePicker) view.findViewById(R.id.time_picker);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                datePicker.setMinDate(System.currentTimeMillis() - Constants.Config.MIN_DATE_DURATION);
                datePicker.setMaxDate((System.currentTimeMillis() + Constants.Config.MAX_DATE_DURATION));
            }

            alertDialog.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth(),
                            timePicker.getCurrentHour(),
                            timePicker.getCurrentMinute());
                    long datetime = calendar.getTimeInMillis();
                    long currentTime = Helper.getDateTimeNowMillis();
                    currentTime = currentTime + Constants.Config.BOOK_LATER_DELAY;

                    if ((datetime >= currentTime) && (datetime > 0)) {

                        Helper.putPreference(getActivity(), Constants.Keys.LATER_BOOKING_DATETIME, Helper.getDate(datetime));
                        dialog.dismiss();

                    } else {

                        DatePickerDialog datepicker = new DatePickerDialog();
                        datepicker.show(getActivity().getFragmentManager(), "ABC");
                        Helper.ToastShort(getActivity(), Constants.Message.INVALID_DATETIME);

                    }

                }
            });

            alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);

                    }

                    return true;

                }
            });

        }

        dialog = alertDialog.create();
        return dialog;

    }

    public void show(FragmentManager childFragmentManager, String abc) {
        dialog.show();
    }

}
