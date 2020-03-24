package is.hi.tournamentmanager.utils.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class SimpleMessageDialogFragment extends DialogFragment {
    public static SimpleMessageDialogFragment newInstance(String title, String message) {
        SimpleMessageDialogFragment frag = new SimpleMessageDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        final TextView textView = new TextView(getActivity());
        textView.setText(message);
        layout.addView(textView);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setNegativeButton("Close", (dialog, whichButton) -> dialog.cancel())
                .setView(layout)
                .create();
    }
}
