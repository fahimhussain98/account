package wts.com.accountpe.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    static {
        System.loadLibrary("native-lib");
    }

    public native static String getAPIHost();
    public native static String getAPI();
    public native static String getAuthKey();

    public static final String BASE_URL = getAPIHost();
    public static final String BASE_URL_PLANS = getAPI();
    public static final String AUTH_KEY = getAuthKey();
    private static RetrofitClient mInstance;
    private final retrofit2.Retrofit retrofit,retrofitPlans;

    private RetrofitClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(7000, TimeUnit.SECONDS)
                .readTimeout(7000, TimeUnit.SECONDS).build();

        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitPlans = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL_PLANS).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public WebServiceInterface getApi() {

        return retrofit.create(WebServiceInterface.class);
    }

    public WebServiceInterface getApiPlans() {

        return retrofitPlans.create(WebServiceInterface.class);
    }
}
