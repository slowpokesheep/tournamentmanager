package is.hi.tournamentmanager.api;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;

public class ApolloConnector {

    private static final String BASE_URL = "https://tmmanagerbackend.herokuapp.com/";
    private static ApolloClient apolloClient;

    public static ApolloClient setupApollo() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build();
        return apolloClient;
    }

    public static ApolloClient getApolloClient() {
        return apolloClient;
    }

}
