package is.hi.tournamentmanager.utils.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import is.hi.tournamentmanager.R;

public class ErrorsDialogFragment extends DialogFragment {

    public static ErrorsDialogFragment newInstance(String[] errors) {
        ErrorsDialogFragment frag = new ErrorsDialogFragment();
        Bundle args = new Bundle();
        args.putStringArray("errors", errors);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] errors = getArguments().getStringArray("errors");
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(25, 25, 25, 25);

        for (String error: errors) {
            final TextView textView = new TextView(getActivity());
            textView.setText(error);
            textView.setPadding(25, 25, 25, 25);
            textView.setTextColor(Color.RED);
            layout.addView(textView);
        }

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_error_black_24dp)
                .setTitle("One or more errors occurred")
                .setNeutralButton("Close", (dialog, whichButton) -> dialog.cancel())
                .setView(layout)
                .create();
    }
}
