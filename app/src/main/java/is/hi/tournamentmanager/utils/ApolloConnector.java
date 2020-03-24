package is.hi.tournamentmanager.utils;

import android.app.Application;
import android.util.Log;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.cache.normalized.CacheKey;
import com.apollographql.apollo.cache.normalized.CacheKeyResolver;
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory;
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy;
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory;
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.interceptor.ApolloInterceptor;
import com.apollographql.apollo.interceptor.ApolloInterceptorChain;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.Executor;

import is.hi.tournamentmanager.MainActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;

// singleton class that initiates our API connection.
public class ApolloConnector {
    public static ApolloConnector apolloConnector;

    public static ApolloConnector getInstance() {
        if (apolloConnector == null) {
            apolloConnector = new ApolloConnector();
        }
        return apolloConnector;
    }

    private final String BASE_URL = "https://tmmanagerbackend.herokuapp.com/";
    private ApolloClient apolloClient;

    public ApolloClient setupApollo(Application app, MainActivity mainActivity) {
        ApolloSqlHelper apolloSqlHelper = ApolloSqlHelper.create(app.getApplicationContext());
        NormalizedCacheFactory cacheFactory = new LruNormalizedCacheFactory(EvictionPolicy.builder().maxSizeBytes(10 * 1024).build());

        // Create the cache key resolver, this example works well when all types have globally unique ids.
        CacheKeyResolver resolver =  new CacheKeyResolver() {
            @NotNull @Override
            public CacheKey fromFieldRecordSet(@NotNull ResponseField field, @NotNull Map<String, Object> recordSet) {
                return formatCacheKey((String) recordSet.get("id"));
            }

            @NotNull @Override
            public CacheKey fromFieldArguments(@NotNull ResponseField field, @NotNull Operation.Variables variables) {
                return formatCacheKey((String) field.resolveArgument("id", variables));
            }

            private CacheKey formatCacheKey(String id) {
                if (id == null || id.isEmpty()) {
                    return CacheKey.NO_KEY;
                } else {
                    return CacheKey.from(id);
                }
            }
        };

        // We add an interceptor so we can display a spinner at the start of each request and hide at the end of it
        ApolloInterceptor interceptor = new ApolloInterceptor() {
            @Override
            public void interceptAsync(@NotNull InterceptorRequest request, @NotNull ApolloInterceptorChain chain, @NotNull Executor dispatcher, @NotNull CallBack callBack) {
                mainActivity.runOnUiThread(() -> mainActivity.displaySpinner());
                CallBack newCallBack = new CallBack() {
                    @Override
                    public void onResponse(@NotNull InterceptorResponse response) {
                        callBack.onResponse(response);
                    }
                    @Override
                    public void onFetch(FetchSourceType sourceType) {
                        callBack.onFetch(sourceType);
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        callBack.onFailure(e);
                    }
                    @Override
                    public void onCompleted() {
                        callBack.onCompleted();
                        mainActivity.runOnUiThread(() -> mainActivity.hideSpinner());
                    }
                };
                chain.proceedAsync(request, dispatcher, newCallBack);
            }

            @Override
            public void dispose() {
                Log.d("Interceptor", "dispose");
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                    // check for auth token
                    String token = SharedPref.getInstance().getToken();
                    if (token != null) {
                        Log.d("auth token", token);
                        builder.header("Authorization", "JWT " + token);
                    }
                    return chain.proceed(builder.build());
                })
                .build();
        apolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                // .normalizedCache(cacheFactory, resolver)
                .addApplicationInterceptor(interceptor)
                .okHttpClient(okHttpClient)
                .build();

        return apolloClient;
    }

    public ApolloClient getApolloClient() {
        return apolloClient;
    }

}
