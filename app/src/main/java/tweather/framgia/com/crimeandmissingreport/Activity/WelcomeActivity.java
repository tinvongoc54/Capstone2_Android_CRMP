package tweather.framgia.com.crimeandmissingreport.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import tweather.framgia.com.crimeandmissingreport.R;

public class WelcomeActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainScreen = new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(mainScreen);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
