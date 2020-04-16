package is.hi.tournamentmanager.ui.authentication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.service.ApiRepository;

public class RegisterDialogFragment extends DialogFragment {

    public static RegisterDialogFragment newInstance() {
        RegisterDialogFragment frag = new RegisterDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50 , 50, 50);

        final EditText usernameInput = new EditText(getActivity());
        final EditText emailInput = new EditText(getActivity());
        final EditText nameInput = new EditText(getActivity());
        final EditText passwordInput = new EditText(getActivity());
        final EditText password2Input = new EditText(getActivity());
        final CheckBox superuser = new CheckBox(getActivity());

        usernameInput.setHint("Username");
        emailInput.setHint("Email");
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        nameInput.setHint("Name");
        passwordInput.setHint("Password");
        password2Input.setHint("Confirm Password");
        passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password2Input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password2Input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        superuser.setText("Superuser*");
        superuser.setTextColor(Color.rgb(128, 128, 128));
        TextView superuserInfo = new TextView(getActivity());
        superuserInfo.setText("*Superusers can modify all tournaments, this option is temporarily available for testing purposes.");
        superuserInfo.setTextSize(12);

        layout.addView(usernameInput);
        layout.addView(emailInput);
        layout.addView(nameInput);
        layout.addView(passwordInput);
        layout.addView(password2Input);
        layout.addView(superuser);
        layout.addView(superuserInfo);

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_perm_identity_black_24dp)
                .setTitle("Register")
                .setView(layout)
                .setPositiveButton("Register", (dialog, whichButton) ->
                        ApiRepository.getInstance().register(
                                usernameInput.getText().toString(),
                                emailInput.getText().toString(),
                                nameInput.getText().toString(),
                                passwordInput.getText().toString(),
                                password2Input.getText().toString(),
                                superuser.isChecked()
                        ))
                .setNegativeButton("Cancel", (dialog, whichButton) -> dialog.dismiss())
                .create();
    }
}
