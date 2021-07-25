package xyz.capsaicine.freeperiod.activities.chat;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.app.App;
import xyz.capsaicine.freeperiod.app.NetworkService;

public class ChattingMapActivity extends AppCompatActivity implements MapView.MapViewEventListener,MapView.POIItemEventListener{

    Button btn_share, btn_returnToChat;
    MapPOIItem marker;
    MapView mapView;
    ViewGroup mapViewContainer;

    //Dialog Notify Message
    private Button btn_cancel_yes;
    private Button btn_cancel_no;
    private Dialog cancelRequestDialog;

    private double ajouLatitude = 37.283133;
    private double ajouLongitude = 127.044850;
    int roomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_map);
        setAppointmentChangeDialogWindow();
        //이부분 나중에 로그인할때로 옮겨야함
        getAppKeyHash();

        roomId = getIntent().getIntExtra(ChatDBCtrct.roomId, -1);
        mapView = new MapView(this);
        mapView.setDaumMapApiKey("7b275b621f842f710cf86a6c8642bda9");
        getAppointmentPoint(roomId);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        btn_returnToChat = findViewById(R.id.btn_returnToChat);
        btn_share = findViewById(R.id.btn_share);
        btn_returnToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAppointmentChangeDialog();
            }
        });
        marker=new MapPOIItem();
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        marker.setItemName("약속 장소");
        marker.setTag(0);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
    }
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }
    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        mapView.removePOIItem(marker);
        marker.setMapPoint(mapPoint);
        mapView.addPOIItem(marker);
    }
    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    public class appointmentPoint{
        double latitude;
        double longitude;
    }

    private void  getAppointmentPoint(int roomId){
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        Call<appointmentPoint> getAppointmentPoint = networkService.getAppointmentPoint(roomId);
        getAppointmentPoint.enqueue(new Callback<appointmentPoint>() {
            @Override
            public void onResponse(Response<appointmentPoint> response, Retrofit retrofit) {
                appointmentPoint appPoint = response.body();
                try{
                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(appPoint.latitude, appPoint.longitude);
                    mapView.setMapCenterPoint(mapPoint,true);
                    marker.setMapPoint(mapPoint);
                    mapView.addPOIItem(marker);
                }catch(Exception e){
                    Toast.makeText(ChattingMapActivity.this, "약속 장소를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(ajouLatitude, ajouLongitude);
                    mapView.setMapCenterPoint(mapPoint,true);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(ChattingMapActivity.this, "약속 장소를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(ajouLatitude, ajouLongitude);
                mapView.setMapCenterPoint(mapPoint,true);

            }
        });
    }
    private void  postAppointmentPoint(int roomId, double latitude, double longitude){
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        Call<Integer> postAppointmentPoint = networkService.postAppointmentPoint(roomId, latitude, longitude);
        postAppointmentPoint.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Response<Integer> response, Retrofit retrofit) {
                Toast.makeText(ChattingMapActivity.this, "약속 장소가 설정되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(ChattingMapActivity.this, "약속 장소 설정에 실패했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAppointmentChangeDialogWindow(){
        cancelRequestDialog = new Dialog(this);
        cancelRequestDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    private void showAppointmentChangeDialog(){
        cancelRequestDialog.setContentView(R.layout.chat_mappoint_dialog);
        btn_cancel_yes = cancelRequestDialog.findViewById(R.id.btn_cancel_yes);
        btn_cancel_no = cancelRequestDialog.findViewById(R.id.btn_cancel_no);
        btn_cancel_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRequestDialog.dismiss();
            }
        });
        btn_cancel_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRequestDialog.dismiss();
                postAppointmentPoint(roomId, marker.getMapPoint().getMapPointGeoCoord().latitude, marker.getMapPoint().getMapPointGeoCoord().longitude);
            }
        });
        cancelRequestDialog.show();
    }

}

