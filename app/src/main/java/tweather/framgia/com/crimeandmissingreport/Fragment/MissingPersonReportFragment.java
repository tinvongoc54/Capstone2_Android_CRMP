package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Objects;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Activity.LoginDialog;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class MissingPersonReportFragment extends Fragment {

    EditText mEditTextTitle, mEditTextDescription;
    Button mButtonSelectImage, mButtonPost;
    private ProgressDialog mProgressDialog = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,

            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missing_person_report, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        mEditTextTitle = view.findViewById(R.id.editTextTitle);
        mEditTextDescription = view.findViewById(R.id.editTextDescription);
        mButtonSelectImage = view.findViewById(R.id.buttonSelectImage);
        mButtonPost = view.findViewById(R.id.buttonPost);
    }

    private void initEvent() {
        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPostButton();
            }
        });
    }

    private void clickPostButton() {
        if (!LoginDialog.isLogged) {
            Toast.makeText(getContext(), "You are not logged in!", Toast.LENGTH_SHORT).show();
            new LoginDialog(getContext()).clickButtonProfile();
            return;
        }

        if (!validate()) {
            Toast.makeText(getContext(), "Post failure!", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.show();

        Call<JSONObject> callReport = APIUtils.getData()
                .CreateMissingPerson(mEditTextTitle.getText().toString(),
                        mEditTextDescription.getText().toString(),
                        Objects.requireNonNull(getContext())
                                .getSharedPreferences("dataLogin", Context.MODE_PRIVATE)
                                .getString("phoneNumber", ""), Objects.requireNonNull(getContext())
                                .getSharedPreferences("dataLogin", Context.MODE_PRIVATE)
                                .getInt("idUser", 1000)

                );
        callReport.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call,
                    @NonNull Response<JSONObject> response) {
                if (response.body() != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                    Toast.makeText(getContext(), "Post successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Post failure!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable t) {
                Log.d("checkFailPostReport", t.getMessage());
                Toast.makeText(getContext(), "Post failure!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        if (mEditTextTitle.getText().toString().isEmpty()) {
            mEditTextTitle.setError("Title can't be empty!");
            valid = false;
        } else if (mEditTextTitle.getText().toString().length() < 6) {
            mEditTextTitle.setError("Title must not be less than 6 characters!");
            valid = false;
        } else {
            mEditTextTitle.setError(null);
        }

        if (mEditTextDescription.getText().toString().isEmpty()) {
            mEditTextDescription.setError("Description can't be empty!");
            valid = false;
        } else if (mEditTextDescription.getText().toString().length() < 6) {
            mEditTextDescription.setError("Description must not be less than 6 characters!");
            valid = false;
        } else {
            mEditTextDescription.setError(null);
        }

        return valid;
    }
}
