package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewNewsAdapter;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewSpecialNewsAdapter;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class CrimeListFragment extends Fragment {
    RecyclerView mRecyclerViewSpecial, mRecyclerViewNew;
    public static ArrayList<Report> crimeReportArrayList = new ArrayList<>();
    NestedScrollView mNestedScrollView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        getCrimeReportList();
        initSwipeRefreshLayout();
        mNestedScrollView = view.findViewById(R.id.scrollViewCrimeList);
        return view;
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutCrimeList);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCrimeReportList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void getCrimeReportList() {
        Call<List<Report>> callList =
                APIUtils.getData(APIUtils.BASE_URL).GetCrimeReportList(APIUtils.API_GET_CRIME_REPORT_LIST_URL);
        callList.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(@NonNull Call<List<Report>> call,
                    @NonNull Response<List<Report>> response) {
                if (response.body() != null) {
                    crimeReportArrayList = (ArrayList<Report>) response.body();

                    initRecyclerViewSpecial(view);
                    initRecyclerViewNew(view);
                } else {
                    Toast.makeText(getContext(), "No data!", Toast.LENGTH_SHORT).show();
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<List<Report>> call, @NonNull Throwable t) {
                Log.d("checkFailGetCrimeReportList", t.getMessage());
            }
        });
    }

    private void initRecyclerViewNew(View view) {
        mRecyclerViewNew = view.findViewById(R.id.recyclerViewNew);
        mRecyclerViewNew.setHasFixedSize(true);
        mRecyclerViewNew.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));
        RecyclerViewNewsAdapter recyclerViewNewsAdapter =
                new RecyclerViewNewsAdapter(crimeReportArrayList, getContext());
        mRecyclerViewNew.setAdapter(recyclerViewNewsAdapter);
    }

    public void initRecyclerViewSpecial(View view) {
        mRecyclerViewSpecial = view.findViewById(R.id.recyclerViewSpecial);
        mRecyclerViewSpecial.setHasFixedSize(true);
        mRecyclerViewSpecial.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false));
        RecyclerViewSpecialNewsAdapter recyclerViewSpecialNewsAdapter =
                new RecyclerViewSpecialNewsAdapter(crimeReportArrayList, getContext());
        mRecyclerViewSpecial.setAdapter(recyclerViewSpecialNewsAdapter);
    }
}
