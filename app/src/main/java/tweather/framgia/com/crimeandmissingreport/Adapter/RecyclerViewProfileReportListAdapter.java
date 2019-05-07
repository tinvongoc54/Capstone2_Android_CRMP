package tweather.framgia.com.crimeandmissingreport.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Activity.DetailCrimeActivity;
import tweather.framgia.com.crimeandmissingreport.Activity.DetailMissingPersonActivity;
import tweather.framgia.com.crimeandmissingreport.Activity.LoginDialog;
import tweather.framgia.com.crimeandmissingreport.Fragment.ProfileFragment;
import tweather.framgia.com.crimeandmissingreport.Object.CrimeCategory;
import tweather.framgia.com.crimeandmissingreport.Object.ImageResponse;
import tweather.framgia.com.crimeandmissingreport.Object.NotificationHelper;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.Object.User;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class RecyclerViewProfileReportListAdapter
        extends RecyclerView.Adapter<RecyclerViewProfileReportListAdapter.ViewHolder> {

    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_GALLERY = 101;
    private static String imageReport = null;
    private List<Report> mReportList;
    private Context mContext;

    public RecyclerViewProfileReportListAdapter(List<Report> reportList, Context context) {
        mReportList = reportList;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_recyclerview_profile_report_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,
            @SuppressLint("RecyclerView") final int i) {
        Picasso.with(mContext).load(mReportList.get(mReportList.size() - i - 1).getImage()).into(viewHolder.mImageView);
        viewHolder.mTextViewTitle.setText(mReportList.get(mReportList.size() - i - 1).getTitle());

        if (mReportList.get(mReportList.size() - i - 1).getDescription().length() > 80) {
            viewHolder.mTextViewDes.setText(
                    mReportList.get(mReportList.size() - i - 1).getDescription().substring(0, 79) + "...");
        } else {
            viewHolder.mTextViewDes.setText(mReportList.get(mReportList.size() - i - 1).getDescription());
        }

        viewHolder.mTextViewTime.setText(APIUtils.convertTime(mReportList.get(mReportList.size() - i - 1).getTime()));
        viewHolder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ProfileFragment.checkFragment) {
                    Intent intent = new Intent(mContext, DetailCrimeActivity.class);
                    intent.putExtra("positionProfileCrimeList", mReportList.size() - i - 1);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, DetailMissingPersonActivity.class);
                    intent.putExtra("positionProfileMissingList", mReportList.size() - i - 1);
                    mContext.startActivity(intent);
                }
            }
        });

        viewHolder.mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiaglogEdit diaglogEdit = new DiaglogEdit(mContext);
                if (ProfileFragment.checkFragment) {
                    diaglogEdit.showEditCrimeDialog(i);
                } else {
                    diaglogEdit.showEditMissingDialog(i);
                }
            }
        });

        viewHolder.mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ProfileFragment.checkFragment) {
                    confirmDelete(true, mReportList.size() - i - 1);
                } else {
                    confirmDelete(false, mReportList.size() - i - 1);
                }
            }
        });
    }

    private class DiaglogEdit {
        private TextView mTextViewCategory, mTextViewArea;
        private Spinner mSpinnerCategory, mSpinnerArea;
        private EditText mEditTextTile, mEditTextDescription;
        private ImageView mImageView;
        private Button mButtonEdit, mChooseImage;
        private Dialog mDialog;
        private Context mContext;
        private ProgressDialog mProgressDialog = null;
        private File mChosenFile;

        public DiaglogEdit(Context context) {
            mContext = context;
            mDialog = new Dialog(mContext);
        }

        @SuppressLint("CutPasteId")
        public void showEditMissingDialog(final int position) {
            mDialog.setContentView(R.layout.dialog_profile_edit_missing);
            initViewMissing();

            mEditTextTile.setText(mReportList.get(position).getTitle());
            mEditTextDescription.setText(mReportList.get(position).getDescription());
            Picasso.with(mContext)
                    .load(mReportList.get(position).getImage())
                    .into(mImageView);

            mDialog.show();
            mChooseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
            if (mChosenFile != null) {
                uploadImageToImgur();
            }
            mButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateMissingReport(position);
                }
            });
        }

        @SuppressLint("CutPasteId")
        public void showEditCrimeDialog(final int position) {
            mDialog.setContentView(R.layout.dialog_profile_edit_crime);
            initViewCrime();
            getSpinnerCrimeCategoryList(position);

            getSpinnerArea(position);
            mEditTextTile.setText(mReportList.get(position).getTitle());
            mEditTextDescription.setText(mReportList.get(position).getDescription());

            Picasso.with(mContext)
                    .load(mReportList.get(position).getImage())
                    .into(mImageView);

            mDialog.show();
            mChooseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });
            if (mChosenFile != null) {
                uploadImageToImgur();
            }
            mButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCrimeReport(position);
                }
            });
        }

        public void initViewMissing() {
            mEditTextTile = mDialog.findViewById(R.id.editTextTitle);
            mEditTextDescription = mDialog.findViewById(R.id.editTextContent);
            mButtonEdit = mDialog.findViewById(R.id.buttonEditMissing);
            mChooseImage = mDialog.findViewById(R.id.buttonChooseImage);
            mImageView = mDialog.findViewById(R.id.imageViewCrime);
        }

        public void initViewCrime() {
            mTextViewCategory = mDialog.findViewById(R.id.textViewCategory);
            mSpinnerCategory = mDialog.findViewById(R.id.spinnerCategory);
            mTextViewArea = mDialog.findViewById(R.id.textViewArea);
            mSpinnerArea = mDialog.findViewById(R.id.spinnerArea);
            mEditTextTile = mDialog.findViewById(R.id.editTextTitle);
            mEditTextDescription = mDialog.findViewById(R.id.editTextContent);
            mButtonEdit = mDialog.findViewById(R.id.buttonEditCrime);
            mChooseImage = mDialog.findViewById(R.id.buttonChooseImage);
            mImageView = mDialog.findViewById(R.id.imageViewCrime);
        }

        private void updateMissingReport(final int position) {
            mProgressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Processing...");
            mProgressDialog.show();

            Call<Report> callUpdate = APIUtils.getData(APIUtils.API_EDIT_MISSING_REPORT_URL)
                    .EditMissingReport(
                            mReportList.get(position).getId(),
                            mEditTextTile.getText().toString(),
                            mEditTextDescription.getText().toString(),
                            imageReport);
            callUpdate.enqueue(new Callback<Report>() {
                @Override
                public void onResponse(@NonNull Call<Report> call, @NonNull Response<Report> response) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;

                    if (response.body() != null) {
                        Toast.makeText(mContext, "Update successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent("updateMissingReport");
                        intent.putExtra("loadListMissing", true);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        mDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Report> call, @NonNull Throwable throwable) {

                }
            });
        }

        private void updateCrimeReport(final int position) {
            mProgressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Processing...");
            mProgressDialog.show();

            Call<Report> callUpdate = APIUtils.getData(APIUtils.API_EDIT_MISSING_REPORT_URL)
                    .EditCrimeReport(
                            mReportList.get(position).getId(),
                            mEditTextTile.getText().toString(),
                            mSpinnerArea.getSelectedItem().toString(),
                            mEditTextDescription.getText().toString(),
                            ((CrimeCategory) mSpinnerCategory.getSelectedItem()).getId(),
                            imageReport);
            callUpdate.enqueue(new Callback<Report>() {
                @Override
                public void onResponse(@NonNull Call<Report> call, @NonNull Response<Report> response) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;

                    if (response.body() != null) {
                        Toast.makeText(mContext, "Update successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent("updateCrimeReport");
                        intent.putExtra("loadListCrime", true);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        mDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Report> call, @NonNull Throwable throwable) {

                }
            });
        }

        private void uploadImageToImgur() {
            final NotificationHelper notificationHelper = new NotificationHelper(mContext);
            notificationHelper.createUploadingNotification();

            Call<ImageResponse> callImageToImgur = APIUtils.getData(APIUtils.API_IMGUR_URL)
                    .PostImageToImgur(MultipartBody.Part.createFormData("image", mChosenFile.getName(),
                            RequestBody.create(MediaType.parse("image/*"), mChosenFile)));

            callImageToImgur.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {

                    if (response == null) {
                        notificationHelper.createFailedUploadNotification();
                        Log.d("checkResponseNull", response.body().toString());
                        return;
                    }
                    if (response.isSuccessful()) {
                        notificationHelper.createUploadedNotification(response.body());
                        imageReport = ("http://imgur.com/" + response.body().data.id);
                        Log.d("URL Picture", "http://imgur.com/" + response.body().data.id);
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    Toast.makeText(mContext, "An unknown error has occured.", Toast.LENGTH_SHORT)
                            .show();
                    notificationHelper.createFailedUploadNotification();
                    Log.d("checkUploadImageFail", t.getMessage());
                    t.printStackTrace();
                }
            });
        }

        private void getSpinnerCrimeCategoryList(final int postion) {
            Call<List<CrimeCategory>> callCrimeCategory = APIUtils.getData(APIUtils.BASE_URL)
                    .GetCrimeCategoryList(APIUtils.API_GET_CRIME_CATEGORY_LIST_URL);
            callCrimeCategory.enqueue(new Callback<List<CrimeCategory>>() {
                @Override
                public void onResponse(@NonNull Call<List<CrimeCategory>> call,
                                       @NonNull Response<List<CrimeCategory>> response) {
                    if (response.body() != null) {
                        ArrayList<CrimeCategory> arrayListCategory = (ArrayList<CrimeCategory>) response.body();
                        ArrayAdapter<CrimeCategory> adapter =
                                new ArrayAdapter<>(Objects.requireNonNull(mContext),
                                        R.layout.spinner_textview, Objects.requireNonNull(arrayListCategory));
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinnerCategory.setAdapter(adapter);
                        for (int i = 0 ; i<arrayListCategory.size() ; i++) {
                            if (mReportList.get(postion).getCrimeCategory() == arrayListCategory.get(i).getId()) {
                                int spinnerPosition = adapter.getPosition(arrayListCategory.get(i));
                                mSpinnerCategory.setSelection(spinnerPosition);
                            }
                        }
                    }
                }

                @SuppressLint("LongLogTag")
                @Override
                public void onFailure(@NonNull Call<List<CrimeCategory>> call, @NonNull Throwable t) {
                    Log.d("checkGetCrimeCategoryList", t.getMessage());
                }
            });
        }

        private void getSpinnerArea(final int position) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("Cẩm Lệ");
            arrayList.add("Hải Châu");
            arrayList.add("Liên Chiểu");
            arrayList.add("Thanh Khê");
            arrayList.add("Sơn Trà");
            arrayList.add("Ngũ Hành Sơn");

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(Objects.requireNonNull(mContext), R.layout.spinner_textview,
                            arrayList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerArea.setAdapter(adapter);
            for (int i = 0 ; i<arrayList.size() ; i++) {
                if (mReportList.get(position).getArea().equals(arrayList.get(i))) {
                    int spinnerPosition = adapter.getPosition(arrayList.get(i));
                    mSpinnerArea.setSelection(spinnerPosition);
                }
            }
        }
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(Objects.requireNonNull(mContext));
        dialog.setContentView(R.layout.dialog_choose_gallery_or_camera);
        dialog.show();
        dialog.findViewById(R.id.imageViewCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ((Activity) mContext).startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                        REQUEST_CODE_CAMERA);
            }
        });
        dialog.findViewById(R.id.imageViewGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
        });
    }

    private void confirmDelete(final boolean isCrimeFragment, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppTheme_Dark_Dialog);
        builder.setMessage("Do you want to delete this report?")
                .setCancelable(false)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isCrimeFragment) {
                            deleteCrimeReport(position);
                        } else {
                            deleteMissingReport(position);
                        }
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

        alertDialog.show();
    }

    private void deleteMissingReport(int position) {
        Call<JSONObject> callDelete = APIUtils.getData(APIUtils.API_GET_MISSINGS_URL)
                .DeleteMissingReport(mReportList.get(position).getId());
        callDelete.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call,
                                   @NonNull Response<JSONObject> response) {
                if (response.body() != null) {
                    Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("deleteMissingReport");
                    intent.putExtra("delete", true);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                } else {
                    Toast.makeText(mContext, "An error occurred!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable throwable) {
                Log.d("checkFailDeleteReport", throwable.getMessage());
            }
        });
    }

    private void deleteCrimeReport(int position) {
        Call<JSONObject> callDelete = APIUtils.getData(APIUtils.API_GET_CRIMES_URL)
                .DeleteCrimeReport(mReportList.get(position).getId());
        callDelete.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call,
                                   @NonNull Response<JSONObject> response) {
                if (response.body() != null) {
                    Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("deleteCrimeReport");
                    intent.putExtra("delete", true);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                } else {
                    Toast.makeText(mContext, "An error occurred!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable throwable) {
                Log.d("checkFailDeleteReport", throwable.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReportList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextViewTitle, mTextViewDes, mTextViewTime;
        Button mButtonEdit, mButtonDelete;
        RelativeLayout mRelativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextViewTitle = itemView.findViewById(R.id.textViewTitle);
            mTextViewDes = itemView.findViewById(R.id.textViewDescription);
            mTextViewTime = itemView.findViewById(R.id.textViewTime);
            mButtonEdit = itemView.findViewById(R.id.buttonEdit);
            mButtonDelete = itemView.findViewById(R.id.buttonDelete);
            mRelativeLayout = itemView.findViewById(R.id.relativeLayoutReportList);
        }
    }
}
