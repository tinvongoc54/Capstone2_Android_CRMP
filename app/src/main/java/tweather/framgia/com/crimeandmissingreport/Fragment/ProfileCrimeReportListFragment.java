package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Activity.LoginDialog;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewProfileReportListAdapter;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class ProfileCrimeReportListFragment extends Fragment {

    RecyclerView mRecyclerViewReportList;
    TextView mTextViewReportListProfile;
    RecyclerViewProfileReportListAdapter recyclerViewProfileReportListAdapter;
    public static ArrayList<Report> reportArrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_report_list, container, false);
        reportArrayList = new ArrayList<>();

        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext()))
                .registerReceiver(mMessageReceiver, new IntentFilter("deleteCrimeReport"));
        initView(view);
        getReportList();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ProfileFragment.checkFragment = true;
    }

    private void initView(View view) {
        mTextViewReportListProfile = view.findViewById(R.id.textViewCrimeReportListProfile);

        mRecyclerViewReportList = view.findViewById(R.id.recyclerViewCrimeReportListProfile);
        mRecyclerViewReportList.setHasFixedSize(true);
        mRecyclerViewReportList.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));
    }

    private void initRecyclerView() {
        recyclerViewProfileReportListAdapter =
                new RecyclerViewProfileReportListAdapter(reportArrayList, getContext());
        mRecyclerViewReportList.setAdapter(recyclerViewProfileReportListAdapter);
    }

    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("delete", false)) {
                getReportList();
            }
        }
    };

    private void getReportList() {
        Call<List<Report>> callList = APIUtils.getData(APIUtils.BASE_URL)
                .GetReportListOfUser(
                        APIUtils.API_UPDATE_USER_URL + ProfileFragment.mSharedPreferences.getInt(
                                LoginDialog.SHAREDPREFERENCES_ID_USER, 10000) + "/crimes");
        callList.enqueue(new Callback<List<Report>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<List<Report>> call,
                    @NonNull Response<List<Report>> response) {
                if (response.body() != null) {
                    reportArrayList = (ArrayList<Report>) response.body();
                    initRecyclerView();
                }
                mTextViewReportListProfile.setText(
                        "Crime Report List (" + reportArrayList.size() + ")");
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<List<Report>> call, @NonNull Throwable t) {
                Log.d("checkFailGetCrimeReportList", t.getMessage());
            }
        });
    }
}
