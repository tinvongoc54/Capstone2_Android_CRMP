package tweather.framgia.com.crimeandmissingreport.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Activity.DetailCrimeActivity;
import tweather.framgia.com.crimeandmissingreport.Activity.DetailMissingPersonActivity;
import tweather.framgia.com.crimeandmissingreport.Fragment.ProfileCrimeReportListFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.ProfileFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.ProfileMissingReportListFragment;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class RecyclerViewProfileReportListAdapter
        extends RecyclerView.Adapter<RecyclerViewProfileReportListAdapter.ViewHolder> {

    private List<Report> mReportList;
    private Context mContext;

    public RecyclerViewProfileReportListAdapter(List<Report> reportList, Context context) {
        mReportList = reportList;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_recyclerview_profile_report_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(R.drawable.image1);
        arrayList.add(R.drawable.image2);
        arrayList.add(R.drawable.image8);
        arrayList.add(R.drawable.image6);
        arrayList.add(R.drawable.image7);
        Random random = new Random();

        viewHolder.mImageView.setImageResource(arrayList.get(random.nextInt(arrayList.size())));
        viewHolder.mTextViewTitle.setText(mReportList.get(mReportList.size() - i - 1).getTitle());
        viewHolder.mTextViewDes.setText(
                mReportList.get(mReportList.size() - i - 1).getDescription());
        viewHolder.mTextViewTime.setText(mReportList.get(mReportList.size() - i - 1).getTime());

        viewHolder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ProfileFragment.checkFragment) {
                    Intent intent = new Intent(mContext, DetailCrimeActivity.class);
                    intent.putExtra("positionProfileCrimeList", mReportList.size() - i - 1);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, DetailMissingPersonActivity.class);
                    intent.putExtra("positionProfileMissingList", mReportList.size() - i - 1);
                    mContext.startActivity(intent);
                }
            }
        });

        viewHolder.mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        viewHolder.mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ProfileFragment.checkFragment) {
                    deleteCrimeReport(i);
                } else {
                    deleteMissingReport(i);
                }
            }
        });
    }

    private void deleteMissingReport(final int position) {
        Call<JSONObject> callDelete = APIUtils.getData(APIUtils.API_GET_MISSINGS_URL)
                .DeleteMissingReport(mReportList.get(position).getId());
        callDelete.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call,
                    @NonNull Response<JSONObject> response) {
                if (response.body() != null) {
                    Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("deleteMissingReport");
                    intent.putExtra("positionDelete", position);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                } else {
                    Toast.makeText(mContext, "An error occurred!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable throwable) {
                Log.d("checkFailDeleteReport", throwable.getMessage());
            }
        });
    }

    private void deleteCrimeReport(final int position) {
        Call<JSONObject> callDelete = APIUtils.getData(APIUtils.API_GET_CRIMES_URL)
                .DeleteCrimeReport(mReportList.get(position).getId());
        callDelete.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call,
                    @NonNull Response<JSONObject> response) {
                if (response.body() != null) {
                    Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("deleteCrimeReport");
                    intent.putExtra("positionDelete", position);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                } else {
                    Toast.makeText(mContext, "An error occurred!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable throwable) {
                Log.d("checkFailDeleteReport", throwable.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReportList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextViewTitle, mTextViewDes, mTextViewTime;
        Button mButtonEdit, mButtonDelete;
        RelativeLayout mRelativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextViewTitle = itemView.findViewById(R.id.textViewTitle);
            mTextViewDes = itemView.findViewById(R.id.textViewDescription);
            mTextViewTime = itemView.findViewById(R.id.textViewTime);
            mButtonEdit = itemView.findViewById(R.id.buttonEdit);
            mButtonDelete = itemView.findViewById(R.id.buttonDelete);
            mRelativeLayout = itemView.findViewById(R.id.relativeLayoutReportList);
        }
    }
}
