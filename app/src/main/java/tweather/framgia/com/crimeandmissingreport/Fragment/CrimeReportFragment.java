package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Activity.LoginDialog;
import tweather.framgia.com.crimeandmissingreport.Activity.MainActivity;
import tweather.framgia.com.crimeandmissingreport.Object.CrimeCategory;
import tweather.framgia.com.crimeandmissingreport.Object.ImageResponse;
import tweather.framgia.com.crimeandmissingreport.Object.NotificationHelper;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class CrimeReportFragment extends Fragment implements LocationListener {

    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_GALLERY = 101;
    public static String mLocationName;
    Spinner mSpinnerCategory, mSpinnerArea;
    RadioButton mRadioButtonPresentLocation, mRadioButtonSelectLocation;
    EditText mEditTextTitle, mEditTextDescription;
    ImageView mImageViewCrime;
    Button mButtonSelectImage, mButtonPost;
    NestedScrollView mNestedScrollView;
    File mChosenFile;
    String realPath;
    LocationManager mLocationManager;
    private ProgressDialog mProgressDialog = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_report, container, false);
        initView(view);
        initEvent();
        getSpinnerCrimeCategoryList();
        getSpinnerArea();
        EnableRuntimePermission();
        return view;
    }

    private void initView(View view) {
        mSpinnerCategory = view.findViewById(R.id.spinnerCategory);
        mRadioButtonPresentLocation = view.findViewById(R.id.radioPresentLocation);
        mRadioButtonSelectLocation = view.findViewById(R.id.radioSelectLocation);
        mSpinnerArea = view.findViewById(R.id.spinnerArea);
        mSpinnerArea.setEnabled(false);
        mEditTextTitle = view.findViewById(R.id.editTextTitle);
        mEditTextDescription = view.findViewById(R.id.editTextDescription);
        mButtonSelectImage = view.findViewById(R.id.buttonSelectImage);
        mImageViewCrime = view.findViewById(R.id.imageViewCrime);
        mButtonPost = view.findViewById(R.id.buttonPost);
        mNestedScrollView = view.findViewById(R.id.nestedScrollView);
    }

    private void initEvent() {
        mRadioButtonPresentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusCheck();
                checkPermissionAndGetLocationName();
            }
        });
        mRadioButtonSelectLocation.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            mSpinnerArea.setEnabled(true);
                        } else {
                            mSpinnerArea.setEnabled(false);
                        }
                    }
                });

        mButtonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPostButton();
            }
        });
    }

    private void checkPermissionAndGetLocationName() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MainActivity.MY_PERMISSION_REQUEST_LOCATION);
        } else {
            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                try {
                    mLocationName = getLocation(location.getLatitude(), location.getLongitude());
                } catch (Exception e) {
                    e.printStackTrace();
                    mLocationName = "";
                }
            } else {
                mLocationManager.requestLocationUpdates(
                        String.valueOf(mLocationManager.getBestProvider(new Criteria(), true)),
                        1000, 0, this);
            }
        }
        Toast.makeText(getContext(), mLocationName, Toast.LENGTH_SHORT).show();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.setContentView(R.layout.dialog_choose_gallery_or_camera);
        dialog.show();
        dialog.findViewById(R.id.imageViewCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File file;
                    try {
                        file = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        ex.printStackTrace();
                        return;
                    }
                    // Continue only if the File was successfully created
                    Uri photoUri = FileProvider.getUriForFile(getContext(),
                            getActivity().getPackageName() + ".provider", file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }
            }
        });
        dialog.findViewById(R.id.imageViewGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                //tạo file từ uri của data
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

    public void EnableRuntimePermission() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                    new String[]{Manifest.permission.CAMERA}, 3);
        }
    }

    private void clickPostButton() {
        if (!LoginDialog.isLogged) {
            Toast.makeText(getContext(), "You are not logged in!", Toast.LENGTH_SHORT).show();
            new LoginDialog(getContext()).clickButtonProfile();
            return;
        }

        if (!validate()) {
            Toast.makeText(getContext(), "Post failure!", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.show();

        if (mChosenFile != null) {
            uploadImageToImgur();
        } else {
            postCrimeReport("");
        }
    }

    private void postCrimeReport(String image) {
        Call<JSONObject> callReport;
        if (mRadioButtonPresentLocation.isChecked()) {
            callReport = APIUtils.getData(APIUtils.BASE_URL)
                    .CreateCrimeReport(((CrimeCategory) mSpinnerCategory.getSelectedItem()).getId(),
                            mLocationName,
                            mEditTextTitle.getText().toString(),
                            mEditTextDescription.getText().toString(),
                            image,
                            Objects.requireNonNull(getContext())
                                    .getSharedPreferences("dataLogin", Context.MODE_PRIVATE)
                                    .getInt("idUser", 1000));
        } else {
            callReport = APIUtils.getData(APIUtils.BASE_URL)
                    .CreateCrimeReport(((CrimeCategory) mSpinnerCategory.getSelectedItem()).getId(),
                            mSpinnerArea.getSelectedItem().toString(),
                            mEditTextTitle.getText().toString(),
                            mEditTextDescription.getText().toString(),
                            image,
                            Objects.requireNonNull(getContext())
                                    .getSharedPreferences("dataLogin", Context.MODE_PRIVATE)
                                    .getInt("idUser", 1000));
        }
        callReport.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call,
                                   @NonNull Response<JSONObject> response) {
                if (response.body() != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                    clearInput();
                    Toast.makeText(getContext(), "Post successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Post failure!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable t) {
                Log.d("checkFailPostReport", t.getMessage());
                Toast.makeText(getContext(), "Post failure!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToImgur() {
        final NotificationHelper notificationHelper = new NotificationHelper(getContext());
        notificationHelper.createUploadingNotification();

        Call<ImageResponse> callImageToImgur = APIUtils.getData(APIUtils.API_IMGUR_URL)
                .PostImageToImgur(MultipartBody.Part.createFormData("image", mChosenFile.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), mChosenFile)));
        Log.d("checkFile1", mChosenFile.getAbsolutePath());
        Log.d("checkFile2", mChosenFile.getName());
        callImageToImgur.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(@NonNull Call<ImageResponse> call, @NonNull Response<ImageResponse> response) {
                if (response.isSuccessful()) {
                    notificationHelper.createUploadedNotification(response.body());
                    postCrimeReport(Objects.requireNonNull(response.body()).data.link);
                } else {
                    notificationHelper.createFailedUploadNotification();
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                Toast.makeText(getContext(), "An unknown error has occured.", Toast.LENGTH_SHORT)
                        .show();
                notificationHelper.createFailedUploadNotification();
                Log.d("checkUploadImageFail", t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        realPath = image.getAbsolutePath();
        return image;
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

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dark_Dialog);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getSpinnerArea() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Cẩm Lệ");
        arrayList.add("Hải Châu");
        arrayList.add("Liên Chiểu");
        arrayList.add("Thanh Khê");
        arrayList.add("Sơn Trà");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.spinner_textview,
                        arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerArea.setAdapter(adapter);
    }

    private void getSpinnerCrimeCategoryList() {
        Call<List<CrimeCategory>> callCrimeCategory = APIUtils.getData(APIUtils.BASE_URL)
                .GetCrimeCategoryList(APIUtils.API_GET_CRIME_CATEGORY_LIST_URL);
        callCrimeCategory.enqueue(new Callback<List<CrimeCategory>>() {
            @Override
            public void onResponse(@NonNull Call<List<CrimeCategory>> call,
                                   @NonNull Response<List<CrimeCategory>> response) {
                if (response.body() != null) {
                    ArrayList<CrimeCategory> arrayList = (ArrayList<CrimeCategory>) response.body();

                    ArrayAdapter<CrimeCategory> adapter =
                            new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                                    R.layout.spinner_textview, Objects.requireNonNull(arrayList));

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerCategory.setAdapter(adapter);
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<List<CrimeCategory>> call, @NonNull Throwable t) {
                Log.d("checkGetCrimeCategoryList", t.getMessage());
            }
        });
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

    private void clearInput() {
        mEditTextTitle.setText("");
        mEditTextDescription.setText("");
        mImageViewCrime.setImageBitmap(null);
        mRadioButtonPresentLocation.setChecked(false);
        mRadioButtonSelectLocation.setChecked(false);
        mSpinnerArea.setEnabled(false);
    }

    private boolean validate() {
        boolean valid = true;

        if (mEditTextTitle.getText().toString().isEmpty()) {
            mEditTextTitle.setError("Title can't be empty!");
            valid = false;
        } else if (mEditTextTitle.getText().toString().length() < 6) {
            mEditTextTitle.setError("Title must not be less than 6 characters!");
            valid = false;
        } else {
            mEditTextTitle.setError(null);
        }

        if (mEditTextDescription.getText().toString().isEmpty()) {
            mEditTextDescription.setError("Description can't be empty!");
            valid = false;
        } else if (mEditTextDescription.getText().toString().length() < 6) {
            mEditTextDescription.setError("Description must not be less than 6 characters!");
            valid = false;
        } else {
            mEditTextDescription.setError(null);
        }

        if (!mRadioButtonSelectLocation.isChecked() && (!mRadioButtonPresentLocation.isChecked())) {
            valid = false;
            Toast.makeText(getContext(), "You have not selected a location!", Toast.LENGTH_SHORT)
                    .show();
        }

        return valid;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationManager.removeUpdates(this);
        mLocationName = getLocation(location.getLatitude(), location.getLongitude());
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