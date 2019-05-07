package tweather.framgia.com.crimeandmissingreport.Fragment;

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
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewMissingPersonAdapter;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class MissingPersonListFragment extends Fragment {
    RecyclerView mRecyclerViewMissingPerson;
    public static ArrayList<Report> missingPersonReportArrayList = new ArrayList<>();
    NestedScrollView mNestedScrollView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_missing_person_list, container, false);
        getMissingPersonReportList();
        initSwipeRefreshLayout();
        mNestedScrollView = mView.findViewById(R.id.scrollViewMissingPersonList);
        initRecyclerViewMissingPerson(mView);
        return mView;
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout = mView.findViewById(R.id.swipeRefreshLayoutMissingList);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMissingPersonReportList();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
    }

    private void getMissingPersonReportList() {
        Call<List<Report>> call = APIUtils.getData(APIUtils.BASE_URL)
                .GetMissingReportList(APIUtils.API_GET_MISSINGS_URL);
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(@NonNull Call<List<Report>> call,
                    @NonNull Response<List<Report>> response) {
                if (response.body() != null) {
                    missingPersonReportArrayList = (ArrayList<Report>) response.body();
                    initRecyclerViewMissingPerson(mView);
                } else {
                    Toast.makeText(getContext(), "No data!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Report>> call, @NonNull Throwable throwable) {
                Log.d("GetMissingListFail", throwable.getMessage());
            }
        });
    }

    private void initRecyclerViewMissingPerson(View view) {
        mRecyclerViewMissingPerson = view.findViewById(R.id.recyclerViewMissingPerson);
        mRecyclerViewMissingPerson.setHasFixedSize(true);
        mRecyclerViewMissingPerson.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));
        RecyclerViewMissingPersonAdapter recyclerViewMissingPersonAdapter =
                new RecyclerViewMissingPersonAdapter(missingPersonReportArrayList, getContext());
        mRecyclerViewMissingPerson.setAdapter(recyclerViewMissingPersonAdapter);
    }
}
