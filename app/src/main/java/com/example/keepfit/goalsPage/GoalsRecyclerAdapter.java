package com.example.keepfit.goalsPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepfit.R;
import com.example.keepfit.models.Goals;

public class GoalsRecyclerAdapter extends ListAdapter<Goals, GoalsRecyclerAdapter.goalHolder> {

    private OnItemClickListener listener;

    public GoalsRecyclerAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Goals> DIFF_CALLBACK = new DiffUtil.ItemCallback<Goals>() {
        @Override
        public boolean areItemsTheSame(@NonNull Goals oldItem, @NonNull Goals newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Goals oldItem, @NonNull Goals newItem) {
            return oldItem.getGoal_name().equals(newItem.getGoal_name()) &&
                    oldItem.getActive()==newItem.getActive() &&
                    oldItem.getNumber_of_steps()==newItem.getNumber_of_steps();
        }
    };

    @NonNull
    @Override
    public goalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goal_item, parent, false);
        return new goalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull goalHolder holder, int position) {
        Goals currentGoal = getItem(position);
        holder.textViewName.setText(currentGoal.getGoal_name());
        holder.textViewSteps.setText(currentGoal.getNumber_of_steps() + " steps");
        if (currentGoal.getActive() == 1){
            holder.textViewName.setText(currentGoal.getGoal_name() + "(Active)");
            holder.textViewSteps.setText(currentGoal.getNumber_of_steps() + " steps");
        }
    }

    public Goals getGoalAt(int position){
        return getItem(position);
    }

    class goalHolder extends RecyclerView.ViewHolder{
        private TextView textViewName;
        private TextView textViewSteps;

        public goalHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.goal_name);
            textViewSteps = itemView.findViewById(R.id.number_of_steps);

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
        void onItemClick(Goals goal);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
