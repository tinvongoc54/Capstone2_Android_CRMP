package tweather.framgia.com.crimeandmissingreport.Activity;

import android.annotation.SuppressLint;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.picasso.Picasso;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_missing_person);

        initView();
        initViewFAB();
        initEvent();
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
        mToolbar = findViewById(R.id.toolbarMissingPersonDetail);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        mButtonPostComment = findViewById(R.id.buttonPostComment);
        mEditTextComment = findViewById(R.id.editTextComment);
        mRecyclerViewComment = findViewById(R.id.recyclerViewCommentDetail);
    }

    private void initViewFAB() {
        final View actionB = findViewById(R.id.action_b);

        FloatingActionButton actionC = new FloatingActionButton(getBaseContext());
        actionC.setTitle("Hide/Show Action above");
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailMissingPersonActivity.this, "C", Toast.LENGTH_SHORT).show();
                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

        final FloatingActionsMenu menuMultipleActions = findViewById(R.id.multiple_actions);
        menuMultipleActions.addButton(actionC);
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

        mToolbar.setTitle(Objects.requireNonNull(missingReport).getTitle());
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
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailMissingPersonActivity.this, MainActivity.class));
                finish();
            }
        });
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
}
