package is.hi.tournamentmanager.utils;

import android.app.Application;
import android.content.SharedPreferences;
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
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

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

    public ApolloClient setupApollo(Application app) {
        ApolloSqlHelper apolloSqlHelper = ApolloSqlHelper.create(app.getApplicationContext());
        NormalizedCacheFactory cacheFactory = new SqlNormalizedCacheFactory(apolloSqlHelper);
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
                .normalizedCache(cacheFactory, resolver)
                .okHttpClient(okHttpClient)
                .build();

        return apolloClient;
    }

    public ApolloClient getApolloClient() {
        return apolloClient;
    }

}
