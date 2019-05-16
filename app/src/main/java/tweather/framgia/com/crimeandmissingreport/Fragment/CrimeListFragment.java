package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Activity.MainActivity;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewNewsAdapter;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewSpecialNewsAdapter;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class CrimeListFragment extends Fragment {
    public static RecyclerView mRecyclerViewSpecial, mRecyclerViewNew;
    public static RecyclerViewNewsAdapter mRecyclerViewNewsAdapter;
    public static RecyclerViewSpecialNewsAdapter mRecyclerViewSpecialNewsAdapter;
    public static ArrayList<Report> crimeReportArrayList = new ArrayList<>();
    public static ArrayList<Report> crimeSpecialReportArrayList = new ArrayList<>();
    public static String mDistrictName = "";
    LocationManager locationManager;
    NestedScrollView mNestedScrollView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Spinner mSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        initView(view);
        initSpinnerFilerEvent();
        getCrimeReportList();
        initSwipeRefreshLayout(view);
        mNestedScrollView = view.findViewById(R.id.scrollViewCrimeList);
        return view;
    }

    private void initSpinnerFilerEvent() {
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        getCrimeReportList();
                        break;
                    case 1:
                        checkLocationPermission();
                        Toast.makeText(getContext(), mDistrictName, Toast.LENGTH_SHORT).show();
                        getCrimeReportListByLocation(mDistrictName);
                        break;
                    case 2:
                        showDialogSelectArea();
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MainActivity.MY_PERMISSION_REQUEST_LOCATION);
        } else {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
                //check enable or disable location
                final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                } else {
                    locationManager.requestLocationUpdates(
                            String.valueOf(locationManager.getBestProvider(new Criteria(), true)),
                            1000, 0, (LocationListener) this);
                }
            }
        }
    }

    private String getLocation(double latitude, double longitude) {
        String cityName = "";

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dark_Dialog);
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

    private void showDialogSelectArea() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_select_area);
        final Spinner spinnerArea = dialog.findViewById(R.id.spinnerSelectArea);
        getSpinnerArea(spinnerArea);
        Button buttonSelect = dialog.findViewById(R.id.buttonSelect);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getCrimeReportListByLocation(spinnerArea.getSelectedItem().toString());
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
                new ArrayAdapter<>(getContext(), R.layout.spinner_textview,
                        arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void initView(View view) {
        mRecyclerViewSpecial = view.findViewById(R.id.recyclerViewSpecial);
        mRecyclerViewSpecial.setHasFixedSize(true);
        mRecyclerViewSpecial.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false));

        mRecyclerViewNew = view.findViewById(R.id.recyclerViewNew);
        mRecyclerViewNew.setHasFixedSize(true);
        mRecyclerViewNew.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));

        mSpinner = view.findViewById(R.id.spinnerFilter);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("All");
        arrayList.add("News From Current Location");
        arrayList.add("News From Select Location");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.spinner_filter_textview,
                        arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }

    private void initSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutCrimeList);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCrimeReportList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
    }

    private void getCrimeReportList() {
        Call<List<Report>> callList =
                APIUtils.getData(APIUtils.BASE_URL).GetCrimeReportList(APIUtils.API_GET_CRIMES_URL);
        callList.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(@NonNull Call<List<Report>> call,
                                   @NonNull Response<List<Report>> response) {
                if (response.body() != null) {
                    crimeReportArrayList = (ArrayList<Report>) response.body();

                    initRecyclerViewSpecial();
                    initRecyclerViewNew();
                } else {
                    Toast.makeText(getContext(), "No data!", Toast.LENGTH_SHORT).show();
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<List<Report>> call, @NonNull Throwable t) {
                Log.d("checkFailGetCrimeReportList", t.getMessage());
            }
        });
    }

    public void getCrimeReportListByLocation(String location) {
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
                                        getContext());
                        CrimeListFragment.mRecyclerViewNew.setAdapter(CrimeListFragment.mRecyclerViewNewsAdapter);
                    } else {
                        Toast.makeText(getContext(), "No report about this location!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                Log.d("checkFail", t.getMessage());
            }
        });
    }

    private void initRecyclerViewNew() {
        mRecyclerViewNew.setLayoutAnimation(AnimationUtils
                .loadLayoutAnimation(getContext(),
                        R.anim.layout_animation_down_to_up));
        mRecyclerViewNewsAdapter = new RecyclerViewNewsAdapter(crimeReportArrayList, getContext());
        mRecyclerViewNew.setAdapter(mRecyclerViewNewsAdapter);
        mRecyclerViewNew.scheduleLayoutAnimation();
    }

    public void initRecyclerViewSpecial() {
        mRecyclerViewSpecialNewsAdapter = new RecyclerViewSpecialNewsAdapter(crimeReportArrayList, getContext());
        mRecyclerViewSpecial.setAdapter(mRecyclerViewSpecialNewsAdapter);
    }
}
