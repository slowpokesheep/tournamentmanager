package is.hi.tournamentmanager.ui.tournaments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.service.ApiRepository;

public class MatchResultsDialog extends DialogFragment {
    TournamentBracketViewModel viewModel;

    public static MatchResultsDialog newInstance(int id, String code, String homeUser, String visitorUser) {
        MatchResultsDialog frag = new MatchResultsDialog();
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("code", code);
        args.putString("homeUser", homeUser);
        args.putString("visitorUser", visitorUser);
        frag.setArguments(args);
        return frag;
    }

    public void setViewModel(TournamentBracketViewModel v) {
        viewModel = v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int id = getArguments().getInt("id");
        String code = getArguments().getString("code");
        String homeUser = getArguments().getString("homeUser");
        String visitorUser = getArguments().getString("visitorUser");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(25, 25, 25, 25);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams textParamsLeft1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textParamsLeft1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        RelativeLayout.LayoutParams textParamsRight1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textParamsRight1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        RelativeLayout.LayoutParams textParamsLeft2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textParamsLeft2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        RelativeLayout.LayoutParams textParamsRight2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textParamsRight2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        RelativeLayout homeLayout = new RelativeLayout(getActivity());
        // homeLayout.setPadding(10, 10, 10, 10);
        TextView homeTextView = new TextView(getActivity());
        homeTextView.setText(homeUser + "'s score:");
        homeTextView.setTextSize(16);
        homeTextView.setHeight(150);
        homeTextView.setGravity(Gravity.CENTER);
        homeTextView.setMinimumWidth(0);
        homeLayout.addView(homeTextView, textParamsLeft1);
        EditText homeEdit = new EditText(getActivity());
        homeEdit.setHeight(50);
        homeEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        homeEdit.setGravity(Gravity.CENTER);
        homeLayout.addView(homeEdit, textParamsRight1);

        RelativeLayout visitorLayout = new RelativeLayout(getActivity());
        // visitorLayout.setPadding(10, 10, 10, 10);
        TextView visitorTextView = new TextView(getActivity());
        visitorTextView.setText(visitorUser + "'s score:");
        visitorTextView.setTextSize(16);
        visitorTextView.setHeight(150);
        visitorTextView.setGravity(Gravity.CENTER);
        visitorTextView.setMinimumWidth(0);
        visitorLayout.addView(visitorTextView, textParamsLeft2);
        EditText visitorEdit = new EditText(getActivity());
        visitorEdit.setHeight(50);
        visitorEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        visitorEdit.setGravity(Gravity.CENTER);
        visitorLayout.addView(visitorEdit, textParamsRight2);

        TextView errorMessage = new TextView(getActivity());
        errorMessage.setTextColor(Color.RED);

        layout.addView(homeLayout);
        layout.addView(visitorLayout);
        layout.addView(errorMessage);

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Submit match results")
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", (dialog, whichButton) -> dialog.dismiss())
                .setView(layout)
                .create();

        alertDialog.setOnShowListener(dialog -> {
            Button button = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                String homeEditText = homeEdit.getText().toString();
                String visitorEditText = visitorEdit.getText().toString();
                boolean isEmpty = homeEditText.length() == 0 || visitorEditText.length() == 0;
                if (isEmpty) {
                    errorMessage.setText("Please fill out the score fields.");
                } else {
                    int homeScore = Integer.parseInt(homeEdit.getText().toString());
                    int visitorScore = Integer.parseInt(visitorEdit.getText().toString());
                    ApiRepository.getInstance().submitMatch(viewModel, id, code, homeScore, visitorScore);
                    dialog.dismiss();
                }
            });
        });

        return alertDialog;
    }
}
