package xyz.capsaicine.freeperiod.app;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.capsaicine.freeperiod.Model.Lecture;
import xyz.capsaicine.freeperiod.Model.Party;
import xyz.capsaicine.freeperiod.Model.PartyInDatabase;
import xyz.capsaicine.freeperiod.Model.User;


public class DB {

    private static NetworkService networkService = App.retrofit.create(NetworkService.class);

    private static ArrayList<Party> partyList = new ArrayList<Party>();
    private static ArrayList<Party> recruitMealPartyList = new ArrayList<>();
    private static ArrayList<Party> recruitPlayPartyList = new ArrayList<>();

    private static Calendar startOfWeek;
    private static Calendar endOfWeek;
    private static Calendar todayCalendar;
    private static Calendar after7dayCalendar;

    // TODO - fanta: remove this debuging variables when server will be able to connect with ip
    final public static String adminId = "admin";
    final public static String adminPassword = "a";

    /* -----------------------------------
        for singleton DB class
     ----------------------------------- */
    private DB(){}

    private static class LazyHolder{
        public static final DB INSTANCE = new DB();
    }

    public static DB getInstance(){
        return LazyHolder.INSTANCE;
    }
    // end of singleton setting

    public class PartyGetter{
        int partyId;
    }

    /* -------------------------------------
        API about Party
    ------------------------------------- */
    public int postNewParty(Party newParty){
        int partyId = -1;

        PartyInDatabase newPartyDB = new PartyInDatabase(newParty);
        Call<PartyGetter> registerPartyCall = networkService.registerNewParty(Account.getInstance().getUserId(), newPartyDB);

        registerPartyCall.enqueue(new Callback<PartyGetter>() {
            @Override
            public void onResponse(Response<PartyGetter> response, Retrofit retrofit) {

            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 에러입니다 : " + t.getMessage());
            }
        });
        return partyId;
    }

    /* -------------------------------------
        API about Party
    ------------------------------------- */
    public Party getPartyById(int partyId) throws IOException {
        // TODO - fanta: remove this samples, put real party value
        int sampleId = 1;
        Party.Type sampleType = Party.Type.Meal;
        Party.Status sampleStatus = Party.Status.Recruiting;
        String sampleCoverURL = "https://static.vecteezy.com/system/resources/previews/000/095/280/original/red-low-poly-vector.jpg";
        String sampleName = "name_sample";
        ArrayList<String> sampleTagList = new ArrayList<>();
        int capacityMax = 4;
        int capacityCurrent = 1;
        Date sampleStartTime = Calendar.getInstance().getTime();
        Date sampleEndTime = Calendar.getInstance().getTime();
        Date sampleCloseTime = Calendar.getInstance().getTime();

        return new Party(
                sampleId,
                sampleType,
                sampleStatus,
                null,
                sampleName,
                sampleTagList,
                capacityMax,
                capacityCurrent,
                sampleStartTime,
                sampleEndTime,
                sampleCloseTime
        );
    }

