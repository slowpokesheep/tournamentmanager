package is.hi.tournamentmanager.ui.tournaments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.service.ApiRepository;

public class TournamentUpdateDialog extends DialogFragment {
    private TournamentInfoViewModel viewModel;

    public static TournamentUpdateDialog newInstance(String id, String code, String field, String initial) {
        TournamentUpdateDialog frag = new TournamentUpdateDialog();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("code", code);
        args.putString("field", field);
        args.putString("initial", initial);
        frag.setArguments(args);
        return frag;
    }

    public void setViewModel(TournamentInfoViewModel v) {
        viewModel = v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String id = getArguments().getString("id");
        String code = getArguments().getString("code");
        String field = getArguments().getString("field");
        String fieldDisplay = Character.toUpperCase(field.charAt(0)) + field.substring(1).toLowerCase();
        String initial = getArguments().getString("initial");

        Log.d("field", field);

        final LinearLayout layout = new LinearLayout(getActivity());
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(50, 50, 50, 50);

        DatePicker datePicker = new DatePicker(getActivity());
        datePicker.setMinDate(System.currentTimeMillis());

        TimePicker timePicker = new TimePicker(getActivity());

        EditText input = new EditText(getActivity());

        switch (field) {
            case "date":
                layout.addView(datePicker);
                break;
            case "time":
                layout.addView(timePicker);
                break;
            default:
                input.setText(initial);
                layout.addView(input);
                break;
        }

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_mode_edit_black_24dp)
                .setTitle("Update Tournament " + fieldDisplay)
                .setView(layout)
                .setPositiveButton("Update", (dialog, whichButton) -> {
                    switch (field) {
                        case "name":
                            ApiRepository.getInstance().updateTournamentName(viewModel, code, id, input.getText().toString());
                            break;
                        case "location":
                            ApiRepository.getInstance().updateTournamentLocation(viewModel, code, id, input.getText().toString());
                            break;
                        case "date":
                            String month = String.valueOf(datePicker.getMonth() + 1);
                            if (month.length() == 1) month = "0" + month;
                            String day = String.valueOf(datePicker.getDayOfMonth());
                            if (day.length() == 1) day = "0" + day;
                            String dateString = datePicker.getYear() + "-" + month + "-" + day;
                            Log.d("date", dateString);
                            ApiRepository.getInstance().updateTournamentDate(viewModel, code, id, dateString);
                            break;
                        case "time":
                            String hour = String.valueOf(timePicker.getHour());
                            String minute = String.valueOf(timePicker.getMinute());
                            if (hour.length() == 1) hour = "0" + hour;
                            if (minute.length() == 1) minute = "0" + minute;
                            ApiRepository.getInstance().updateTournamentTime(viewModel, code, id, hour + ":" + minute);
                            break;
                        default:
                            break;
                    }
                })
                .setNegativeButton("Cancel", (dialog, whichButton) -> dialog.dismiss())
                .create();

    }
}
