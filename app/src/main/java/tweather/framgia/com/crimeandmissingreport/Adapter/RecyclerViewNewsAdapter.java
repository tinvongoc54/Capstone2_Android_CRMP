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

import tweather.framgia.com.crimeandmissingreport.Activity.DetailCrimeActivity;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder,
            @SuppressLint("RecyclerView") final int i) {
        if (!mCrimeReportList.get(i).getImage().equals("")) {
            Picasso.get()
                    .load(mCrimeReportList.get(i).getImage())
                    .placeholder(R.drawable.avatar)
                    .into(viewHolder.mImageView);
        } else {
            viewHolder.mImageView.setImageResource(R.drawable.avatar);
        }

        viewHolder.mTextViewTitle.setText(
                mCrimeReportList.get(i).getTitle());
        viewHolder.mTextViewArea.setText(
                mCrimeReportList.get(i).getArea());

        if (mCrimeReportList.get(i).getDescription().length() > 120) {
            viewHolder.mTextViewDes.setText(mCrimeReportList.get(i)
                    .getDescription()
                    .substring(0, 119) + "...");
        } else {
            viewHolder.mTextViewDes.setText(
                    mCrimeReportList.get(i).getDescription());
        }

        viewHolder.mTextViewTime.setText(APIUtils.convertTime(
                mCrimeReportList.get(i).getTime()));
        viewHolder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailCrimeActivity.class);
                intent.putExtra("position", i);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCrimeReportList.size();
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
