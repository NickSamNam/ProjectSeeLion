package com.ags.projectseelion;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by snick on 11-12-2017.
 */

class MonumentAdapter extends RecyclerView.Adapter<MonumentAdapter.MonumentHolder> {
    private List<POI> monuments;
    private OnItemClickListener onItemClickListener;

    public MonumentAdapter(List<POI> monuments) {
        this.monuments = monuments;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MonumentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MonumentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_monument, parent, false));
    }

    @Override
    public void onBindViewHolder(MonumentHolder holder, int position) {
        POI m = monuments.get(position);
        holder.tvName.setText(m.getName());
        holder.checkBoxVisit.setChecked(m.isChosen());
        holder.checkBoxVisit.setOnClickListener((View view) -> m.setChosen(!m.isChosen()));
        holder.root.setOnClickListener((view -> {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(holder.getAdapterPosition());
        }));
    }

    @Override
    public int getItemCount() {
        return monuments.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class MonumentHolder extends RecyclerView.ViewHolder {
        private final View root;
        private final TextView tvName;
        private final CheckBox checkBoxVisit;

        public MonumentHolder(View itemView) {
            super(itemView);
            root = itemView;
            tvName = itemView.findViewById(R.id.item_monument_tv_name);
            checkBoxVisit = itemView.findViewById(R.id.item_monument_checkBox_visit);
        }
    }
}
