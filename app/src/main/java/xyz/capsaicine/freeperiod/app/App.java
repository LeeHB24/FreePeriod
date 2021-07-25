
package xyz.capsaicine.freeperiod.app;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;


public class App extends Application {

    private static App app;

    // TODO - Acka: change debug false when deploy application
    public final static boolean isDebugMode = false;

    static Gson gson = new GsonBuilder().create();
    static GsonConverterFactory factory = GsonConverterFactory.create(gson);

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://49.236.132.218:8000/")
            .addConverterFactory(factory)
            .build();

    public static Context getAppContext() {
        return app.getApplicationContext();
    }

    public static Object getAppSystemService(String name){
        return app.getSystemService(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

}