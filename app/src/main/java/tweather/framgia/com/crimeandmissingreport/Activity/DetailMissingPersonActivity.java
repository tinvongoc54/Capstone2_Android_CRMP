package tweather.framgia.com.crimeandmissingreport.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewCommentAdapter;
import tweather.framgia.com.crimeandmissingreport.Fragment.MissingPersonListFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.ProfileMissingReportListFragment;
import tweather.framgia.com.crimeandmissingreport.Object.Comment;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

import static tweather.framgia.com.crimeandmissingreport.Activity.MainActivity.MY_PERMISSION_REQUEST_CALL_PHONE;
import static tweather.framgia.com.crimeandmissingreport.Activity.MainActivity.MY_PERMISSION_REQUEST_SMS;

public class DetailMissingPersonActivity extends AppCompatActivity {
    ImageView mImageView;
    TextView mTextViewTitle, mTextViewDes, mTextViewTime;
    NestedScrollView mNestedScrollView;
    Toolbar mToolbar;
    Button mButtonPostComment;
    EditText mEditTextComment;
    RecyclerView mRecyclerViewComment;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Report missingReport;
    FloatingActionButton mActionShareFacebook, mActionCall, mActionSms;
    FloatingActionsMenu mActionMenu;
    CallbackManager mCallbackManager;
    ShareDialog mShareDialog;

//    Target target = new Target() {
//        @Override
//        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//            SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(bitmap).build();
//            if (ShareDialog.canShow(SharePhotoContent.class)) {
//                mShareDialog.show(new SharePhotoContent.Builder().addPhoto(sharePhoto).build());
//            }
//        }
//
//        @Override
//        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//        }
//
//        @Override
//        public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_missing_person);

        initView();
        initViewFAB();
        initEvent();
        initEventFAB();
        initSwipeRefreshLayout();
        getData();

