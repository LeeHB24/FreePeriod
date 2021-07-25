package xyz.capsaicine.freeperiod.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.ChatActivity;
import xyz.capsaicine.freeperiod.activities.home.HomeActivity;
import xyz.capsaicine.freeperiod.activities.meal.MealActivity;
import xyz.capsaicine.freeperiod.activities.meal.PlayActivity;
import xyz.capsaicine.freeperiod.activities.mypage.MyPageActivity;
import xyz.capsaicine.freeperiod.views.BottomNavigationViewHelper;


public class PivotActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private PivotActivity activityCurrent;
    private BottomNavigationView navBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setPivotActivity(PivotActivity activityCurrent, BottomNavigationView navBottom){
        this.activityCurrent = activityCurrent;
        this.navBottom = navBottom;
        BottomNavigationViewHelper.disableShiftMode(navBottom);
        BottomNavigationViewHelper.removeTitle(navBottom);
        navBottom.setOnNavigationItemSelectedListener(this);
        navBottom.setSelectedItemId(findPivotIdByClass(activityCurrent.getClass()));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Class classNext = findPivotClassById(item.getItemId());
        if(classNext.equals(activityCurrent.getClass())){
            return true;
        }
        Intent intent = new Intent(activityCurrent, classNext);
        finish();
        startActivity(intent);
        return true;
    }


    /* ---------------------------------------
        utility for pivot class and pivot id
    -----------------------------------------*/
    public static Class findPivotClassById(int idPivot){
        if(idPivot == R.id.pivot_home) return HomeActivity.class;
        else if(idPivot == R.id.pivot_meal) return MealActivity.class;
        else if(idPivot == R.id.pivot_play) return PlayActivity.class;
        else if(idPivot == R.id.pivot_chat) return ChatActivity.class;
        else if(idPivot == R.id.pivot_mypage) return MyPageActivity.class;
        return null;
    }

    public static int findPivotIdByClass(Class classPivot){
        if(classPivot.equals(HomeActivity.class)) return R.id.pivot_home;
        else if(classPivot.equals(MealActivity.class)) return R.id.pivot_meal;
        else if(classPivot.equals(PlayActivity.class)) return R.id.pivot_play;
        else if(classPivot.equals(ChatActivity.class)) return R.id.pivot_chat;
        else if(classPivot.equals(MyPageActivity.class)) return R.id.pivot_mypage;
        return -1;
    }
}
