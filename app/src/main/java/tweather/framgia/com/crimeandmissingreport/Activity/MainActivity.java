package tweather.framgia.com.crimeandmissingreport.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import tweather.framgia.com.crimeandmissingreport.Fragment.CrimeFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.MissingPersonFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.ProfileFragment;
import tweather.framgia.com.crimeandmissingreport.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crime_list_screen, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonCrime:
                initCrimeLayout();
                break;
            case R.id.buttonMissingPerson:
                initMissingPersonLayout();
                break;
            case R.id.buttonProfile:
                initProfileLayout();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        Log.d("checkRemember2", String.valueOf(sharedPreferences.getBoolean("save", false)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        Log.d("checkRemember2", String.valueOf(sharedPreferences.getBoolean("save", false)));
        if (!sharedPreferences.getBoolean("save", false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("email");
            editor.remove("password");
            editor.remove("save");
            editor.remove("idUser");
            editor.remove("fullName");
            editor.remove("phoneNumber");
            editor.apply();
            LoginDialog.isLogged = false;
        }
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, new CrimeFragment())
                .commit();

        findViewById(R.id.buttonCrime).setOnClickListener(this);
        findViewById(R.id.buttonMissingPerson).setOnClickListener(this);
        findViewById(R.id.buttonProfile).setOnClickListener(this);
    }

    private void initCrimeLayout() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new CrimeFragment())
                .commit();
    }

    private void initMissingPersonLayout() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new MissingPersonFragment())
                .commit();
    }

    private void initProfileLayout() {
        if (LoginDialog.isLogged) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new ProfileFragment())
                    .commit();
        } else {
            Toast.makeText(this, "You are not logged in!", Toast.LENGTH_SHORT).show();
            new LoginDialog(MainActivity.this).clickButtonProfile();
        }
    }
}
