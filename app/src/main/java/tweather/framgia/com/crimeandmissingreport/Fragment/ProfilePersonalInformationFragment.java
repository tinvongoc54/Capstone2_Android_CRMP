package tweather.framgia.com.crimeandmissingreport.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Activity.LoginDialog;
import tweather.framgia.com.crimeandmissingreport.Object.User;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class ProfilePersonalInformationFragment extends Fragment implements View.OnClickListener {
    SharedPreferences mSharedPreferences;
    TextView mTextViewEmail, mTextViewAddress, mTextViewPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view =
                inflater.inflate(R.layout.fragment_profile_personal_information, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mSharedPreferences = Objects.requireNonNull(getContext())
                .getSharedPreferences(LoginDialog.SHAREDPREFERENCES, Context.MODE_PRIVATE);

        mTextViewEmail = view.findViewById(R.id.textViewEmailProfile);
        mTextViewEmail.setText(
                mSharedPreferences.getString(LoginDialog.SHAREDPREFERENCES_EMAIL, ""));

        mTextViewAddress = view.findViewById(R.id.textViewAdressProfile);
        mTextViewAddress.setText(
                mSharedPreferences.getString(LoginDialog.SHAREDPREFERENCES_ADDRESS, ""));

        mTextViewPhone = view.findViewById(R.id.textViewPhoneProfile);
        mTextViewPhone.setText(
                mSharedPreferences.getString(LoginDialog.SHAREDPREFERENCES_PHONE_NUMBER, ""));

        view.findViewById(R.id.buttonEditProfile).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonEditProfile:
                EditDialog editDialog = new EditDialog(getContext());
                editDialog.showEditDialog();
                break;
            default:
                break;
        }
    }

    private class EditDialog {
        private Button mButtonEdit;
        private EditText mEditTextEmail, mEditTextPassword, mEditTextPasswordConfirm,
                mEditTextFullName, mEditTextPhoneNumber, mEditTextAddress;
        private ProgressDialog mProgressDialog = null;
        private Dialog mDialog;
        private Context mContext;

        public EditDialog(Context context) {
            mContext = context;
            mDialog = new Dialog(mContext);
        }

        @SuppressLint("CutPasteId")
        public void showEditDialog() {
            mDialog.setContentView(R.layout.dialog_profile_edit_personal_information);
            initView();
            iniEvent();
            mDialog.show();
        }

        private void iniEvent() {
            mButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validate()) {
                        updateProfile();
                    }
                }
            });
        }

        private void updateProfile() {
            mProgressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Processing...");
            mProgressDialog.show();

            Call<User> callUpdate = APIUtils.getData(APIUtils.API_UPDATE_USER_URL)
                    .UpdateUserProfile(
                            mSharedPreferences.getInt(LoginDialog.SHAREDPREFERENCES_ID_USER, 10000),
                            mEditTextEmail.getText().toString(),
                            mEditTextPassword.getText().toString(),
                            mEditTextFullName.getText().toString(),
                            mEditTextPhoneNumber.getText().toString(),
                            mEditTextAddress.getText().toString());
            callUpdate.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                    if (response.body() != null) {
                        Toast.makeText(mContext, "Update successfully!", Toast.LENGTH_SHORT).show();

                        mTextViewEmail.setText(response.body().getEmail());
                        mTextViewAddress.setText(response.body().getAddress());
                        mTextViewPhone.setText(response.body().getPhoneNumber());

                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.clear();
                        editor.putString(LoginDialog.SHAREDPREFERENCES_EMAIL,
                                mTextViewEmail.getText().toString());
                        editor.putString(LoginDialog.SHAREDPREFERENCES_PASSWORD,
                                mEditTextPassword.getText().toString());

                        editor.putString(LoginDialog.SHAREDPREFERENCES_FULLNAME,
                                response.body().getFullname());

                        editor.putString(LoginDialog.SHAREDPREFERENCES_ADDRESS,
                                mTextViewAddress.getText().toString());
                        editor.putString(LoginDialog.SHAREDPREFERENCES_PHONE_NUMBER,
                                mTextViewPhone.getText().toString());
                        editor.apply();

                        ProfileFragment.mTextViewFullName.setText(
                                mSharedPreferences.getString(LoginDialog.SHAREDPREFERENCES_FULLNAME,
                                        ""));
                        mDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {

                }
            });
        }

        private void initView() {
            mEditTextEmail = mDialog.findViewById(R.id.editTextEmail);
            mEditTextEmail.setText(ProfileFragment.mSharedPreferences.getString(
                    LoginDialog.SHAREDPREFERENCES_EMAIL, ""));
            mEditTextPassword = mDialog.findViewById(R.id.editTextPassword);
            mEditTextPassword.setText(ProfileFragment.mSharedPreferences.getString(
                    LoginDialog.SHAREDPREFERENCES_PASSWORD, ""));
            mEditTextPasswordConfirm = mDialog.findViewById(R.id.editTextPasswordConfirm);
            mEditTextPasswordConfirm.setText("");
            mEditTextFullName = mDialog.findViewById(R.id.editTextFullname);
            mEditTextFullName.setText(ProfileFragment.mSharedPreferences.getString(
                    LoginDialog.SHAREDPREFERENCES_FULLNAME, ""));
            mEditTextAddress = mDialog.findViewById(R.id.editTextAddress);
            mEditTextAddress.setText(ProfileFragment.mSharedPreferences.getString(
                    LoginDialog.SHAREDPREFERENCES_ADDRESS, ""));
            mEditTextPhoneNumber = mDialog.findViewById(R.id.editTextPhoneNumber);
            mEditTextPhoneNumber.setText(ProfileFragment.mSharedPreferences.getString(
                    LoginDialog.SHAREDPREFERENCES_PHONE_NUMBER, ""));

            mButtonEdit = mDialog.findViewById(R.id.buttonEdit);
        }

        private boolean validate() {
            boolean valid = true;

            if (mEditTextEmail.getText().toString().isEmpty()) {
                mEditTextEmail.setError("Email can't be empty!");
                valid = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(
                    mEditTextEmail.getText().toString()).matches()) {
                mEditTextEmail.setError("Enter a valid email address");
                valid = false;
            } else {
                mEditTextEmail.setError(null);
            }

            if (mEditTextPassword.getText().toString().isEmpty()) {
                mEditTextPassword.setError("Password can't be empty!");
                valid = false;
            } else if (mEditTextPassword.getText().toString().length() < 4
                    || mEditTextPassword.getText().toString().length() > 10) {
                mEditTextPassword.setError("Between 6 and 20 alphanumeric characters!");
                valid = false;
            } else {
                mEditTextPassword.setError(null);
            }

            if (mEditTextPasswordConfirm.getText().toString().isEmpty()) {
                mEditTextPasswordConfirm.setError("Confirm password can't be empty!");
            } else if (mEditTextPassword.getText()
                    .toString()
                    .equals(mEditTextPasswordConfirm.getText().toString())) {
                mEditTextPassword.setError(null);
                mEditTextPasswordConfirm.setError(null);
            } else {
                mEditTextPasswordConfirm.setError("Confirm password incorrect!");
                valid = false;
            }

            if (mEditTextFullName.getText().toString().isEmpty()) {
                mEditTextFullName.setError("Fullname can't be empty!");
                valid = false;
            } else {
                mEditTextFullName.setError(null);
            }

            if (mEditTextPhoneNumber.getText().toString().isEmpty()) {
                mEditTextPhoneNumber.setError("Phone number can't be empty!");
                valid = false;
            } else {
                mEditTextPhoneNumber.setError(null);
            }

            if (mEditTextAddress.getText().toString().isEmpty()) {
                mEditTextAddress.setError("Address can't be empty!");
                valid = false;
            } else {
                mEditTextAddress.setError(null);
            }

            if (mEditTextPassword.getText()
                    .toString()
                    .equals(mEditTextPasswordConfirm.getText().toString())) {
                mEditTextPassword.setError(null);
                mEditTextPasswordConfirm.setError(null);
            } else {
                mEditTextPasswordConfirm.setError("Confirm password incorrect!");
                valid = false;
            }

            return valid;
        }
    }
}
