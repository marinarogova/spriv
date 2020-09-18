package com.spriv;

//import com.crashlytics.android.Crashlytics;
import com.spriv.utils.FontsManager;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
//import io.fabric.sdk.android.Fabric;

public class SprivApp extends Application{

    private static Context context;
    public static boolean InForground;
    
    public void onCreate(){
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
       // crashlytics.log("This is test message");


        SprivApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return SprivApp.context;
    }
    
    public static void updateActionbarText(AppCompatActivity activity)
    {
    	int titleId = 0;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
        {
            titleId = activity.getResources().getIdentifier("action_bar_title", "id", "android");
        }
        else
        {
          // This is the id is from your app's generated R class when AppCompatActivity is used for SupportActionBar

            titleId = R.id.action_bar_title;
        }

        if(titleId>0)
        {
            TextView titleView = (TextView)activity.findViewById(titleId);
            if(titleView != null)
            {
            	float leftPaddingDP = 5;
            	titleView.setTypeface(FontsManager.getInstance().getBoldFont());
            	float scale = activity.getResources().getDisplayMetrics().density;
            	int leftPaddingPixels = (int) (leftPaddingDP * scale + 0.5f);
            	titleView.setPadding(leftPaddingPixels, titleView.getPaddingTop(), titleView.getPaddingRight(), titleView.getPaddingBottom());
            }
        }
	}
}
