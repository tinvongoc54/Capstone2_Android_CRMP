package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import tweather.framgia.com.crimeandmissingreport.Activity.LoginDialog;
import tweather.framgia.com.crimeandmissingreport.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_GALLERY = 101;
    public static TextView mTextViewFullName;
    public static SharedPreferences mSharedPreferences;
    public static String realPath;
    public static boolean checkFragment = false;
    File mChosenFile;
    ImageView mImageView, mImageViewCrime;
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
        mImageViewCrime = view.findViewById(R.id.imageViewCrime);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                //tạo file từ uri của data
                Log.d("checkEditProfile", "có");
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
