package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Objects;
import tweather.framgia.com.crimeandmissingreport.R;

public class ProfileFragment extends Fragment {
    ImageView mImageView;
    TextView mTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mImageView = view.findViewById(R.id.imageViewAvatar);
        mTextView = view.findViewById(R.id.textViewNameUser);
        mTextView.setText(Objects.requireNonNull(getContext())
                .getSharedPreferences("dataLogin", Context.MODE_PRIVATE)
                .getString("fullName", ""));
    }
}
