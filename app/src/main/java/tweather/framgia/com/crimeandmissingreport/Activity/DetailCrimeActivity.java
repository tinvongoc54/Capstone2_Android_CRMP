package tweather.framgia.com.crimeandmissingreport.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewCommentAdapter;
import tweather.framgia.com.crimeandmissingreport.Fragment.CrimeListFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.ProfileCrimeReportListFragment;
import tweather.framgia.com.crimeandmissingreport.Object.Comment;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class DetailCrimeActivity extends AppCompatActivity {

    ImageView mImageView;
    TextView mTextViewTitle, mTextViewArea, mTextViewDes, mTextViewTime;
    EditText mEditTextComment;
    Button mButtonPostComment;
    NestedScrollView mNestedScrollView;
    Toolbar mToolbar;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerViewComment;
    Report crimeReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_crime);

        initView();
        initEvent();
        getData();
    }

    private void initView() {
        mImageView = findViewById(R.id.imageViewDetail);
        mTextViewTitle = findViewById(R.id.textViewTitleDetail);
        mTextViewArea = findViewById(R.id.textViewAreaDetail);
        mTextViewDes = findViewById(R.id.textViewDescriptionDetail);
        mTextViewTime = findViewById(R.id.textViewTime);
        mNestedScrollView = findViewById(R.id.nestedScrollViewDetailCrime);
        mToolbar = findViewById(R.id.toolbarCrimeDetail);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutDetailCrime);
        mRecyclerViewComment = findViewById(R.id.recyclerViewCommentDetail);
        mEditTextComment = findViewById(R.id.editTextComment);
        mButtonPostComment = findViewById(R.id.buttonPostComment);
    }

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
        RecyclerViewCommentAdapter recyclerViewCommentAdapter =
                new RecyclerViewCommentAdapter(commentArrayList, this);
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

        mToolbar.setTitle(Objects.requireNonNull(crimeReport).getTitle());
        Picasso.with(DetailCrimeActivity.this).load(crimeReport.getImage()).into(mImageView);
        mTextViewTitle.setText(crimeReport.getTitle());
        mTextViewArea.setText("District: " + crimeReport.getArea());
        mTextViewTime.setText("Posted: " + APIUtils.convertTime(crimeReport.getTime()));
        mTextViewDes.setText(crimeReport.getDescription());

        getListComment();
    }

    public void initEvent() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(DetailCrimeActivity.this, MainActivity.class));
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
