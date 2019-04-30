package tweather.framgia.com.crimeandmissingreport.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import java.util.zip.Inflater;
import tweather.framgia.com.crimeandmissingreport.Object.Comment;
import tweather.framgia.com.crimeandmissingreport.R;
import tweather.framgia.com.crimeandmissingreport.Retrofit.APIUtils;

public class RecyclerViewCommentAdapter
        extends RecyclerView.Adapter<RecyclerViewCommentAdapter.ViewHolder> {

    List<Comment> mCommentList;
    Context mContext;

    public RecyclerViewCommentAdapter(List<Comment> commentList, Context context) {
        mCommentList = commentList;
        mContext = context;
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mTextViewUsername.setText("User" + Integer.toString(mCommentList.get(i).getUserId()));
        viewHolder.mTextViewTime.setText(APIUtils.convertTime(mCommentList.get(i).getCreatedAt()));
        viewHolder.mTextViewContent.setText(mCommentList.get(i).getContent());
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewUsername, mTextViewTime, mTextViewContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewUsername = itemView.findViewById(R.id.textViewUsername);
            mTextViewTime = itemView.findViewById(R.id.textViewTime);
            mTextViewContent = itemView.findViewById(R.id.textViewContent);
        }
    }


}
