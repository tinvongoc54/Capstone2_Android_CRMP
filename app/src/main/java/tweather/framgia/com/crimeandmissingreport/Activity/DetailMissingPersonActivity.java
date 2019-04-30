package tweather.framgia.com.crimeandmissingreport.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
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
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.json.JSONObject;
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
    Report missingReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_missing_person);

        initView();
        initEvent();
        getData();
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
        Picasso.with(DetailMissingPersonActivity.this)
                .load(missingReport.getImage())
                .into(mImageView);
        mTextViewTitle.setText(missingReport.getTitle());
        mTextViewTime.setText("Posted: " + APIUtils.convertTime(missingReport.getTime()));
        mTextViewDes.setText(missingReport.getDescription());
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
                new RecyclerViewCommentAdapter(commentArrayList, this);
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
