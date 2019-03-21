package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import java.util.ArrayList;
import tweather.framgia.com.crimeandmissingreport.Adapter.RecyclerViewMissingPersonAdapter;
import tweather.framgia.com.crimeandmissingreport.Object.Report;
import tweather.framgia.com.crimeandmissingreport.R;

public class MissingPersonListFragment extends Fragment {
    RecyclerView mRecyclerViewMissingPerson;
    public static ArrayList<Report> missingPersonReportArrayList;
    ScrollView mScrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missing_person_list, container, false);

        missingPersonReportArrayList = new ArrayList<>();
        mScrollView = view.findViewById(R.id.scrollViewMissingPersonList);
        initRecyclerViewMissingPerson(view);
        return view;
    }

    private void initRecyclerViewMissingPerson(View view) {
        mRecyclerViewMissingPerson = view.findViewById(R.id.recyclerViewMissingPerson);
        mRecyclerViewMissingPerson.setHasFixedSize(true);
        mRecyclerViewMissingPerson.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));
        missingPersonReportArrayList.add(new Report.CrimeReportBuilder().setId(1)
                .setTitle("Trộm xe đạp")
                .setDescription("Một thanh niên khoảng 25t đã vào nhà ăn trộm xe đạp")
                .setTime("01/04/2019")
                .setImage(R.drawable.image7)
                .setStatus(false)
                .build());

        missingPersonReportArrayList.add(new Report.CrimeReportBuilder().setId(2)
                .setTitle("Tấn công tạp hóa")
                .setDescription("Một thanh niên cao to mặc áo da đen đã tấn công tiệm tạp hóa")
                .setTime("01/04/2019")
                .setImage(R.drawable.image6)
                .setStatus(false)
                .build());

        missingPersonReportArrayList.add(new Report.CrimeReportBuilder().setId(3)
                .setTitle("Ăn trộm nhà dân")
                .setDescription("Tối ngày 20/3/2019 tôi đã thấy 1 thanh niên")
                .setTime("31/03/2019")
                .setImage(R.drawable.image8)
                .setStatus(false)
                .build());

        missingPersonReportArrayList.add(new Report.CrimeReportBuilder().setId(4)
                .setTitle("Băng nhóm đánh hội đồng 1 thanh niên")
                .setDescription("Vào trưa ngày 21/3/2019, 1 nhóm thanh niên")
                .setTime("31/03/2019")
                .setImage(R.drawable.image7)
                .setStatus(false)
                .build());

        missingPersonReportArrayList.add(new Report.CrimeReportBuilder().setId(5)
                .setTitle("Trộm xe đạp")
                .setDescription("Một thanh niên khoảng 25t đã vào nhà ăn trộm xe đạp")
                .setTime("01/04/2019")
                .setImage(R.drawable.image6)
                .setStatus(false)
                .build());

        missingPersonReportArrayList.add(new Report.CrimeReportBuilder().setId(6)
                .setTitle("Ăn trộm nhà dân")
                .setDescription("Tối ngày 20/3/2019 tôi đã thấy 1 thanh niên")
                .setTime("01/04/2019")
                .setImage(R.drawable.image7)
                .setStatus(false)
                .build());

        RecyclerViewMissingPersonAdapter recyclerViewMissingPersonAdapter =
                new RecyclerViewMissingPersonAdapter(missingPersonReportArrayList, getContext());
        mRecyclerViewMissingPerson.setAdapter(recyclerViewMissingPersonAdapter);
    }
}
