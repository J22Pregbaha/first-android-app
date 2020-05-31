package com.example.keepfit.historyPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepfit.R;
import com.example.keepfit.models.CurrentGoal;

public class HistoryRecyclerAdapter extends ListAdapter<CurrentGoal, HistoryRecyclerAdapter.goalHolder> {

    private OnItemClickListener listener;

    public HistoryRecyclerAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<CurrentGoal> DIFF_CALLBACK = new DiffUtil.ItemCallback<CurrentGoal>() {
        @Override
        public boolean areItemsTheSame(@NonNull CurrentGoal oldItem, @NonNull CurrentGoal newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CurrentGoal oldItem, @NonNull CurrentGoal newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getSteps()==newItem.getSteps() &&
                    oldItem.getInput()==newItem.getInput() &&
                    oldItem.getDate().equals(newItem.getDate());
        }
    };

    @NonNull
    @Override
    public goalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new goalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull goalHolder holder, int position) {
        CurrentGoal currentGoal = getItem(position);
        holder.textViewName.setText(currentGoal.getName());
        holder.textViewSteps.setText(currentGoal.getInput() + "/" + currentGoal.getSteps() + " steps");
        holder.textViewDate.setText(currentGoal.getDate());
        int percent = (currentGoal.getInput() * 100) / currentGoal.getSteps();
        holder.textViewPercentage.setText(percent + "%");
        if (percent >= 100){
            holder.starOn.setVisibility(View.VISIBLE);
            holder.starOff.setVisibility(View.GONE);
        } else {
            holder.starOn.setVisibility(View.GONE);
            holder.starOff.setVisibility(View.VISIBLE);
        }
    }

    class goalHolder extends RecyclerView.ViewHolder{
        private TextView textViewName;
        private TextView textViewSteps;
        private TextView textViewDate;
        private TextView textViewPercentage;
        private ImageView starOn;
        private ImageView starOff;

        public goalHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.goal_name);
            textViewSteps = itemView.findViewById(R.id.number_of_steps);
            textViewDate = itemView.findViewById(R.id.date);
            textViewPercentage = itemView.findViewById(R.id.percentage);
            starOn = itemView.findViewById(R.id.star_on);
            starOff = itemView.findViewById(R.id.star_off);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener !=null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(CurrentGoal currentGoal);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
