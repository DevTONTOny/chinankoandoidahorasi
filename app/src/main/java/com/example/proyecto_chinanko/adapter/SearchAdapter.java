package com.example.proyecto_chinanko.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_chinanko.R;
import com.example.proyecto_chinanko.dto.InterestPointResponse;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<InterestPointResponse> results;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(InterestPointResponse punto);
    }

    public SearchAdapter(List<InterestPointResponse> results, OnItemClickListener listener) {
        this.results = results;
        this.listener = listener;
    }

    public void updateData(List<InterestPointResponse> newResults) {
        this.results = newResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new SearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        InterestPointResponse punto = results.get(position);
        holder.tvName.setText(punto.getName());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(punto));
    }

    @Override
    public int getItemCount() {
        return results != null ? results.size() : 0;
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSearchItemName);
        }
    }
}