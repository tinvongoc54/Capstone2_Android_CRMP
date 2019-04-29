package tweather.framgia.com.crimeandmissingreport.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import tweather.framgia.com.crimeandmissingreport.Activity.DetailCrimeActivity;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;

public class RecyclerViewSpecialNewsAdapter
        extends RecyclerView.Adapter<RecyclerViewSpecialNewsAdapter.ViewHolder> {

    private List<Report> mCrimeReportList;
    private Context mContext;

    public RecyclerViewSpecialNewsAdapter(List<Report> crimeReportList, Context context) {
        mCrimeReportList = crimeReportList;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerViewSpecialNewsAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_recyclerview_special_news, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder,
            @SuppressLint("RecyclerView") final int i) {
        Picasso.with(mContext)
                .load(mCrimeReportList.get(mCrimeReportList.size() - i - 1).getImage())
                .into(viewHolder.mImageView);

        viewHolder.mTextView.setText(
                mCrimeReportList.get(mCrimeReportList.size() - i - 1).getTitle());
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

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;
        RelativeLayout mRelativeLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView = itemView.findViewById(R.id.textViewTitle);
            mRelativeLayout = itemView.findViewById(R.id.relativeLayoutSpecial);
        }
    }
}
