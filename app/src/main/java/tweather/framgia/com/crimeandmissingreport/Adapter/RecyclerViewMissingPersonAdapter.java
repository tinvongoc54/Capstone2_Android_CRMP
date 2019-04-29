package tweather.framgia.com.crimeandmissingreport.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import tweather.framgia.com.crimeandmissingreport.Activity.DetailMissingPersonActivity;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;

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

    //Convert Image Url to bitmap android
    public  class LoadImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        Bitmap bitmap = null;
        public LoadImageFromUrl(ImageView imageView) {
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                InputStream inputStream = url.openConnection().getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewMissingPersonAdapter.ViewHolder viewHolder,
            @SuppressLint("RecyclerView") final int i) {

        new LoadImageFromUrl(viewHolder.mImageView).execute(mReportList.get(i).getImage());
        viewHolder.mTextViewTitle.setText(mReportList.get(i).getTitle());
        viewHolder.mTextViewDes.setText(mReportList.get(i).getDescription());
        if (mReportList.get(i).getTime().equals("01/04/2019")) {
            viewHolder.mTextViewTime.setText("Hôm nay");
        } else if (mReportList.get(i).getTime().equals("31/03/2019")) {
            viewHolder.mTextViewTime.setText("Hôm qua");
        } else {
            viewHolder.mTextViewTime.setText(mReportList.get(i).getTime());
        }
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
