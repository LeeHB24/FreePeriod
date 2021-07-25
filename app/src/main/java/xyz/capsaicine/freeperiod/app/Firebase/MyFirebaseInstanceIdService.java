package xyz.capsaicine.freeperiod.app.Firebase;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.Display;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.ArrayList;

import retrofit.Call;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatRoomItem;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.App;
import xyz.capsaicine.freeperiod.app.DB;
import xyz.capsaicine.freeperiod.app.NetworkService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    public MyFirebaseInstanceIdService() {
        super();
    }

    private static final String TAG = "MyFirebaseIIDService";
    private final String TOKEN = "token";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        saveSharedPreferenceToken(refreshedToken);
//        sendRegistrationToServer(Account.getInstance().getUserId(),refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    //추후에 REST로 SERVER에 USER ID 와 함께 보낼 것 최초(LOGIN)
    public void sendRegistrationToServer(int userId, String token) {
        DB.getInstance().postUserToken(userId, token);
        // TODO: Implement this method to send token to your app server.
    }


    private void saveSharedPreferenceToken(String token){
        SharedPreferences pref = getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(TOKEN, token);
        editor.commit();
    }

    public String getSharedPreferenceToken(){
        SharedPreferences pref = getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getString(TOKEN, "");
    }


}
