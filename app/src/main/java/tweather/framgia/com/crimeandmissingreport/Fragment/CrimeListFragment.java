package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.annotation.SuppressLint;
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
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewSpecialNewsAdapter;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewNewsAdapter;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class CrimeListFragment extends Fragment {
    RecyclerView mRecyclerViewSpecial, mRecyclerViewNew;
    public static ArrayList<Report> crimeReportArrayList = new ArrayList<>();
    ScrollView mScrollView;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        getCrimeReportList();
        mScrollView = view.findViewById(R.id.scrollViewCrimeList);
        return view;
    }

    private void getCrimeReportList() {
        Call<List<Report>> callList =
                APIUtils.getData().GetCrimeReportList(APIUtils.API_GET_CRIME_REPORT_LIST_URL);
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
        //        crimeReportArrayList.add(new Report.CrimeReportBuilder().setId(1)
        //                .setTitle("Trộm xe đạp")
        //                .setDescription("Một thanh niên khoảng 25t đã vào nhà ăn trộm xe đạp")
        //                .setCrimeCategory(40)
        //                .setArea("Hải Châu")
        //                .setTime("01/04/2019")
        //                .setImage(R.drawable.image7)
        //                .build());
        //
        //        crimeReportArrayList.add(new Report.CrimeReportBuilder().setId(2)
        //                .setTitle("Tấn công tạp hóa")
        //                .setDescription("Một thanh niên cao to mặc áo da đen đã tấn công tiệm
        // tạp hóa")
        //                .setCrimeCategory(41)
        //                .setArea("Thanh Khê")
        //                .setTime("31/03/2019")
        //                .setImage(R.drawable.image6)
        //                .build());
        //
        //        crimeReportArrayList.add(new Report.CrimeReportBuilder().setId(3)
        //                .setTitle("Ăn trộm nhà dân")
        //                .setDescription("Tối ngày 20/3/2019 tôi đã thấy 1 thanh niên")
        //                .setCrimeCategory(42)
        //                .setArea("Sơn Trà")
        //                .setTime("31/03/2019")
        //                .setImage(R.drawable.image8)
        //                .build());
        //
        //        crimeReportArrayList.add(new Report.CrimeReportBuilder().setId(4)
        //                .setTitle("Băng nhóm đánh hội đồng 1 thanh niên")
        //                .setDescription("Vào trưa ngày 21/3/2019, 1 nhóm thanh niên")
        //                .setCrimeCategory(42)
        //                .setArea("Thanh Khê")
        //                .setTime("31/03/2019")
        //                .setImage(R.drawable.image7)
        //                .build());
        //
        //        crimeReportArrayList.add(new Report.CrimeReportBuilder().setId(5)
        //                .setTitle("Trộm xe đạp")
        //                .setDescription("Một thanh niên khoảng 25t đã vào nhà ăn trộm xe đạp")
        //                .setCrimeCategory(41)
        //                .setArea("Hải Châu")
        //                .setTime("01/04/2019")
        //                .setImage(R.drawable.image6)
        //                .build());
        //
        //        crimeReportArrayList.add(new Report.CrimeReportBuilder().setId(6)
        //                .setTitle("Ăn trộm nhà dân")
        //                .setDescription("Tối ngày 20/3/2019 tôi đã thấy 1 thanh niên")
        //                .setCrimeCategory(41)
        //                .setArea("Sơn Trà")
        //                .setTime("01/04/2019")
        //                .setImage(R.drawable.image7)
        //                .build());

        RecyclerViewNewsAdapter recyclerViewNewsAdapter =
                new RecyclerViewNewsAdapter(crimeReportArrayList, getContext());
        mRecyclerViewNew.setAdapter(recyclerViewNewsAdapter);
    }

    public void initRecyclerViewSpecial(View view) {
        mRecyclerViewSpecial = view.findViewById(R.id.recyclerViewSpecial);
        mRecyclerViewSpecial.setHasFixedSize(true);
        mRecyclerViewSpecial.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL, false));
        //        crimeReportArrayList.add(new Report.CrimeReportBuilder().setId(7)
        //                .setTitle("Hacker đánh sập mạng Viettel")
        //                .setDescription("1 hacker đã phá hủy mạng của Viettel")
        //                .setCrimeCategory(41)
        //                .setArea("Thanh Khê")
        //                .setTime("01/04/2019")
        //                .setImage(R.drawable.image6)
        //                .build());
        //
        //        crimeReportArrayList.add(new Report.CrimeReportBuilder().setId(8)
        //                .setTitle("Truy nã hung thủ giết người")
        //                .setDescription("Nhiều người nhìn thấy 1 thanh niên mặc đồ đen tay cầm
        // dao")
        //                .setCrimeCategory(42)
        //                .setArea("Cẩm Lệ")
        //                .setTime("01/04/2019")
        //                .setImage(R.drawable.image8)
        //                .build());
        //
        //        crimeReportArrayList.add(new Report.CrimeReportBuilder().setId(9)
        //                .setTitle("Cướp xe taxi")
        //                .setDescription("Vào lúc khoảng 23h đã xảy ra 1 vụ cướp trắng trợn")
        //                .setCrimeCategory(41)
        //                .setArea("Liên Chiểu")
        //                .setTime("01/04/2019")
        //                .setImage(R.drawable.image7)
        //                .build());
        //
        //        crimeReportArrayList.add(new Report.CrimeReportBuilder().setId(10)
        //                .setTitle("Hacker đánh sập mạng Viettel")
        //                .setDescription("1 hacker đã phá hủy mạng của Viettel")
        //                .setCrimeCategory("Hacker")
        //                .setArea("Thanh Khê")
        //                .setTime("01/04/2019")
        //                .setImage(R.drawable.image8)
        //                .build());
        //
        //        crimeReportArrayList.add(new Report.CrimeReportBuilder().setId(11)
        //                .setTitle("Truy nã hung thủ giết người")
        //                .setDescription("Nhiều người nhìn thấy 1 thanh niên mặc đồ đen tay cầm dao")
        //                .setCrimeCategory("Giết người")
        //                .setArea("Cẩm Lệ")
        //                .setTime("01/04/2019")
        //                .setImage(R.drawable.image7)
        //                .build());

        RecyclerViewSpecialNewsAdapter recyclerViewSpecialNewsAdapter =
                new RecyclerViewSpecialNewsAdapter(crimeReportArrayList, getContext());
        mRecyclerViewSpecial.setAdapter(recyclerViewSpecialNewsAdapter);
    }
}
