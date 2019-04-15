package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Objects;
import tweather.framgia.com.crimeandmissingreport.Activity.LoginDialog;
import tweather.framgia.com.crimeandmissingreport.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    ImageView mImageView;
    public static TextView mTextViewFullName;
    public static SharedPreferences mSharedPreferences;
    public static boolean checkFragment = false;
    NestedScrollView mNestedScrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mNestedScrollView = view.findViewById(R.id.nestedScrollViewProfile);

        mSharedPreferences = Objects.requireNonNull(getContext())
                .getSharedPreferences(LoginDialog.SHAREDPREFERENCES, Context.MODE_PRIVATE);

        mImageView = view.findViewById(R.id.imageViewAvatar);
        mTextViewFullName = view.findViewById(R.id.textViewNameUser);
        mTextViewFullName.setText(mSharedPreferences.getString(LoginDialog.SHAREDPREFERENCES_FULLNAME, ""));

        view.findViewById(R.id.imageButtonPersonalInformation).setOnClickListener(this);
        view.findViewById(R.id.imageButtonCrimeReportList).setOnClickListener(this);
        view.findViewById(R.id.imageButtonMissingReportList).setOnClickListener(this);

        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayoutProfile, new ProfilePersonalInformationFragment())
                .commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButtonPersonalInformation:
                initPersonalInformationLayout();
                break;
            case R.id.imageButtonCrimeReportList:
                initCrimeReportListLayout();
                break;
            case R.id.imageButtonMissingReportList:
                initMissingReportListLayout();
                break;
        }
    }

    private void initMissingReportListLayout() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutProfile, new ProfileMissingReportListFragment())
                .commit();
    }

    private void initCrimeReportListLayout() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutProfile, new ProfileCrimeReportListFragment())
                .commit();
    }

    private void initPersonalInformationLayout() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutProfile, new ProfilePersonalInformationFragment())
                .commit();
    }
}
