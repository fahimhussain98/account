package wts.com.accountpe.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import wts.com.accountpe.R;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView logoSplash;
    private Animation anim;
    SharedPreferences sharedPreferences;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        init();

        int SPLASH_SCREEN_TIME_OUT = 2500; //by me


        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity.this);

        logoSplash.startAnimation(anim);

       /* anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {


                //boolean isMpinGenerated = sharedPreferences.getBoolean("mpin",false);
                boolean isMpinGenerated = sharedPreferences.getBoolean("mpin",false);
                Intent intent;
                if (isMpinGenerated){
                    intent = new Intent(getApplicationContext(),HomeDashActivity.class);

                }else {
                    intent = new Intent(getApplicationContext(),  MPINActivity.class);
                }

                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                user =  sharedPreferences.getString("username",null);
                if (user!=null){
                    startActivity(new Intent(SplashScreenActivity.this, MPINActivity.class));
                }
                else {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginNewActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);

    }

    private void init(){
        logoSplash = findViewById(R.id.ivLogoSplash);
        anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.zoom_out);
    }
}