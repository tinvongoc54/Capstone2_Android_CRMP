package tweather.framgia.com.crimeandmissingreport.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import java.util.Objects;
import tweather.framgia.com.crimeandmissingreport.Fragment.CrimeListFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.ProfileCrimeReportListFragment;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;

public class DetailCrimeActivity extends AppCompatActivity {

    ImageView mImageView;
    TextView mTextViewTitle, mTextViewArea, mTextViewDes, mTextViewTime;
    ScrollView mScrollView;
    Toolbar mToolbar;

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
        mScrollView = findViewById(R.id.scrollViewCrimeDetail);
        mToolbar = findViewById(R.id.toolbarCrimeDetail);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
    }

    @SuppressLint("SetTextI18n")
    public void getData() {
        Intent intent = getIntent();
        int positionCrime = intent.getIntExtra("position", 10000);
        int positionProfileCrime = intent.getIntExtra("positionProfileCrimeList", 10001);

        Report crimeReport = null;
        if (positionCrime != 10000) {
            crimeReport = CrimeListFragment.crimeReportArrayList.get(positionCrime);
        } else if (positionProfileCrime != 10001) {
            crimeReport = ProfileCrimeReportListFragment.reportArrayList.get(positionProfileCrime);
        }

        mToolbar.setTitle(Objects.requireNonNull(crimeReport).getTitle());
        mImageView.setImageResource(crimeReport.getImage());
        mTextViewTitle.setText(crimeReport.getTitle());
        mTextViewArea.setText("District: " + crimeReport.getArea());
        mTextViewTime.setText("Posted: " + crimeReport.getTime());
        mTextViewDes.setText(crimeReport.getDescription());
    }

    public void initEvent() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailCrimeActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