        LocalBroadcastManager.getInstance(Objects.requireNonNull(this))
                .registerReceiver(mMessageReceiver, new IntentFilter("LoadMissingComment"));
    }

    private void initView() {
        mImageView = findViewById(R.id.imageViewDetail);
        mTextViewTitle = findViewById(R.id.textViewTitleDetail);
        mTextViewDes = findViewById(R.id.textViewDescriptionDetail);
        mTextViewTime = findViewById(R.id.textViewTimeDetail);
        mNestedScrollView = findViewById(R.id.nestedScrollViewDetailMissing);
//        mToolbar = findViewById(R.id.toolbarMissingPersonDetail);
//        setSupportActionBar(mToolbar);
//        mToolbar.setNavigationIcon(R.drawable.back);
        mButtonPostComment = findViewById(R.id.buttonPostComment);
        mEditTextComment = findViewById(R.id.editTextComment);
        mRecyclerViewComment = findViewById(R.id.recyclerViewCommentDetail);
    }

    private void initViewFAB() {
        mActionMenu = findViewById(R.id.multiple_actions);
        mActionShareFacebook = new FloatingActionButton(this);
        mActionShareFacebook.setIcon(R.drawable.facebook);
        mActionCall = new FloatingActionButton(this);
        mActionCall.setIcon(R.drawable.call);
        mActionSms = new FloatingActionButton(this);
        mActionSms.setIcon(R.drawable.sms);
        mActionMenu.addButton(mActionShareFacebook);
        mActionMenu.addButton(mActionCall);
        mActionMenu.addButton(mActionSms);

        mCallbackManager = CallbackManager.Factory.create();
        mShareDialog = new ShareDialog(this);
    }

    private void initEventFAB() {
        mActionShareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register callback
                mShareDialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(DetailMissingPersonActivity.this, "Share Successful!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(DetailMissingPersonActivity.this, "Share Cancle!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(DetailMissingPersonActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
//
                //load ảnh vào facebook
//                Picasso.get().load(missingReport.getImage()).into(target);
                ShareLinkContent linkContent;
                if (!missingReport.getImage().equals("")) {
                    linkContent = new ShareLinkContent.Builder()
                            .setQuote(missingReport.getTitle() + "\n\n" + missingReport.getDescription())
                            .setContentUrl(Uri.parse(missingReport.getImage()))
                            .build();
                } else {
                    linkContent = new ShareLinkContent.Builder()
                            .setQuote(missingReport.getTitle() + "\n\n" + missingReport.getDescription())
                            .setContentUrl(Uri.parse("https://i.imgur.com/zXO7Xk8.png"))
                            .build();
                }
                mShareDialog.show(linkContent);  // Show facebook ShareDialog
            }
        });
        mActionCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSharedPreferences(LoginDialog.SHAREDPREFERENCES, MODE_PRIVATE)
                        .contains(LoginDialog.SHAREDPREFERENCES_EMAIL)) {
                    checkCallPhonePermission();
                } else {
                    Toast.makeText(DetailMissingPersonActivity.this, "You are not logged in!", Toast.LENGTH_SHORT).show();
                    new LoginDialog(DetailMissingPersonActivity.this).clickButtonProfile();
                }
            }
        });
        mActionSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSharedPreferences(LoginDialog.SHAREDPREFERENCES, MODE_PRIVATE)
                        .contains(LoginDialog.SHAREDPREFERENCES_EMAIL)) {
                    checkSmsPermission();
                } else {
                    Toast.makeText(DetailMissingPersonActivity.this, "You are not logged in!", Toast.LENGTH_SHORT).show();
                    new LoginDialog(DetailMissingPersonActivity.this).clickButtonProfile();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void getData() {
        Intent intent = getIntent();
        int positionMissing = intent.getIntExtra("position", 10000);
        int positionProfileMissing = intent.getIntExtra("positionProfileMissingList", 10001);

        if (positionMissing != 10000) {
            missingReport =
                    MissingPersonListFragment.missingPersonReportArrayList.get(positionMissing);
        } else if (positionProfileMissing != 10001) {
            missingReport =
                    ProfileMissingReportListFragment.reportArrayList.get(positionProfileMissing);
        }

//        mToolbar.setTitle(Objects.requireNonNull(missingReport).getTitle());
        if (!missingReport.getImage().equals("")) {
            Picasso.get()
                    .load(missingReport.getImage())
                    .into(mImageView);
        } else {
            mImageView.setImageResource(R.drawable.avatar);
        }
        mTextViewTitle.setText(missingReport.getTitle());
        mTextViewTime.setText("Posted: " + APIUtils.convertTime(missingReport.getTime()));
        mTextViewDes.setText(missingReport.getDescription());

        getListComment();
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutDetailMissing);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListComment();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
    }

    public void initEvent() {
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(DetailMissingPersonActivity.this, MainActivity.class));
//                finish();
//            }
//        });
        mButtonPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mEditTextComment.equals("")) {
                    postComment();
                }
            }
        });
    }

    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("confirm", false)) {
                Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
                getListComment();
            }
        }
    };

    private void getListComment() {
        Call<List<Comment>> callComment = APIUtils.getData(APIUtils.BASE_URL)
                .GetCommentOfMissingReport(APIUtils.API_GET_COMMENT_OF_MISSING_REPORT_URL
                        + missingReport.getId()
                        + "/comments");
        callComment.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call,
                    @NonNull Response<List<Comment>> response) {
                if (response.body() != null) {
                    ArrayList<Comment> commentArrayList = (ArrayList<Comment>) response.body();
                    initRecyclerView(commentArrayList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call, @NonNull Throwable throwable) {
                Log.d("checkFailGetListComment", throwable.getMessage());
            }
        });
    }

    private void initRecyclerView(ArrayList<Comment> commentArrayList) {
        RecyclerViewCommentAdapter recyclerViewCommentAdapter =
                new RecyclerViewCommentAdapter(commentArrayList, this, false,
                        missingReport.getUserId());
        mRecyclerViewComment.setAdapter(recyclerViewCommentAdapter);
    }

    private void postComment() {
        if (!LoginDialog.isLogged) {
            Toast.makeText(this, "You need to login to comment!", Toast.LENGTH_SHORT).show();
            new LoginDialog(DetailMissingPersonActivity.this).clickButtonProfile();
        } else {
            Call<JSONObject> call = APIUtils.getData(APIUtils.BASE_URL)
                    .PostCommentMissingReport(missingReport.getId(),
                            getSharedPreferences("dataLogin", MODE_PRIVATE).getInt(
                                    LoginDialog.SHAREDPREFERENCES_ID_USER, 0),
                            getSharedPreferences("dataLogin", MODE_PRIVATE).getString(
                                    LoginDialog.SHAREDPREFERENCES_FULLNAME, ""),
                            mEditTextComment.getText().toString());
            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(@NonNull Call<JSONObject> call,
                        @NonNull Response<JSONObject> response) {
                    Log.d("checkComment", response.toString());
                    if (response.body() != null) {
                        getListComment();
                        mEditTextComment.setText("");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JSONObject> call,
                        @NonNull Throwable throwable) {
                    Log.d("checkFailPostComment", throwable.getMessage());
                }
            });
        }
    }

    private void showDialogCallHotline() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
        builder.setMessage("Do you want to call the report owner?")
                .setCancelable(false)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_CALL );
                        intent.setData(Uri.parse("tel:" +
                                getSharedPreferences(LoginDialog.SHAREDPREFERENCES, MODE_PRIVATE)
                                        .getString(LoginDialog.SHAREDPREFERENCES_PHONE_NUMBER,"")));
                        startActivity(intent);
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

    private void showDialogSms() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
        builder.setMessage("Do you want to send message the report owner?")
                .setCancelable(false)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("sms:" +
                                getSharedPreferences(LoginDialog.SHAREDPREFERENCES, MODE_PRIVATE)
                                        .getString(LoginDialog.SHAREDPREFERENCES_PHONE_NUMBER,"")));
                        startActivity(intent);
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

    private void checkSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSION_REQUEST_SMS);
        } else {
            showDialogSms();
        }
    }

    private void checkCallPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSION_REQUEST_CALL_PHONE);
        } else {
            showDialogCallHotline();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        showDialogCallHotline();
                    }
                }
                break;
            case MY_PERMISSION_REQUEST_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                        showDialogSms();
                    }
                }
                break;
        }
    }
}