    @SuppressLint("StaticFieldLeak")
    public void updatePartyList(){
        ArrayList<Party> newPartyList = new ArrayList<>();
        ArrayList<Party> newRecruitMealPartyList = new ArrayList<>();
        ArrayList<Party> newRecruitPlayPartyList = new ArrayList<>();
        ArrayList<PartyInDatabase> partyInDatabaseList = new ArrayList<>();
        try {
            partyInDatabaseList = new AsyncTask<Void, Void, ArrayList<PartyInDatabase>>(){
                @Override
                protected ArrayList<PartyInDatabase> doInBackground(Void... voids) {
                    try {
                        return networkService.getPartyList().execute().body();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        for(PartyInDatabase partyInDatabase : partyInDatabaseList){
            newPartyList.add(new Party(partyInDatabase));
        }
        Collections.sort(newPartyList, new Comparator<Party>() {
            @Override
            public int compare(Party o1, Party o2) {
                if(o1.getPartyStartTime().compareTo(o2.getPartyStartTime()) != 0){
                    return o1.getPartyStartTime().compareTo(o2.getPartyStartTime());
                }
                else if(o1.getPartyEndTime().compareTo(o2.getPartyEndTime()) != 0){
                    return o1.getPartyEndTime().compareTo(o2.getPartyEndTime());
                }
                else{
                    return o1.getPartyCloseTime().compareTo(o2.getPartyCloseTime());
                }
            }
        });

        for(Party party : newPartyList){
            if(party.getPartyStatus() != Party.Status.Recruiting ||
                    party.getPartyStartTime().before(Calendar.getInstance().getTime())){
                continue;
            }
            if(party.getPartyType() == Party.Type.Meal){
                newRecruitMealPartyList.add(party);
            }
            else{
                newRecruitPlayPartyList.add(party);
            }
        }
        partyList = newPartyList;
        recruitMealPartyList = newRecruitMealPartyList;
        recruitPlayPartyList = newRecruitPlayPartyList;
    }

    public int getPartyLeaderIdByPartyId(int partyId){
        int leaderId = -1;
        return leaderId;
    }
    /* end of Party qeury */

    /* -------------------------------------
        API about User
    ------------------------------------- */
    public User getUserById(int userId){
        // TODO - fanta: remove this samples, put real user value
        int sampleId = 1;
        String sampleEmail = "email_str";
        String samplePw = "password_str";
        String sampleUniv = "univ_str";
        String sampleName = "name_str";
        int sampleReportedCount = 0;
        float sampleRatingAverage = 0;
        ArrayList<Lecture> sampleLectureList = new ArrayList<Lecture>();
        User.Auth sampleAuth = User.Auth.Accepted;

        return new User(sampleId,
                sampleEmail,
                samplePw,
                sampleUniv,
                sampleName,
                sampleReportedCount,
                sampleRatingAverage,
                sampleLectureList,
                sampleAuth);
    }

    public int getUserIdByEmail(String userEmail){
        // TODO - fanta: remove this samples, get user id by user email
        int userId = -1;
        return userId;
    }
    /* end of User query */

    public void joinToParty(int userId, int partyId){
        Call<PartyJoinResult> joinPartyCall = networkService.joinParty(userId, partyId);
        joinPartyCall.enqueue(new Callback<PartyJoinResult>() {
            @Override
            public void onResponse(Response<PartyJoinResult> response, Retrofit retrofit) {
                PartyJoinResult partyJoinResult = response.body();

                if(partyJoinResult.joinResult == 1){
                    Toast.makeText(App.getAppContext(), "모임 참가 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(partyJoinResult.joinResult == 2){
                    Toast.makeText(App.getAppContext(), "이미 들어가거나 신청중인 모임입니다.", Toast.LENGTH_SHORT).show();
                }
                else if(partyJoinResult.joinResult == 3){
                    Toast.makeText(App.getAppContext(), "파티에 참가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.i("신청 중", "신청 불가!");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "서버 에러입니다 : " + t.getMessage());
            }
        });
    }

    public Calendar getStartOfWeekCalendar(){
        startOfWeek = Calendar.getInstance();
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return startOfWeek;
    }

    public Calendar getEndOfWeekCalendar(){
        endOfWeek = Calendar.getInstance();
        endOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        return endOfWeek;
    }

    public Calendar getTodayCalendar(){
        todayCalendar = Calendar.getInstance();
        return todayCalendar;
    }

    public static Calendar getAfter7dayCalendar(){
        after7dayCalendar = Calendar.getInstance();
        after7dayCalendar.add(Calendar.DAY_OF_YEAR, 7);
        return after7dayCalendar;
    }

    public int postUserToken(int userId, String token){
        Call<Integer> setUserToken = networkService.postUserToken(userId, token);
        setUserToken.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Response<Integer> response, Retrofit retrofit) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
        return userId;
    }
    public int postCloseParty(int partyId){
        Call<Integer> postBlockParty = networkService.closeParty(partyId);
        postBlockParty.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Response<Integer> response, Retrofit retrofit) {
                Toast.makeText(App.getAppContext(), "모집이 마감되었습니다", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(App.getAppContext(), "모집 마감 실패", Toast.LENGTH_SHORT).show();
            }
        });
        return partyId;
    }
    public int postReportReview(int reviewIndex){
        Call<Integer> postReportReview = networkService.reportReview(reviewIndex);
        postReportReview.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Response<Integer> response, Retrofit retrofit) {
                Toast.makeText(App.getAppContext(), "신고 성공", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(App.getAppContext(), "신고 실패", Toast.LENGTH_SHORT).show();
            }
        });
        return reviewIndex;
    }

    /* -------------------------------------
        Getter for Party instances
    ------------------------------------- */
    public ArrayList<Party> getPartyList() {
        return partyList;
    }


    //모임 신청의 결과를 나타내는 클래스
    public class PartyJoinResult{
        int joinResult;
    }

    public ArrayList<Party> getRecruitMealPartyList() {
        return recruitMealPartyList;
    }

    public ArrayList<Party> getRecruitPlayPartyList() {
        return recruitPlayPartyList;
    }
}
