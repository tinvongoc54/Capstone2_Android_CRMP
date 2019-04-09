package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewMissingPersonAdapter;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class MissingPersonListFragment extends Fragment {
    RecyclerView mRecyclerViewMissingPerson;
    public static ArrayList<Report> missingPersonReportArrayList = new ArrayList<>();
    ScrollView mScrollView;
    View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_missing_person_list, container, false);
        getMissingPersonReportList();
        mScrollView = mView.findViewById(R.id.scrollViewMissingPersonList);
        initRecyclerViewMissingPerson(mView);
        return mView;
    }

    private void getMissingPersonReportList() {
        Call<List<Report>> call =
                APIUtils.getData().GetMissingReportList(APIUtils.API_GET_MISSING_PERSON_LIST_URL);
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(@NonNull Call<List<Report>> call, @NonNull Response<List<Report>> response) {
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
//        missingPersonReportArrayList.add(new Report.CrimeReportBuilder().setId(1)
//                .setTitle("Trộm xe đạp")
//                .setDescription("Một thanh niên khoảng 25t đã vào nhà ăn trộm xe đạp")
//                .setTime("01/04/2019")
//                .setImage(R.drawable.image7)
//                .setStatus(false)
//                .build());
//
//        missingPersonReportArrayList.add(new Report.CrimeReportBuilder().setId(2)
//                .setTitle("Tấn công tạp hóa")
//                .setDescription("Một thanh niên cao to mặc áo da đen đã tấn công tiệm tạp hóa")
//                .setTime("01/04/2019")
//                .setImage(R.drawable.image6)
//                .setStatus(false)
//                .build());
//
//        missingPersonReportArrayList.add(new Report.CrimeReportBuilder().setId(3)
//                .setTitle("Ăn trộm nhà dân")
//                .setDescription("Tối ngày 20/3/2019 tôi đã thấy 1 thanh niên")
//                .setTime("31/03/2019")
//                .setImage(R.drawable.image8)
//                .setStatus(false)
//                .build());
//
//        missingPersonReportArrayList.add(new Report.CrimeReportBuilder().setId(4)
//                .setTitle("Băng nhóm đánh hội đồng 1 thanh niên")
//                .setDescription("Vào trưa ngày 21/3/2019, 1 nhóm thanh niên")
//                .setTime("31/03/2019")
//                .setImage(R.drawable.image7)
//                .setStatus(false)
//                .build());
//
//        missingPersonReportArrayList.add(new Report.CrimeReportBuilder().setId(5)
//                .setTitle("Trộm xe đạp")
//                .setDescription("Một thanh niên khoảng 25t đã vào nhà ăn trộm xe đạp")
//                .setTime("01/04/2019")
//                .setImage(R.drawable.image6)
//                .setStatus(false)
//                .build());
//
//        missingPersonReportArrayList.add(new Report.CrimeReportBuilder().setId(6)
//                .setTitle("Ăn trộm nhà dân")
//                .setDescription("Tối ngày 20/3/2019 tôi đã thấy 1 thanh niên")
//                .setTime("01/04/2019")
//                .setImage(R.drawable.image7)
//                .setStatus(false)
//                .build());

        RecyclerViewMissingPersonAdapter recyclerViewMissingPersonAdapter =
                new RecyclerViewMissingPersonAdapter(missingPersonReportArrayList, getContext());
        mRecyclerViewMissingPerson.setAdapter(recyclerViewMissingPersonAdapter);
    }
}
