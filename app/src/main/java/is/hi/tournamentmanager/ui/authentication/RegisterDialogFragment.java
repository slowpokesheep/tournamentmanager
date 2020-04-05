package is.hi.tournamentmanager.ui.authentication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.LinearLayout;

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
        layout.addView(usernameInput);
        layout.addView(emailInput);
        layout.addView(nameInput);
        layout.addView(passwordInput);
        layout.addView(password2Input);

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
                                password2Input.getText().toString()
                        ))
                .setNeutralButton("Cancel", (dialog, whichButton) -> dialog.cancel())
                .create();
    }
}
