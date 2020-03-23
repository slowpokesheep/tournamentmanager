package is.hi.tournamentmanager.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloCallback;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.tournament.LoginMutation;
import com.apollographql.apollo.tournament.MeQuery;
import com.apollographql.apollo.tournament.TournamentsQuery;

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

    public void getTournaments(MutableLiveData<TournamentsQuery.Data> tournamentsData, int first) {
        TournamentsQuery query = TournamentsQuery
            .builder()
            .first(first)
            .build();
        ApolloConnector.getInstance().getApolloClient().query(query)
            .enqueue(new ApolloCallback<>(new ApolloCall.Callback<TournamentsQuery.Data>() {

            @Override
            public void onResponse(@NotNull Response<TournamentsQuery.Data> response) {
                if (!response.hasErrors()) {
                    tournamentsData.setValue(response.data());
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
                        // storing token in shared preferences
                        SharedPref.getInstance().setToken(token.token());
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

    // ==== USER ==== //

    public void getMe(MutableLiveData<MeQuery.Data> meData) {
        MeQuery query = MeQuery
                .builder()
                .build();
        ApolloConnector.getInstance().getApolloClient().query(query)
                .enqueue(new ApolloCallback<>(new ApolloCall.Callback<MeQuery.Data>() {

                    @Override
                    public void onResponse(@NotNull Response<MeQuery.Data> response) {
                        if (!response.hasErrors()) {
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
