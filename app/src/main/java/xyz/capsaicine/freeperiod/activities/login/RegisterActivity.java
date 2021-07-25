package xyz.capsaicine.freeperiod.activities.login;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.capsaicine.freeperiod.Model.User;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.app.App;
import xyz.capsaicine.freeperiod.app.NetworkService;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText idText, passwordText, schoolText, nameText, pwcheckText;
    String sId, sPw, sPwcheck, sSchool, sName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idText = (EditText) findViewById(R.id.idText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        pwcheckText = (EditText) findViewById(R.id.pwcheckText);
        schoolText = (EditText) findViewById(R.id.schoolText);
        nameText = (EditText) findViewById(R.id.nameText);
        Button registerButton = (Button) findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkService networkService = App.retrofit.create(NetworkService.class);

                String id = idText.getText().toString();
                String password = passwordText.getText().toString();
                String school = schoolText.getText().toString();
                String name = nameText.getText().toString();

                User user = new User(id, password, school, name);
                String email = id.substring(id.length() - 11, id.length());
                if (!email.equals("@ajou.ac.kr")) {
                    Toast.makeText(getApplicationContext(), "메일을 잘 입력해 주십시오.", Toast.LENGTH_SHORT).show();
                } else {
                    Call<RegisterResult> registerResultCall = networkService.registerUser(user);
                    registerResultCall.enqueue(new Callback<RegisterResult>() {
                        @Override
                        public void onResponse(Response<RegisterResult> response, Retrofit retrofit) {
                            RegisterResult registerResult = response.body();
                            if (registerResult.message.equals("success")) {
                                Toast.makeText(getBaseContext(), "회원으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                getAuthentication(id);

                            } else {
                                Toast.makeText(getBaseContext(), "회원정보를 다시 입력해 주십시오.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.i("MyTag", "서버 에러입니다 : " + t.getMessage());
                        }
                    });
                }
            }
        });
    }

    //회원가입 성공 여부
    public class RegisterResult {
        String message;
    }

    public void getAuthentication(String email){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("메일 인증을 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NetworkService networkService = App.retrofit.create(NetworkService.class);

                Toast.makeText(getBaseContext(), "인증 메일을 확인해 주십시오.", Toast.LENGTH_SHORT).show();
                Call<User> authenticationCall = networkService.sendEmail(email);
                authenticationCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Response<User> response, Retrofit retrofit) {

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("MyTag", "서버 에러입니다 : " + t.getMessage());
                    }
                });
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

}