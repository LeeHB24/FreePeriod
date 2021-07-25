package xyz.capsaicine.freeperiod.activities.login;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.ChatService;
import xyz.capsaicine.freeperiod.activities.home.HomeActivity;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.DB;
import xyz.capsaicine.freeperiod.app.Firebase.MyFirebaseInstanceIdService;
import xyz.capsaicine.freeperiod.app.NetworkService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static xyz.capsaicine.freeperiod.app.App.isDebugMode;
import static xyz.capsaicine.freeperiod.app.App.retrofit;

public class LoginActivity extends AppCompatActivity implements Button.OnClickListener{

    private static final String TAG = "MyFirebaseIIDService";
    private final String TOKEN = "token";
    public static String ID = "ID";
    public static String EMAIL = "EMAIL";
    public static String PASSWORD = "PW";
    public static String SCHOOL = "SHCOOL";
    public static String NAME = "NAME";
    public static String ISAUTOLOGIN = "ISAUTOLOGIN";
    public static String CHATROOMRESTED = "CHATROOMRESTED";


    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private Button registerButton;
    private Button developerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autoLogin();

        emailText = (EditText)findViewById(R.id.emailText);
        passwordText = (EditText)findViewById(R.id.passwordText);

        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton1);
        developerButton = (Button)findViewById(R.id.dev_btn);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        developerButton.setOnClickListener(this);
        developerButton.setVisibility(View.GONE);

     /*  registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, WriteReviewActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
         });*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginButton: {
                tryLogin(emailText.getText().toString(), passwordText.getText().toString());
                break;
            }
            case R.id.registerButton1: {
                moveToRegistActivity();
                break;
            }
            case R.id.dev_btn: {
                tryLogin(DB.adminId, DB.adminPassword);
                break;
            }
        }
    }

    private void tryLogin(String loginEmail, String loginPassword){
        //if(isDebugMode){
        //    completeLogin(-1, loginEmail, loginPassword, null, null);
        //}
        NetworkService networkService = retrofit.create(NetworkService.class);
        Call<ValidationResult> loginCall = networkService.validateUser(loginEmail, loginPassword);
        loginCall.enqueue(new Callback<ValidationResult>() {
            @Override
            public void onResponse(Response<ValidationResult> response, Retrofit retrofit) {
                ValidationResult validation = response.body();
                if(validation.strReceiveMessage.equals("success")){
                    // TODO - Jinnel: fill other fields of Account instance
                    //ADDED FOR AUTO LOGIN
                    setAutoLogin(validation.userId, loginEmail, loginPassword, null, validation.userName);
                    completeLogin(validation.userId, loginEmail, loginPassword, null, validation.userName);
                }
                else if(validation.strReceiveMessage.equals("no_validate")){
                    Toast.makeText(getBaseContext(), "메일 인증을 완료 후 로그인 해주십시오.", Toast.LENGTH_SHORT).show();
                }
                else if(validation.strReceiveMessage.equals("block_user")){
                    Toast.makeText(getBaseContext(), "이용이 정지된 유저입니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getBaseContext(), "회원 정보를 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 에러 : " + t.getMessage());
            }
        });
    }

    private void completeLogin(int id, String email, String pw, String school, String name){
        Account.getInstance().setUserId(id);
        Account.getInstance().setUserEmail(email);
        Account.getInstance().setUserPassword(pw);
        Account.getInstance().setUserSchool(school);
        Account.getInstance().setUserName(name);

        Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
        MyFirebaseInstanceIdService fb = new MyFirebaseInstanceIdService();
        //SENDING TO SERVER THE USER TOKEN
        fb.sendRegistrationToServer(Account.getInstance().getUserId(), getSharedPreferenceToken());
        finish();
        /*****Start Service for Chatting********/
        Intent serviceIntent = new Intent(this, ChatService.class);
        startService(serviceIntent);
        /***********************************/
        startActivity(loginIntent);
    }
    public String getSharedPreferenceToken(){
        SharedPreferences pref = getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getString(TOKEN, "");
    }

    private void moveToRegistActivity(){
        Log.i("ASD", "ABS");
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public class ValidationResult{
        int userId;
        String strReceiveMessage;
        String userName;
    }

    private void setAutoLogin(int id, String email, String pw, String school, String name){
        SharedPreferences auto = getSharedPreferences("auto", 0);
        SharedPreferences.Editor editor = auto.edit();
        editor.putInt(ID, id);
        editor.putString(EMAIL, email);
        editor.putString(PASSWORD, pw);
        editor.putString(SCHOOL, school);
        editor.putString(NAME, name);
        editor.putBoolean(ISAUTOLOGIN, true);
        editor.putBoolean(CHATROOMRESTED, false);
        editor.commit();
    }
    private void autoLogin(){
        SharedPreferences auto = getSharedPreferences("auto", 0);
        boolean isAutoLogin = auto.getBoolean(ISAUTOLOGIN, false);
        if(isAutoLogin){
            int id = auto.getInt(ID, -1);
            String email = auto.getString(EMAIL, null);
            String pw = auto.getString(PASSWORD, null);
            String school = auto.getString(SCHOOL, null);
            String name = auto.getString(NAME, null);
            completeLogin(id,email,pw,school,name);
        }
        else{
            return;
        }
    }

}
