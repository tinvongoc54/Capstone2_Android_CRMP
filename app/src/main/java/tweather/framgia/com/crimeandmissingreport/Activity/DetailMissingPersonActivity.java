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
import tweather.framgia.com.crimeandmissingreport.Fragment.MissingPersonListFragment;
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
        int id = intent.getIntExtra("idMissingPerson", 123);
        Report report = MissingPersonListFragment.missingPersonReportArrayList.get(id);
        Log.d("checkID", "id" + id);
        mToolbar.setTitle(report.getTitle());
        mImageView.setImageResource(report.getImage());
        mTextViewTitle.setText(report.getTitle());
        mTextViewTime.setText("Posted: " + report.getTime());
        mTextViewDes.setText(report.getDescription());
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
