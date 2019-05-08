package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
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

    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_GALLERY = 101;
    public static String realPath;
    File mChosenFile;
    ImageView mImageViewCrime;
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

        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext()))
                .registerReceiver(mResfreshReceiver, new IntentFilter("updateCrimeReport"));
        reportArrayList = new ArrayList<>();
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
        mImageViewCrime = view.findViewById(R.id.imageViewCrime);

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

    BroadcastReceiver mResfreshReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            getReportList();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                //tạo file từ uri của data
                Log.d("checkEditProfileCr", "có");
                mChosenFile = new File(getRealPath(data.getData()));
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            Objects.requireNonNull(getContext()).getContentResolver(), data.getData());
                    mImageViewCrime.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) {
                mChosenFile = new File(realPath);
                mImageViewCrime.setImageURI(Uri.parse(realPath));
            }
        }
    }

    private String getRealPath(Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = Objects.requireNonNull(getActivity())
                .getContentResolver().query(contentUri, proj, null, null, null);
        if (Objects.requireNonNull(cursor).moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
        }
        cursor.close();
        return path;
    }
}
