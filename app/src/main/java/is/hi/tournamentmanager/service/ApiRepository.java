package is.hi.tournamentmanager.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloCallback;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.tournament.LoginMutation;
import com.apollographql.apollo.tournament.MeQuery;
import com.apollographql.apollo.tournament.RegisterMutation;
import com.apollographql.apollo.tournament.SeedBracketMutation;
import com.apollographql.apollo.tournament.SubmitMatchMutation;
import com.apollographql.apollo.tournament.TournamentBracketQuery;
import com.apollographql.apollo.tournament.TournamentInfoQuery;
import com.apollographql.apollo.tournament.TournamentSearchQuery;
import com.apollographql.apollo.tournament.TournamentUpdateDateMutation;
import com.apollographql.apollo.tournament.TournamentUpdateLocationMutation;
import com.apollographql.apollo.tournament.TournamentUpdateNameMutation;
import com.apollographql.apollo.tournament.TournamentUpdateTimeMutation;
import com.apollographql.apollo.tournament.TournamentUsersQuery;
import com.apollographql.apollo.tournament.TournamentsQuery;
import com.apollographql.apollo.tournament.type.UserCreateMutationInput;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import is.hi.tournamentmanager.MainActivity;
import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.ui.authentication.LoginViewModel.AuthenticationState;
import is.hi.tournamentmanager.ui.tournaments.TournamentBracketViewModel;
import is.hi.tournamentmanager.ui.tournaments.TournamentInfoViewModel;
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

    public void getTournaments(MutableLiveData<TournamentsQuery.Data> tournamentsData, int type, int superCategory, String search, boolean onlyOpen, int first, String after) {
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
        // 0 - all, 1 - sports, 2 - gaming
        if (superCategory > 0) {
            builder = builder.superCategory((double) superCategory);
        }
        // search string for categories + name
        if (search.length() > 0) {
            builder = builder.search(search);
        }
        if (onlyOpen) {
            builder = builder.statuses("open");
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

    // validation method (checks if the code is valid/exists)
    public void tournamentSearch(String code, NavController navController) {
        TournamentSearchQuery.Builder builder = TournamentSearchQuery.builder();
        builder.code(code);
        TournamentSearchQuery query = builder.build();

        ApolloConnector.getInstance().getApolloClient().query(query)
            .enqueue(new ApolloCallback<>(new ApolloCall.Callback<TournamentSearchQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<TournamentSearchQuery.Data> response) {
                    if (!response.hasErrors()) {
                        Bundle args = new Bundle();
                        args.putString("code", code);
                        navController.navigate(R.id.nav_tournament, args);
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

    public void getTournamentInfo(MutableLiveData<TournamentInfoQuery.Data> tournamentInfoData, String code) {
        TournamentInfoQuery.Builder builder = TournamentInfoQuery.builder();
        builder.code(code);
        TournamentInfoQuery query = builder.build();

        ApolloConnector.getInstance().getApolloClient().query(query)
            .enqueue(new ApolloCallback<>(new ApolloCall.Callback<TournamentInfoQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<TournamentInfoQuery.Data> response) {
                    if (!response.hasErrors()) {
                        tournamentInfoData.setValue(response.data());
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

    public void getTournamentUsers(MutableLiveData<TournamentUsersQuery.Data> tournamentUserData, String code) {
        TournamentUsersQuery.Builder builder = TournamentUsersQuery.builder();
        builder.code(code);
        TournamentUsersQuery query = builder.build();

        ApolloConnector.getInstance().getApolloClient().query(query)
            .enqueue(new ApolloCallback<>(new ApolloCall.Callback<TournamentUsersQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<TournamentUsersQuery.Data> response) {
                    if (!response.hasErrors()) {
                        tournamentUserData.setValue(response.data());
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

    public void updateTournamentName(TournamentInfoViewModel viewModel, String code, String id, String name) {
        TournamentUpdateNameMutation.Builder builder = TournamentUpdateNameMutation.builder();
        builder.id(id);
        builder.name(name);
        TournamentUpdateNameMutation mutation = builder.build();

        ApolloConnector.getInstance().getApolloClient().mutate(mutation)
                .enqueue(new ApolloCallback<>(new ApolloCall.Callback<TournamentUpdateNameMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<TournamentUpdateNameMutation.Data> response) {
                        if (!response.hasErrors()) {
                            viewModel.fetchTournamentInfo(code);
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

    public void updateTournamentLocation(TournamentInfoViewModel viewModel, String code, String id, String location) {
        TournamentUpdateLocationMutation.Builder builder = TournamentUpdateLocationMutation.builder();
        builder.id(id);
        builder.location(location);
        TournamentUpdateLocationMutation mutation = builder.build();

        ApolloConnector.getInstance().getApolloClient().mutate(mutation)
                .enqueue(new ApolloCallback<>(new ApolloCall.Callback<TournamentUpdateLocationMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<TournamentUpdateLocationMutation.Data> response) {
                        if (!response.hasErrors()) {
                            viewModel.fetchTournamentInfo(code);
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

    public void updateTournamentDate(TournamentInfoViewModel viewModel, String code, String id, String date) {
        TournamentUpdateDateMutation.Builder builder = TournamentUpdateDateMutation.builder();
        builder.id(id);
        builder.date(date);
        TournamentUpdateDateMutation mutation = builder.build();

        ApolloConnector.getInstance().getApolloClient().mutate(mutation)
                .enqueue(new ApolloCallback<>(new ApolloCall.Callback<TournamentUpdateDateMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<TournamentUpdateDateMutation.Data> response) {
                        if (!response.hasErrors()) {
                            viewModel.fetchTournamentInfo(code);
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

    public void updateTournamentTime(TournamentInfoViewModel viewModel, String code, String id, String time) {
        TournamentUpdateTimeMutation.Builder builder = TournamentUpdateTimeMutation.builder();
        builder.id(id);
        builder.time(time);
        TournamentUpdateTimeMutation mutation = builder.build();

        ApolloConnector.getInstance().getApolloClient().mutate(mutation)
                .enqueue(new ApolloCallback<>(new ApolloCall.Callback<TournamentUpdateTimeMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<TournamentUpdateTimeMutation.Data> response) {
                        if (!response.hasErrors()) {
                            viewModel.fetchTournamentInfo(code);
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

    public void getTournamentBracket(MutableLiveData<TournamentBracketQuery.Data> bracketData, String code) {
        TournamentBracketQuery.Builder builder = TournamentBracketQuery.builder();
        builder.code(code);
        TournamentBracketQuery query = builder.build();

        ApolloConnector.getInstance().getApolloClient().query(query)
            .enqueue(new ApolloCallback<>(new ApolloCall.Callback<TournamentBracketQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<TournamentBracketQuery.Data> response) {
                    if (!response.hasErrors()) {
                        bracketData.setValue(response.data());
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

    public void seedTournamentBracket(TournamentBracketViewModel viewModel, String id, String code) {
        SeedBracketMutation.Builder builder = SeedBracketMutation.builder();
        builder.id(id);
        SeedBracketMutation mutation = builder.build();

        ApolloConnector.getInstance().getApolloClient().mutate(mutation)
            .enqueue(new ApolloCallback<>(new ApolloCall.Callback<SeedBracketMutation.Data>() {
                @Override
                public void onResponse(@NotNull Response<SeedBracketMutation.Data> response) {
                    if (!response.hasErrors()) {
                        viewModel.fetchTournamentBracket(code);
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

    public void submitMatch(TournamentBracketViewModel viewModel, int id, String code, int home, int visitor) {
        SubmitMatchMutation.Builder builder = SubmitMatchMutation.builder();
        builder.id(String.valueOf(id));
        builder.home(home);
        builder.visitor(visitor);
        SubmitMatchMutation mutation = builder.build();

        ApolloConnector.getInstance().getApolloClient().mutate(mutation)
                .enqueue(new ApolloCallback<>(new ApolloCall.Callback<SubmitMatchMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SubmitMatchMutation.Data> response) {
                        if (!response.hasErrors()) {
                            Log.d("code", code);
                            viewModel.fetchTournamentBracket(code);
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
                        SharedPref.getInstance().setUserInfo(username, token.token(), token.user().idInt());
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

    public void register(String username, String email, String name, String password, String password2, boolean superuser) {
        UserCreateMutationInput input = UserCreateMutationInput
                .builder()
                .username(username)
                .email(email)
                .name(name)
                .password1(password)
                .password2(password2)
                .setAsSuperuser(superuser)
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
                        SharedPref.getInstance().clearUserInfo();
                        mainActivity.showErrorsDialog(getErrorsAsStringArray(response.errors()));
                    }
                }
                @Override
                public void onFailure(@NotNull ApolloException e) {
                    SharedPref.getInstance().clearUserInfo();
                    mainActivity.showErrorsDialog(new String[]{ e.getMessage() });
                }
            }, uiHandler));
    }

}
