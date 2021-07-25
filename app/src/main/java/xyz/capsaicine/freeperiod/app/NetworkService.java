package xyz.capsaicine.freeperiod.app;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import xyz.capsaicine.freeperiod.Model.ChatRoomInDatabase;
import xyz.capsaicine.freeperiod.Model.Lecture;
import xyz.capsaicine.freeperiod.Model.PartyInDatabase;
import xyz.capsaicine.freeperiod.Model.User;

import xyz.capsaicine.freeperiod.activities.chat.ChattingMapActivity;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatterItem;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ReportItem;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ReviewItem;

import xyz.capsaicine.freeperiod.activities.chat.WriteReviewActivity;

import xyz.capsaicine.freeperiod.activities.home.HomeActivity;
import xyz.capsaicine.freeperiod.activities.login.LoginActivity;
import xyz.capsaicine.freeperiod.activities.login.RegisterActivity;
import xyz.capsaicine.freeperiod.activities.mypage.MyPageActivity;

public interface NetworkService {
    @POST("/register")
    Call<RegisterActivity.RegisterResult> registerUser(@Body User user);

    @GET("/login")
    Call<LoginActivity.ValidationResult> validateUser(@Query("email") String id, @Query("password") String password);

    @GET("/send")
    Call<User> sendEmail(@Query("email") String email);

    @GET("/lectures") // 모든 강의의 정보를 받아온다
    Call<List<Lecture>> getAllLectures(@Query("user_id") int userId);

    @GET("/user_lecture_id")
    Call<List<Integer>> getUserLectureIds(@Query("user_id") int userId);

    @POST("/new_party")
    Call<DB.PartyGetter> registerNewParty(@Query("user_id") int userId, @Body PartyInDatabase party);

    //추후 시작시간에 따라 불러오는 기능 구현
    @GET("/get_partylist")
    Call<ArrayList<PartyInDatabase>> getPartyList();

    @GET("/get_chatroomlist")
    Call<ArrayList<ChatRoomInDatabase>> getChatroomList(@Query("user_id")int userId);

    @POST("/join_party")
    Call<DB.PartyJoinResult> joinParty(@Query("user_id")int userId, @Query("party_id") int partyId);

    @POST("/update_lecture")
    Call<HomeActivity.UpdateResult> updateLecture(@Query("user_id")int userId, @Query("lecture_id")int lectureId, @Query("flag")String flag);

    @GET("/timetable_parties")
    Call<ArrayList<PartyInDatabase>> getUserParties(@Query("user_id")int userId);

    @POST("/set_user_token")
    Call<Integer> postUserToken(@Query("user_id")int userId, @Query("user_token")String token);

    @GET("/get_chatroom_userlist")
    Call<ArrayList<ChatterItem>> getChatRoomUserList(@Query("room_id")int roomId);

    @GET("/my_review")
    Call<ArrayList<ReviewItem>> getMyReviewList(@Query("user_id") int userId);

    @GET("/get_user_rate")
    Call<Float> getUserRate(@Query("user_id")int userId);

    @POST("/write_review")
    Call<WriteReviewActivity.reviewInfo>sendReview(@Body ReviewItem review);

    @POST("/leave_chat_room")
    Call<Integer> postLeaveRoom(@Query("user_id")int userId, @Query("room_id") int roomId);

    @POST("/response_party_request")
    Call<Integer> postAcceptance(@Query("user_id")int userId, @Query("party_id") int partyId, @Query("acceptance") boolean acceptance);

    @GET("/get_waiting_party_list")
    Call<ArrayList<PartyInDatabase>> getWaitingPartyList(@Query("user_id")int userId);

    @GET("/get_request_list")
    Call<ArrayList<MyPageActivity.userDBRequest>> getRequestList(@Query("user_id")int userId);

    @POST("/cancel_join_party")
    Call<Integer> postCancelJoinParty(@Query("user_id")int userId, @Query("party_id")int partyId);

    @GET("get_joined_party_list")
    Call<ArrayList<PartyInDatabase>> getJoinedPartyList(@Query("user_id")int userId);

    @POST("post_appointment_point")
    Call<Integer> postAppointmentPoint(@Query("room_id")int roomId, @Query("latitude")double latitude, @Query("longitude")double longitude);

    @GET("get_appointment_point")
    Call<ChattingMapActivity.appointmentPoint> getAppointmentPoint(@Query("room_id")int roomId);

    @POST("write_report")
    Call<Integer> sendReport(@Body ReportItem item);

    @POST("close_party")
    Call<Integer> closeParty(@Query("party_id")int partyId);

    @POST("report_review")
    Call<Integer> reportReview(@Query("review_index")int reviewIndex);

}
