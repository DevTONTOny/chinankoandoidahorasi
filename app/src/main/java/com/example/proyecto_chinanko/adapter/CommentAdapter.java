package com.example.proyecto_chinanko.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.example.proyecto_chinanko.dto.CommentResponse;
import com.example.proyecto_chinanko.R;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<CommentResponse> commentList;

    public CommentAdapter(List<CommentResponse> commentList) {
        this.commentList = commentList;
    }

    public void updateComments(List<CommentResponse> newComments) {
        this.commentList.clear();
        this.commentList.addAll(newComments);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentResponse comment = commentList.get(position);

        // Asignar datos
        String name = comment.getUserUsername() != null ? comment.getUserUsername() : "Usuario";
        holder.tvName.setText(name);
        holder.tvAvatar.setText(name.substring(0, 1).toUpperCase());
        holder.tvContent.setText(comment.getContent());
        holder.tvRating.setText(String.valueOf(comment.getRating() + ".0"));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvContent, tvRating;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatarInitial);
            tvName = itemView.findViewById(R.id.tvCommentName);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
            tvRating = itemView.findViewById(R.id.tvCommentRating);
        }
    }
}