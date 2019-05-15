package tweather.framgia.com.crimeandmissingreport.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tweather.framgia.com.crimeandmissingreport.Activity.LoginDialog;
import tweather.framgia.com.crimeandmissingreport.Object.Comment;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class RecyclerViewCommentAdapter
        extends RecyclerView.Adapter<RecyclerViewCommentAdapter.ViewHolder> {

    private List<Comment> mCommentList;
    private Context mContext;
    private boolean mIsCrimeReport;
    private int mUserIdOfReport;

    public RecyclerViewCommentAdapter(List<Comment> commentList, Context context,
            boolean isCrimeReport, int userId) {
        mCommentList = commentList;
        mContext = context;
        mIsCrimeReport = isCrimeReport;
        mUserIdOfReport = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_recyclerview_comment_report, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        if (mIsCrimeReport) {
            viewHolder.mTextViewUsername.setText(
                    "User" + Integer.toString(mCommentList.get(i).getUserId()));
        } else {
            viewHolder.mTextViewUsername.setText(mCommentList.get(i).getUsername());
        }
        viewHolder.mTextViewTime.setText(APIUtils.convertTime(mCommentList.get(i).getCreatedAt()));
        viewHolder.mTextViewContent.setText(mCommentList.get(i).getContent());

        //kiểm tra nếu là chủ bài post thì show function edit, delete tất cả comment
        if (LoginDialog.isLogged) {
            int userIdSharedPreferences =
                    mContext.getSharedPreferences("dataLogin", Context.MODE_PRIVATE)
                            .getInt(LoginDialog.SHAREDPREFERENCES_ID_USER, 0);
            if (mUserIdOfReport != userIdSharedPreferences) {
                if (mCommentList.get(i).getUserId() != userIdSharedPreferences) {
                    hideEditAndDeleteFunction(viewHolder);
                }
            } else {
                if (mCommentList.get(i).getUserId() != userIdSharedPreferences) {
                    viewHolder.mTextViewEdit.setVisibility(View.INVISIBLE);
                    viewHolder.mTextViewEdit.setEnabled(false);
                    viewHolder.mTextViewDelete.setVisibility(View.VISIBLE);
                    viewHolder.mTextViewDelete.setEnabled(true);
                }
            }
        } else {
            hideEditAndDeleteFunction(viewHolder);
        }

        viewHolder.mTextViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmUpdate(i, mCommentList.get(i).getId());
            }
        });
        viewHolder.mTextViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete(mCommentList.get(i).getId());
            }
        });
    }

    private void deleteMissingComment(int missingId) {
        Call<JSONObject> call = APIUtils.getData(APIUtils.BASE_URL).DeleteMissingComment(missingId);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call,
                    @NonNull Response<JSONObject> response) {
                if (response.body() != null) {
                    intentResult("LoadMissingComment");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable throwable) {

            }
        });
    }

    private void deleteCrimeComment(int crimeId) {
        Call<JSONObject> call = APIUtils.getData(APIUtils.BASE_URL).DeleteCrimeComment(crimeId);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call,
                    @NonNull Response<JSONObject> response) {
                if (response.body() != null) {
                    intentResult("LoadCrimeComment");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable throwable) {

            }
        });
    }

    private void intentResult(String action) {
        Intent intent = new Intent(action);
        intent.putExtra("confirm", true);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void updateMissingComment(int idComment, String content) {
        Call<JSONObject> call =
                APIUtils.getData(APIUtils.BASE_URL).UpdateMissingComment(idComment, content);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call, @NonNull Response<JSONObject> response) {
                if (response.body() != null) {
                    intentResult("LoadMissingComment");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable throwable) {

            }
        });
    }

    private void updateCrimeComment(int idComment, String content) {
        Call<JSONObject> call =
                APIUtils.getData(APIUtils.BASE_URL).UpdateCrimeComment(idComment, content);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call, @NonNull Response<JSONObject> response) {
                if (response.body() != null) {
                    intentResult("LoadCrimeComment");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable throwable) {

            }
        });
    }

    private void hideEditAndDeleteFunction(ViewHolder viewHolder) {
        viewHolder.mTextViewEdit.setVisibility(View.INVISIBLE);
        viewHolder.mTextViewEdit.setEnabled(false);
        viewHolder.mTextViewDelete.setVisibility(View.INVISIBLE);
        viewHolder.mTextViewDelete.setEnabled(false);
    }

    private void confirmDelete(final int id) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(mContext, R.style.AppTheme_Dark_Dialog);
        builder.setMessage("Do you want to delete this comment?")
                .setCancelable(false)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mIsCrimeReport) {
                            deleteCrimeComment(id);
                        } else {
                            deleteMissingComment(id);
                        }
                    }
                })
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Question");

        alertDialog.show();
    }

    private void confirmUpdate(int position, final int idComment) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_edit_comment);
        final EditText editText = dialog.findViewById(R.id.editTextEditComment);
        editText.setText(mCommentList.get(position).getContent());
        Button button = dialog.findViewById(R.id.buttonEditComment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (mIsCrimeReport) {
                    updateCrimeComment(idComment, editText.getText().toString());
                } else {
                    updateMissingComment(idComment, editText.getText().toString());
                }
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewUsername, mTextViewTime, mTextViewContent, mTextViewEdit, mTextViewDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewUsername = itemView.findViewById(R.id.textViewUsername);
            mTextViewTime = itemView.findViewById(R.id.textViewTime);
            mTextViewContent = itemView.findViewById(R.id.textViewContent);
            mTextViewEdit = itemView.findViewById(R.id.textViewEdit);
            mTextViewDelete = itemView.findViewById(R.id.textViewDelete);
        }
    }
}
