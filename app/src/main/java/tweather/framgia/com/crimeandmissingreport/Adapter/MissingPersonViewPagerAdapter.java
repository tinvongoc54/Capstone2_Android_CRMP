package tweather.framgia.com.crimeandmissingreport.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import tweather.framgia.com.crimeandmissingreport.Fragment.MissingPersonListFragment;
import tweather.framgia.com.crimeandmissingreport.Fragment.MissingPersonReportFragment;

public class MissingPersonViewPagerAdapter extends FragmentStatePagerAdapter{
    public MissingPersonViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch (i) {
            case 0:
                fragment = new MissingPersonListFragment();
                break;
            case 1:
                fragment = new MissingPersonReportFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "List";
                break;
            case 1:
                title = "Report";
                break;
        }
        return title;
    }
}
