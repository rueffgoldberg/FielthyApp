package example.com.fielthyapps.Service;

import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.QueryStringSigningStrategy;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

public class ApiUtils {

    private ApiUtils() {
    }

    public static final String BASE_URL = "https://platform.fatsecret.com/rest/";
    public static final String CONSUMER_KEY = "b732ae24309d4aa08a6136f0c32b0fbb";
    public static final String CONSUMER_SECRET = "a71622a4fe1849b0b7239ee36470cdd4";

    public static FatSecretService getAPIService() {

        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        consumer.setMessageSigner(new HmacSha1MessageSigner());
        consumer.setSigningStrategy(new QueryStringSigningStrategy());
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .addInterceptor(loggingInterceptor)
                .build();
        return RetrofitClient.getClient(BASE_URL, client).create(FatSecretService.class);
    }
}