package tweather.framgia.com.crimeandmissingreport.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewNewsAdapter;
import tweather.framgia.com.crimeandmissingreport.Fragment.CrimeFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.CrimeListFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.MissingPersonFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.ProfileFragment;
import tweather.framgia.com.crimeandmissingreport.Object.Hotline;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    public static final int MY_PERMISSION_REQUEST_LOCATION = 1;
    public static final int MY_PERMISSION_REQUEST_CALL_PHONE = 2;
    public static final int MY_PERMISSION_REQUEST_SMS = 4;
    public static String mDistrictName = "";
    LocationManager locationManager;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        statusCheck();
        printKey();
    }

    private void printKey() {
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo infor = getPackageManager()
                    .getPackageInfo("tweather.framgia.com.crimeandmissingreport",
                            PackageManager.GET_SIGNATURES);
            for (Signature signature : infor.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("SHAKEY", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
            case R.id.buttonCallHotline:
                checkLocationPermission();
                checkCallPhonePermission();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.currentLocation:
                checkLocationPermission();
                statusCheck();
                break;
            case R.id.selectLocation:
                showDialogSelectArea();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogSelectArea() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_select_area);
        final Spinner spinnerArea = dialog.findViewById(R.id.spinnerSelectArea);
        getSpinnerArea(spinnerArea);
        Button buttonSelect = dialog.findViewById(R.id.buttonSelect);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callRequestFilter(spinnerArea.getSelectedItem().toString());
            }
        });
        dialog.show();
    }

    private void getSpinnerArea(Spinner spinner) {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Cẩm Lệ");
        arrayList.add("Hải Châu");
        arrayList.add("Liên Chiểu");
        arrayList.add("Thanh Khê");
        arrayList.add("Sơn Trà");
        arrayList.add("Ngũ Hành Sơn");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.spinner_textview,
                        arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                try {
//                mDistrictName = getLocation(16.06117575, 108.22018544);
                    mDistrictName = getLocation(location.getLatitude(), location.getLongitude());
                } catch (Exception e) {
                    e.printStackTrace();
                    mDistrictName = "";
                }
            } else {
                locationManager.requestLocationUpdates(
                        String.valueOf(locationManager.getBestProvider(new Criteria(), true)),
                        1000, 0, this);
            }
        }
    }

    private void checkCallPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSION_REQUEST_CALL_PHONE);
        } else {
            statusCheck();
            showDialogCallHotline();
        }
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            callRequestFilter(mDistrictName);
        }
    }

    public void callRequestFilter(String location) {
        Call<List<Report>> call = APIUtils.getData(APIUtils.BASE_URL)
                .GetCrimesByArea(APIUtils.API_GET_CRIMES_BY_AREA + location);
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        CrimeListFragment.crimeReportArrayList = (ArrayList<Report>) response.body();
                        CrimeListFragment.mRecyclerViewNewsAdapter =
                                new RecyclerViewNewsAdapter(CrimeListFragment.crimeReportArrayList,
                                        MainActivity.this);
                        CrimeListFragment.mRecyclerViewNew.setAdapter(CrimeListFragment.mRecyclerViewNewsAdapter);
                    } else {
                        Toast.makeText(MainActivity.this, "No report about this location!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                Log.d("checkFail", t.getMessage());
            }
        });
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
        builder.setMessage("Your location seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDialogCallHotline() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
        builder.setMessage("Do you want to call the police hotline for this location?")
                .setCancelable(false)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callHotline();
                    }
                })
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Question");
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    private void callHotline() {
        Toast.makeText(this, mDistrictName, Toast.LENGTH_SHORT).show();
        Call<List<Hotline>> call = APIUtils.getData(APIUtils.BASE_URL)
                .GetHotlineByLocation(APIUtils.API_GET_HOTLINE_BY_LOCATION_URL
                        + mDistrictName.toLowerCase().replace(" ", "-"));
        call.enqueue(new Callback<List<Hotline>>() {
            @Override
            public void onResponse(@NonNull Call<List<Hotline>> call, @NonNull Response<List<Hotline>> response) {
                if (response.body() != null) {
                    if (response.body().size() > 0) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + Objects.requireNonNull(response.body()).get(0).getHotlineNumber()));
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "No hotline at this location!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Hotline>> call, @NonNull Throwable t) {
                Log.d("checkFail", t.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        locationManager = (LocationManager)
                                getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try {
                            mDistrictName = getLocation(location.getLatitude(), location.getLongitude());
                        } catch (Exception e) {
                            e.printStackTrace();
                            mDistrictName = "";
                        }
                    }
                } else {
                    Toast.makeText(this, "Not permission granted!", Toast.LENGTH_SHORT).show();
                }
                break;
            case MY_PERMISSION_REQUEST_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        showDialogCallHotline();
                    }
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private String getLocation(double latitude, double longitude) {
        String cityName = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList.size() > 0) {
                cityName = addressList.get(0).getSubAdminArea();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences(LoginDialog.SHAREDPREFERENCES, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(LoginDialog.SHAREDPREFERENCES_REMEMBER_LOGIN, false)) {
            sharedPreferences.edit().clear().apply();
            LoginDialog.isLogged = false;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Crime Report");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, new CrimeFragment())
                .commit();

        findViewById(R.id.buttonCrime).setOnClickListener(this);
        findViewById(R.id.buttonMissingPerson).setOnClickListener(this);
        findViewById(R.id.buttonProfile).setOnClickListener(this);
        findViewById(R.id.buttonCallHotline).setOnClickListener(this);

        if (getSharedPreferences(LoginDialog.SHAREDPREFERENCES, MODE_PRIVATE)
                .contains(LoginDialog.SHAREDPREFERENCES_EMAIL)) {
            LoginDialog.isLogged = true;
        }
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
        if (getSharedPreferences(LoginDialog.SHAREDPREFERENCES, MODE_PRIVATE).contains(LoginDialog.SHAREDPREFERENCES_EMAIL)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new ProfileFragment())
                    .commit();
        } else {
            Toast.makeText(this, "You are not logged in!", Toast.LENGTH_SHORT).show();
            new LoginDialog(MainActivity.this).clickButtonProfile();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        mDistrictName = getLocation(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
