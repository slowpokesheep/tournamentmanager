package is.hi.tournamentmanager.ui.tournaments.filters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.Arrays;
import java.util.List;

public class CategoryFilterDialogFragment extends DialogFragment {

    public static CategoryFilterDialogFragment newInstance() {
        CategoryFilterDialogFragment frag = new CategoryFilterDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // String array for alert dialog multi choice items
        String[] colors = new String[]{
                "Red",
                "Green",
                "Blue",
                "Purple",
                "Olive"
        };

        // Boolean array for initial selected items
        final boolean[] checkedColors = new boolean[]{
                false, // Red
                true, // Green
                false, // Blue
                true, // Purple
                false // Olive
        };

        // Convert the color array to list
        final List<String> colorsList = Arrays.asList(colors);

        builder.setMultiChoiceItems(colors, checkedColors, (dialog, which, isChecked) -> {
            checkedColors[which] = isChecked;
            String currentItem = colorsList.get(which);
            Toast.makeText(getContext(), currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();
        });

        builder.setCancelable(false);

        builder.setTitle("Colors test");

        builder.setPositiveButton("OK", (dialog, which) -> {

        });

        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

        return builder.create();

    }

}
