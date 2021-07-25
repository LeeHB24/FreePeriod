package xyz.capsaicine.freeperiod.activities.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ReportItem;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.NetworkService;

import static xyz.capsaicine.freeperiod.app.App.retrofit;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener{

    int partyId;
    int report_targer;
    EditText edit_content;
    Button btn_report_accept, btn_report_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        partyId = getIntent().getIntExtra(ChatDBCtrct.roomId, -1);
        report_targer = getIntent().getIntExtra(ChatDBCtrct.userID, -1);
        setContentView(R.layout.activity_report);
        edit_content = findViewById(R.id.edit_report_content);
        btn_report_accept = findViewById(R.id.btn_report_accept);
        btn_report_cancel = findViewById(R.id.btn_report_cancel);
        btn_report_accept.setOnClickListener(this);
        btn_report_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.btn_report_accept :{
                if(checkValid())
                    setBtn_report_accept(getReviewItem());
                else
                    Toast.makeText(this, "잘못된 접근입니다. 재접속해주세요", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btn_report_cancel :{
                finish();
                break;
            }
        }

    }
    private ReportItem getReviewItem(){
        ReportItem item  = new ReportItem();
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        item.setContent(edit_content.getText().toString());
        item.setPartyId(partyId);
        item .setReport_writer(Account.getInstance().getUserId());
        item.setReport_target(report_targer);
        item.setTimestamp(date.toString());
        return item;
    }


    private void setBtn_report_accept(ReportItem item){
        NetworkService networkService = retrofit.create(NetworkService.class);
        Call<Integer> sendReport = networkService.sendReport(item);
        sendReport.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Response<Integer> response, Retrofit retrofit) {
                Integer result = response.body();
                    Toast.makeText(getBaseContext(),"신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
            }
            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getBaseContext(),"신고작성 오류.다시시도해주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private boolean checkValid(){
        if(partyId!=-1 && report_targer !=-1)
            return true;
        else
            return false;
    }
}
