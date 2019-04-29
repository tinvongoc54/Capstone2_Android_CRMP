package tweather.framgia.com.crimeandmissingreport.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import tweather.framgia.com.crimeandmissingreport.Activity.DetailCrimeActivity;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;

public class RecyclerViewNewsAdapter
        extends RecyclerView.Adapter<RecyclerViewNewsAdapter.ViewHolder> {

    private List<Report> mCrimeReportList;
    private Context mContext;

    public RecyclerViewNewsAdapter(List<Report> crimeReportList, Context context) {
        mCrimeReportList = crimeReportList;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_recyclerview_news, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder,
            @SuppressLint("RecyclerView") final int i) {
        Picasso.with(mContext)
                .load(mCrimeReportList.get(mCrimeReportList.size() - i - 1).getImage())
                .into(viewHolder.mImageView);

        viewHolder.mTextViewTitle.setText(
                mCrimeReportList.get(mCrimeReportList.size() - i - 1).getTitle());
        viewHolder.mTextViewArea.setText(
                mCrimeReportList.get(mCrimeReportList.size() - i - 1).getArea());
        viewHolder.mTextViewDes.setText(
                mCrimeReportList.get(mCrimeReportList.size() - i - 1).getDescription());
        viewHolder.mTextViewTime.setText(
                mCrimeReportList.get(mCrimeReportList.size() - i - 1).getTime());
        viewHolder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailCrimeActivity.class);
                intent.putExtra("position", mCrimeReportList.size() - i - 1);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCrimeReportList.size();
    }

    private String formatDate(Date date) {
        @SuppressLint("SimpleDateFormat") DateFormat simpleDateFormat =
                new SimpleDateFormat("dd/mm/yyyy");
        return simpleDateFormat.format(date);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextViewTitle, mTextViewArea, mTextViewDes, mTextViewTime;
        RelativeLayout mRelativeLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextViewTitle = itemView.findViewById(R.id.textViewTitle);
            mTextViewArea = itemView.findViewById(R.id.textViewArea);
            mTextViewDes = itemView.findViewById(R.id.textViewDescription);
            mTextViewTime = itemView.findViewById(R.id.textViewTime);
            mRelativeLayout = itemView.findViewById(R.id.relativeLayoutNew);
        }
    }
}
