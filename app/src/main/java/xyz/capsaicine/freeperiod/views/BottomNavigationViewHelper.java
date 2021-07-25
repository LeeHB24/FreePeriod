package xyz.capsaicine.freeperiod.views;

import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;

import xyz.capsaicine.freeperiod.app.App;

public class BottomNavigationViewHelper {
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public static void removeTitle(BottomNavigationView view){
        BottomNavigationMenuView menuView = (BottomNavigationMenuView)view.getChildAt(0);
        for(int i = 0; i < menuView.getChildCount(); i++){
            BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
            View iconView = item.findViewById(android.support.design.R.id.icon);
            ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            DisplayMetrics displayMetrics = App.getAppContext().getResources().getDisplayMetrics();
            layoutParams.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, displayMetrics);
            layoutParams.width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }
    }
}
