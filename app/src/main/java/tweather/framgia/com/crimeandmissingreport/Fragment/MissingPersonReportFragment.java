package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Activity.LoginDialog;
import tweather.framgia.com.crimeandmissingreport.Object.DocumentHelper;
import tweather.framgia.com.crimeandmissingreport.Object.ImageResponse;
import tweather.framgia.com.crimeandmissingreport.Object.NotificationHelper;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class MissingPersonReportFragment extends Fragment {

    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_GALLERY = 101;
    EditText mEditTextTitle, mEditTextDescription;
    Button mButtonSelectImage, mButtonPost;
    ImageView mImageViewMissing;
    File mChosenFile;
    Uri mReturnUri;
    private ProgressDialog mProgressDialog = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,

            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missing_person_report, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        mEditTextTitle = view.findViewById(R.id.editTextTitle);
        mEditTextDescription = view.findViewById(R.id.editTextDescription);
        mButtonSelectImage = view.findViewById(R.id.buttonSelectImage);
        mButtonPost = view.findViewById(R.id.buttonPost);
        mImageViewMissing = view.findViewById(R.id.imageViewMissingReport);
    }

    private void initEvent() {
        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPostButton();
            }
        });
        mButtonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.setContentView(R.layout.dialog_choose_gallery_or_camera);
        dialog.show();
        dialog.findViewById(R.id.imageViewCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                        REQUEST_CODE_CAMERA);
            }
        });
        dialog.findViewById(R.id.imageViewGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                mReturnUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            Objects.requireNonNull(getContext()).getContentResolver(), mReturnUri);
                    mImageViewMissing.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) {
                Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                mImageViewMissing.setImageBitmap(bitmap);
            }
            getFilePath();
        }
    }

    private void getFilePath() {
        String filePath = DocumentHelper.getPath(getContext(), mReturnUri);
        //Safety check to prevent null pointer exception
        if (filePath == null || filePath.isEmpty()) return;
        mChosenFile = new File(filePath);
        Log.d("FilePath", filePath);
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
        }
        postMissingPersonReport();
    }

    private void uploadImageToImgur() {
        final NotificationHelper notificationHelper = new NotificationHelper(getContext());
        notificationHelper.createUploadingNotification();

        Call<ImageResponse> callImageToImgur = APIUtils.getData(APIUtils.API_IMGUR_URL)
                .PostImageToImgur(MultipartBody.Part.createFormData("image", mChosenFile.getName(),
                        RequestBody.create(MediaType.parse("image/*"), mChosenFile)));

        callImageToImgur.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(@NonNull Call<ImageResponse> call,
                    @NonNull Response<ImageResponse> response) {
                if (response.isSuccessful()) {
                    notificationHelper.createUploadedNotification(response.body());
                    Log.d("URL Picture",
                            "http://imgur.com/" + Objects.requireNonNull(response.body()).data.id);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "An unknown error has occured.", Toast.LENGTH_SHORT)
                        .show();
                notificationHelper.createFailedUploadNotification();
                Log.d("checkUploadImageFail", t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void postMissingPersonReport() {
        Call<JSONObject> callReport = APIUtils.getData(APIUtils.BASE_URL)
                .CreateMissingPerson(mEditTextTitle.getText().toString(),
                        mEditTextDescription.getText().toString(),
                        Objects.requireNonNull(getContext())
                                .getSharedPreferences("dataLogin", Context.MODE_PRIVATE)
                                .getString("phoneNumber", ""), Objects.requireNonNull(getContext())
                                .getSharedPreferences("dataLogin", Context.MODE_PRIVATE)
                                .getInt("idUser", 1000)

                );
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

    private void clearInput() {
        mEditTextTitle.setText("");
        mEditTextDescription.setText("");
        mImageViewMissing.setImageBitmap(null);
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

        return valid;
    }
}
