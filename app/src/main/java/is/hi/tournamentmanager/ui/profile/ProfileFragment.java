package is.hi.tournamentmanager.ui.profile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloCallback;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.tournament.LoginMutation;
import com.apollographql.apollo.tournament.TournamentsQuery;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.utils.ApolloConnector;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    private Handler uiHandler = new Handler(Looper.getMainLooper());

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView textView = root.findViewById(R.id.text_profile);

        Button loginButton = root.findViewById(R.id.login_form_submit);
        EditText usernameInput = root.findViewById(R.id.login_form_username);
        EditText passwordInput = root.findViewById(R.id.login_form_password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("login button", "click");
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                login(username, password);
            }
        });

        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    public void login(String username, String password) {
        LoginMutation mutation = LoginMutation
            .builder()
            .username(username)
            .password(password)
            .build();
        ApolloConnector.getInstance().getApolloClient().mutate(mutation)
            .enqueue(new ApolloCallback<>(new ApolloCall.Callback<LoginMutation.Data>() {

                @Override
                public void onResponse(@NotNull Response<LoginMutation.Data> response) {
                    Log.d("Login", "Response: " + response.data());
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    Log.d("Tournament", "Exception " + e.getMessage(), e);
                }

            }, uiHandler));
    }
}
