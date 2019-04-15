package tweather.framgia.com.crimeandmissingreport.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Object.User;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class LoginDialog {

    public static final String SHAREDPREFERENCES_EMAIL = "email";
    public static final String SHAREDPREFERENCES_PASSWORD = "password";
    public static final String SHAREDPREFERENCES_ID_USER = "idUser";
    public static final String SHAREDPREFERENCES_FULLNAME = "fullName";
    public static final String SHAREDPREFERENCES_PHONE_NUMBER = "phoneNumber";
    public static final String SHAREDPREFERENCES_ADDRESS = "address";
    public static final String SHAREDPREFERENCES = "dataLogin";

    private Button mButtonLogin;
    @SuppressLint("StaticFieldLeak")

    public static EditText mEditTextEmail, mEditTextPassword;
    private CheckBox mCheckBox;
    private TextView mTextViewSignUp;
    private ProgressDialog mProgressDialog = null;
    private Dialog mDialog;
    private Context mContext;
    public static SharedPreferences sharedPreferences;
    public static boolean isLogged = false;

    private static final String TAG = "LoginDialog";

    public LoginDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext);
    }

    @SuppressLint("ResourceAsColor")
    public void clickButtonProfile() {
        mDialog.setContentView(R.layout.dialog_login);
        initViewDialog();
        initEvent();
        sharedPreferences = mContext.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE);

        mDialog.show();
    }

    private void initViewDialog() {
        mButtonLogin = mDialog.findViewById(R.id.buttonLogin);
        mEditTextEmail = mDialog.findViewById(R.id.editTextEmail);
        mEditTextPassword = mDialog.findViewById(R.id.editTextPassword);
        mTextViewSignUp = mDialog.findViewById(R.id.textViewSignup);
        mCheckBox = mDialog.findViewById(R.id.checkboxRemember);
    }

    private void initEvent() {
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        mTextViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpDialog signUpDialog = new SignUpDialog(mContext);
                signUpDialog.signUp();
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        mButtonLogin.setEnabled(false);

        mProgressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Authenticating...");
        mProgressDialog.show();

        Call<List<User>> call = APIUtils.getData(APIUtils.BASE_URL)
                .RequestLogin(mEditTextEmail.getText().toString(),
                        mEditTextPassword.getText().toString());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call,
                    @NonNull Response<List<User>> response) {
                if (response.body() != null) {
                    if (response.body().get(0).getMessage().equals("success")) {
                        onLoginSuccess();
                        isLogged = true;
                        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor =
                                sharedPreferences.edit();
                        editor.putString(SHAREDPREFERENCES_EMAIL, mEditTextEmail.getText().toString());
                        editor.putString(SHAREDPREFERENCES_PASSWORD, mEditTextPassword.getText().toString());
                        editor.putInt(SHAREDPREFERENCES_ID_USER, response.body().get(0).getId());
                        editor.putString(SHAREDPREFERENCES_FULLNAME, response.body().get(0).getFullname());
                        editor.putString(SHAREDPREFERENCES_PHONE_NUMBER, response.body().get(0).getPhoneNumber());
                        editor.putString(SHAREDPREFERENCES_ADDRESS, response.body().get(0).getAddress());
                        if (mCheckBox.isChecked()) {
                            Log.d("checkRemember1", String.valueOf(mCheckBox.isChecked()));
                            editor.putBoolean("save", true);
                        } else {
                            Log.d("checkRemember3", String.valueOf(mCheckBox.isChecked()));
                            editor.putBoolean("save", false);
                        }
                        editor.apply();
                    } else {
                        onLoginFailed();
                    }
                }
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                Log.d("checkFail", t.getMessage());
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        });
    }

    private void onLoginSuccess() {
        mButtonLogin.setEnabled(true);
        Toast.makeText(mContext, "Login success!", Toast.LENGTH_LONG).show();
        mDialog.dismiss();
    }

    private void onLoginFailed() {
        Toast.makeText(mContext, "Username or password incorrect!", Toast.LENGTH_LONG).show();
        mButtonLogin.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String email = mEditTextEmail.getText().toString();
        String password = mEditTextPassword.getText().toString();

        if (email.isEmpty()) {
            mEditTextEmail.setError("Email can't be empty!");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEditTextEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            mEditTextEmail.setError(null);
        }

        if (password.isEmpty()) {
            mEditTextPassword.setError("Password can't be empty!");
            valid = false;
        } else if (password.length() < 4 || password.length() > 10) {
            mEditTextPassword.setError("Between 6 and 20 alphanumeric characters!");
            valid = false;
        } else {
            mEditTextPassword.setError(null);
        }

        return valid;
    }
}
