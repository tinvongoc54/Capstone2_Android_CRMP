package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import tweather.framgia.com.crimeandmissingreport.Adapter.CrimeViewPagerAdapter;
import tweather.framgia.com.crimeandmissingreport.R;

public class CrimeFragment extends Fragment {

    ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        initView(view);
        return view;
    }

    public void initView(View view) {
        ((NestedScrollView) view.findViewById(R.id.nestedScrollView)).setFillViewport(true);

        mViewPager = view.findViewById(R.id.viewPager);

        mViewPager.setAdapter(new CrimeViewPagerAdapter(getFragmentManager()));
        ((TabLayout) view.findViewById(R.id.tabLayout)).setupWithViewPager(mViewPager);
    }
}
