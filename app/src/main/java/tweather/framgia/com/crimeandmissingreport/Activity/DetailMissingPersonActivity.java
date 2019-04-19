package tweather.framgia.com.crimeandmissingreport.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Objects;
import tweather.framgia.com.crimeandmissingreport.Fragment.CrimeListFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.MissingPersonListFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.ProfileCrimeReportListFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.ProfileMissingReportListFragment;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;

public class DetailMissingPersonActivity extends AppCompatActivity {
    ImageView mImageView;
    TextView mTextViewTitle, mTextViewDes, mTextViewTime;
    ScrollView mScrollView;
    Toolbar mToolbar;

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
        mScrollView = findViewById(R.id.scrollViewCrimeDetail);
        mToolbar = findViewById(R.id.toolbarMissingPersonDetail);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
    }

    @SuppressLint("SetTextI18n")
    public void getData() {
        Intent intent = getIntent();
        int positionMissing = intent.getIntExtra("position", 10000);
        int positionProfileMissing = intent.getIntExtra("positionProfileMissingList", 10001);

        Report missingReport = null;
        if (positionMissing != 10000) {
            missingReport = MissingPersonListFragment.missingPersonReportArrayList.get(positionMissing);
        } else if (positionProfileMissing != 10001) {
            missingReport = ProfileMissingReportListFragment.reportArrayList.get(positionProfileMissing);
        }

        mToolbar.setTitle(Objects.requireNonNull(missingReport).getTitle());
        mImageView.setImageResource(missingReport.getImage());
        mTextViewTitle.setText(missingReport.getTitle());
        mTextViewTime.setText("Posted: " + missingReport.getTime());
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
    }
}
