package tweather.framgia.com.crimeandmissingreport.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewCommentAdapter;
import tweather.framgia.com.crimeandmissingreport.Fragment.CrimeListFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.ProfileCrimeReportListFragment;
import tweather.framgia.com.crimeandmissingreport.Object.Comment;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.Object.ReportCategory;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class DetailCrimeActivity extends AppCompatActivity {

    ImageView mImageView, mImageViewReport;
    TextView mTextViewTitle, mTextViewArea, mTextViewDes, mTextViewTime;
    EditText mEditTextComment;
    Button mButtonPostComment;
    NestedScrollView mNestedScrollView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerViewComment;
    Report crimeReport;
    Spinner spinnerReportCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_crime);

        initView();
        initEvent();
        initSwipeRefreshLayout();
        getData();

        LocalBroadcastManager.getInstance(Objects.requireNonNull(this))
                .registerReceiver(mMessageReceiver, new IntentFilter("LoadCrimeComment"));
    }

    private void initView() {
        mImageView = findViewById(R.id.imageViewDetail);
        mImageViewReport = findViewById(R.id.imageViewReport);
        mTextViewTitle = findViewById(R.id.textViewTitleDetail);
        mTextViewArea = findViewById(R.id.textViewAreaDetail);
        mTextViewDes = findViewById(R.id.textViewDescriptionDetail);
        mTextViewTime = findViewById(R.id.textViewTime);
        mNestedScrollView = findViewById(R.id.nestedScrollViewDetailCrime);
        mRecyclerViewComment = findViewById(R.id.recyclerViewCommentDetail);
        mEditTextComment = findViewById(R.id.editTextComment);
        mButtonPostComment = findViewById(R.id.buttonPostComment);
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutDetailCrime);
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
                .GetCommentOfCrimeReport(APIUtils.API_GET_COMMENT_OF_CRIME_REPORT_URL
                        + crimeReport.getId()
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

            }
        });
    }

    private void initRecyclerView(ArrayList<Comment> commentArrayList) {
        mRecyclerViewComment.setLayoutAnimation(AnimationUtils
                .loadLayoutAnimation(this,
                        R.anim.layout_animation_down_to_up));

        RecyclerViewCommentAdapter recyclerViewCommentAdapter =
                new RecyclerViewCommentAdapter(commentArrayList, this, true,
                        crimeReport.getUserId());
        mRecyclerViewComment.setAdapter(recyclerViewCommentAdapter);
    }

    @SuppressLint("SetTextI18n")
    public void getData() {
        //kiểm tra nơi click để chuyển trang detail
        //ví dụ bấm từ list crime ở home page thì sẽ dùng mảng của CrimeList
        //bấm từ list crime trong profile thì sẽ dùng mảng của ProfileCrimeList
        //vì trong mỗi mảng, report có vị trí khác nhau
        Intent intent = getIntent();
        int positionCrime = intent.getIntExtra("position", 10000);
        int positionProfileCrime = intent.getIntExtra("positionProfileCrimeList", 10001);

        if (positionCrime != 10000) {
            crimeReport = CrimeListFragment.crimeReportArrayList.get(positionCrime);
        } else if (positionProfileCrime != 10001) {
            crimeReport = ProfileCrimeReportListFragment.reportArrayList.get(positionProfileCrime);
        }

//        mToolbar.setTitle(Objects.requireNonNull(crimeReport).getTitle());
        if (!crimeReport.getImage().equals("")) {
            Picasso.get()
                    .load(crimeReport.getImage())
                    .into(mImageView);
        } else {
            mImageView.setImageResource(R.drawable.avatar);
        }
        mTextViewTitle.setText(crimeReport.getTitle());
        mTextViewArea.setText(crimeReport.getArea());
        mTextViewTime.setText(APIUtils.convertTime(crimeReport.getTime()));
        mTextViewDes.setText(crimeReport.getDescription());

        getListComment();
    }

    public void initEvent() {
        mButtonPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mEditTextComment.equals("")) {
                    postComment();
                }
            }
        });
        mImageViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogReport();
            }
        });
    }

    private void showDialogReport() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_send_report);
        spinnerReportCategory = dialog.findViewById(R.id.spinnerSelectArea);
        getSpinnerReport();
        Button buttonSelect = dialog.findViewById(R.id.buttonSelect);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                postReport(((ReportCategory) spinnerReportCategory.getSelectedItem()).getId());
            }
        });
        dialog.show();
    }

    private void getSpinnerReport() {
        Call<List<ReportCategory>> call = APIUtils.getData(APIUtils.BASE_URL)
                .GetReportCategory(APIUtils.API_GET_REPORT_CATEGORY_URL);
        call.enqueue(new Callback<List<ReportCategory>>() {
            @Override
            public void onResponse(Call<List<ReportCategory>> call, Response<List<ReportCategory>> response) {
                if (response.isSuccessful()) {
                    ArrayList<ReportCategory> arrayList = (ArrayList<ReportCategory>) response.body();

                    ArrayAdapter<ReportCategory> adapter =
                            new ArrayAdapter<>(DetailCrimeActivity.this, R.layout.spinner_textview,
                                    Objects.requireNonNull(arrayList));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerReportCategory.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<ReportCategory>> call, Throwable t) {

            }
        });
    }

    private void postReport(int reportId) {
        Call<JSONObject> call = APIUtils.getData(APIUtils.BASE_URL)
                .PostReport(1, crimeReport.getId(), reportId);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetailCrimeActivity.this, "Send!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {

            }
        });
    }

    private void postComment() {
        if (!LoginDialog.isLogged) {
            Toast.makeText(this, "You need to login to comment!", Toast.LENGTH_SHORT).show();
            new LoginDialog(DetailCrimeActivity.this).clickButtonProfile();
        } else {
            Call<JSONObject> call = APIUtils.getData(APIUtils.BASE_URL)
                    .PostCommentCrimeReport(crimeReport.getId(),
                            getSharedPreferences("dataLogin", MODE_PRIVATE).getInt(
                                    LoginDialog.SHAREDPREFERENCES_ID_USER, 10000),
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
}
