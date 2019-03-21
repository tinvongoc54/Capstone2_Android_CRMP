package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import tweather.framgia.com.crimeandmissingreport.R;

public class MissingPersonReportFragment extends Fragment {

    EditText mEditTextTitle, mEditTextDescription;
    Button mButtonSelectImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missing_person_report, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mEditTextTitle = view.findViewById(R.id.editTextTitle);
        mEditTextDescription = view.findViewById(R.id.editTextDescription);
        mButtonSelectImage = view.findViewById(R.id.buttonSelectImage);
    }
}
