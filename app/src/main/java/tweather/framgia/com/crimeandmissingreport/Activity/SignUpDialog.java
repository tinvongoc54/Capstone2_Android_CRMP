package tweather.framgia.com.crimeandmissingreport.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;
import java.util.Objects;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Object.User;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class SignUpDialog {
    private Button mButtonSignUp;
    private EditText mEditTextEmail, mEditTextPassword, mEditTextPasswordConfirm, mEditTextFullName,
            mEditTextPhoneNumber, mEditTextAddress;
    private ProgressDialog mProgressDialog = null;
    private Dialog mDialog;
    private Context mContext;

    SignUpDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext);
    }

    @SuppressLint("ResourceAsColor")
    void signUp() {
        mDialog.setContentView(R.layout.dialog_signup);
        initViewDialog();
        initEvent();

        mDialog.show();
    }

    private void initViewDialog() {
        mButtonSignUp = mDialog.findViewById(R.id.buttonSignUp);
        mEditTextEmail = mDialog.findViewById(R.id.editTextEmail);
        mEditTextPassword = mDialog.findViewById(R.id.editTextPassword);
        mEditTextPasswordConfirm = mDialog.findViewById(R.id.editTextPasswordConfirm);
        mEditTextFullName = mDialog.findViewById(R.id.editTextFullname);
        mEditTextPhoneNumber = mDialog.findViewById(R.id.editTextPhoneNumber);
        mEditTextAddress = mDialog.findViewById(R.id.editTextAddress);
    }

    private void initEvent() {
        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSignUpButton();
            }
        });
    }

    private void clickSignUpButton() {
        if (!validate()) {
            onSignUpFailed();
            return;
        }

        mButtonSignUp.setEnabled(false);

        mProgressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.show();

        Call<JSONObject> call = APIUtils.getData()
                .CreateUser(mEditTextEmail.getText().toString(),
                        mEditTextPassword.getText().toString(),
                        mEditTextPasswordConfirm.getText().toString(),
                        mEditTextFullName.getText().toString(),
                        mEditTextPhoneNumber.getText().toString(),
                        mEditTextAddress.getText().toString(),
                        2);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call,
                    @NonNull Response<JSONObject> response) {
                if (response.body() != null) {
                    onSignUpSuccess();
                } else {
                    onSignUpFailed();
                }
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable t) {
                Log.d("checkFail", t.getMessage());
            }
        });
    }

    private void onSignUpSuccess() {
        mButtonSignUp.setEnabled(true);
        Toast.makeText(mContext, "Registration success!", Toast.LENGTH_LONG).show();
        LoginDialog.mEditTextEmail.setText(mEditTextEmail.getText().toString());
        LoginDialog.mEditTextPassword.setText(mEditTextPassword.getText().toString());
        mDialog.dismiss();
    }

    private void onSignUpFailed() {
        Toast.makeText(mContext, "Registration failed!", Toast.LENGTH_LONG).show();
        mButtonSignUp.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        if (mEditTextEmail.getText().toString().isEmpty()) {
            mEditTextEmail.setError("Email can't be empty!");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEditTextEmail.getText().toString())
                .matches()) {
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
