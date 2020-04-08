package is.hi.tournamentmanager.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloCallback;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.tournament.LoginMutation;
import com.apollographql.apollo.tournament.MeQuery;
import com.apollographql.apollo.tournament.RegisterMutation;
import com.apollographql.apollo.tournament.TournamentsQuery;
import com.apollographql.apollo.tournament.type.UserCreateMutationInput;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import is.hi.tournamentmanager.MainActivity;
import is.hi.tournamentmanager.ui.authentication.LoginViewModel.AuthenticationState;
import is.hi.tournamentmanager.utils.ApolloConnector;
import is.hi.tournamentmanager.utils.SharedPref;

public class ApiRepository {
    public static ApiRepository apiRepository;
    private MainActivity mainActivity;
    private Handler uiHandler = new Handler(Looper.getMainLooper());

    public static ApiRepository getInstance() {
        if (apiRepository == null) {
            apiRepository = new ApiRepository();
        }
        return apiRepository;
    }

    public void init(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private String[] getErrorsAsStringArray(List<Error> errors) {
        ArrayList<String> errorsString = new ArrayList<>();
        for (Error e: errors) {
            errorsString.add(e.message());
        }
        return errorsString.toArray(new String[errorsString.size()]);
    }

    // ==== TOURNAMENTS ==== //

    public void getTournaments(MutableLiveData<TournamentsQuery.Data> tournamentsData, int type, int first, String after) {
        TournamentsQuery.Builder builder = TournamentsQuery.builder();
        // created tournaments
        if (type == 1) {
            Double userId = new Double(SharedPref.getInstance().getUserId());
            builder = builder.creator(userId);
        }
        // in tournaments
        else if (type == 2) {
            Double userId = new Double(SharedPref.getInstance().getUserId());
            builder = builder.registeredIn(userId);
        }
        builder = builder.first(first).after(after);
        TournamentsQuery query = builder.build();

        ApolloConnector.getInstance().getApolloClient().query(query)
            .enqueue(new ApolloCallback<>(new ApolloCall.Callback<TournamentsQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<TournamentsQuery.Data> response) {
                if (!response.hasErrors()) {
                    tournamentsData.setValue(response.data());
                } else {
                    mainActivity.showErrorsDialog(getErrorsAsStringArray(response.errors()));
                }
            }
            @Override
            public void onFailure(@NotNull ApolloException e) {
                mainActivity.showErrorsDialog(new String[]{ e.getMessage() });
            }
        }, uiHandler));
    }

    // ==== AUTHENTICATION ==== //

    public void login(MutableLiveData<AuthenticationState> authenticationState, String username, String password) {
        LoginMutation mutation = LoginMutation
            .builder()
            .username(username)
            .password(password)
            .build();

        ApolloConnector.getInstance().getApolloClient().mutate(mutation)
            .enqueue(new ApolloCallback<>(new ApolloCall.Callback<LoginMutation.Data>() {
                @Override
                public void onResponse(@NotNull Response<LoginMutation.Data> response) {
                    if (!response.hasErrors()) {
                        LoginMutation.TokenCreate token = response.data().tokenCreate();
                        // storing token and user id in shared preferences
                        SharedPref.getInstance().setToken(token.token(), token.user().idInt());
                        authenticationState.setValue(AuthenticationState.AUTHENTICATED);
                    } else {
                        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
                        mainActivity.showErrorsDialog(getErrorsAsStringArray(response.errors()));
                    }
                }
                @Override
                public void onFailure(@NotNull ApolloException e) {
                    authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
                    mainActivity.showErrorsDialog(new String[]{ e.getMessage() });
                }

            }, uiHandler));
    }

    public void register(String username, String email, String name, String password, String password2) {
        UserCreateMutationInput input = UserCreateMutationInput
                .builder()
                .username(username)
                .email(email)
                .name(name)
                .password1(password)
                .password2(password2)
                .build();

        RegisterMutation mutation = RegisterMutation
                .builder()
                .input(input)
                .build();

        ApolloConnector.getInstance().getApolloClient().mutate(mutation)
                .enqueue(new ApolloCall.Callback<RegisterMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<RegisterMutation.Data> response) {
                        List<RegisterMutation.Error> formValidationErrors = response.data().userCreate().errors();
                        // Success
                        if (!response.hasErrors() && formValidationErrors.size() == 0) {
                            Log.d("Register Mutation", response.data().toString());
                            mainActivity.showSimpleDialog(
                                    "Success!",
                                    "Thank you for registering. You can now " +
                                            "log in using your credentials."
                            );
                        }
                        // Error handling
                        else {
                            // form validation errors
                            if (!response.hasErrors()) {
                                ArrayList<String> formValidationMessages = new ArrayList<>();
                                for (RegisterMutation.Error e : formValidationErrors) {
                                    // output on the format 'field: message'
                                    String field = e.field();
                                    if (field.equals("password1")) field = "Password";
                                    else if (field.equals("password2")) field = "Confirm Password";
                                    else field = field.substring(0, 1).toUpperCase() + field.substring(1);
                                    for (String message: e.messages()) {
                                        formValidationMessages.add(field + " - " + message);
                                    }
                                }
                                mainActivity.showErrorsDialog(formValidationMessages.toArray(
                                        new String[formValidationMessages.size()]
                                ));
                            }
                            // internal errors
                            else {
                                mainActivity.showErrorsDialog(getErrorsAsStringArray(response.errors()));
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        mainActivity.showErrorsDialog(new String[]{ e.getMessage() });
                    }
                });
    }

    // ==== USER ==== //

    public void getMe(MutableLiveData<MeQuery.Data> meData) {
        MeQuery query = MeQuery
                .builder()
                .build();

        ApolloConnector.getInstance().getApolloClient().query(query)
            .enqueue(new ApolloCallback<>(new ApolloCall.Callback<MeQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<MeQuery.Data> response) {
                    if (!response.hasErrors() && response.data().me() != null) {
                        meData.setValue(response.data());
                    } else {
                        SharedPref.getInstance().clearToken();
                        mainActivity.showErrorsDialog(getErrorsAsStringArray(response.errors()));
                    }
                }
                @Override
                public void onFailure(@NotNull ApolloException e) {
                    SharedPref.getInstance().clearToken();
                    mainActivity.showErrorsDialog(new String[]{ e.getMessage() });
                }
            }, uiHandler));
    }

}
