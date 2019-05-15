package tweather.framgia.com.crimeandmissingreport.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tweather.framgia.com.crimeandmissingreport.Activity.DetailMissingPersonActivity;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class RecyclerViewMissingPersonAdapter
        extends RecyclerView.Adapter<RecyclerViewMissingPersonAdapter.ViewHolder> {

    List<Report> mReportList;
    Context mContext;

    public RecyclerViewMissingPersonAdapter(List<Report> reportList, Context context) {
        mReportList = reportList;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerViewMissingPersonAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_recyclerview_missing_person, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewMissingPersonAdapter.ViewHolder viewHolder,
            @SuppressLint("RecyclerView") final int i) {
        if (!mReportList.get(i).getImage().equals("")) {
            Picasso.get()
                    .load(mReportList.get(i).getImage())
                    .placeholder(R.drawable.avatar)
                    .into(viewHolder.mImageView);
        } else {
            viewHolder.mImageView.setImageResource(R.drawable.avatar);
        }

        if (mReportList.get(i).getTitle().length() > 23) {
            viewHolder.mTextViewTitle.setText(mReportList.get(i).getTitle().substring(0, 22) + "...");
        } else {
            viewHolder.mTextViewTitle.setText(mReportList.get(i).getTitle());
        }

        if (mReportList.get(i).getDescription().length() > 120) {
            viewHolder.mTextViewDes.setText(
                    mReportList.get(i).getDescription().substring(0, 119)
                            + "...");
        } else {
            viewHolder.mTextViewDes.setText(
                    mReportList.get(i).getDescription());
        }

        viewHolder.mTextViewTime.setText(
                APIUtils.convertTime(mReportList.get(i).getTime()));

        viewHolder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailMissingPersonActivity.class);
                intent.putExtra("position", i);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReportList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextViewTitle, mTextViewDes, mTextViewTime;
        RelativeLayout mRelativeLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextViewTitle = itemView.findViewById(R.id.textViewTitle);
            mTextViewDes = itemView.findViewById(R.id.textViewDescription);
            mTextViewTime = itemView.findViewById(R.id.textViewTime);
            mRelativeLayout = itemView.findViewById(R.id.relativeLayoutMissingPerson);
        }
    }
}
