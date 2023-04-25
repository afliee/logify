package com.example.logify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logify.R;
import com.example.logify.entities.Category;
import com.example.logify.entities.Topic;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private ArrayList<Category> categories;

    public CategoryAdapter(Context context, ArrayList<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    public CategoryAdapter(Context context) {
        this.context = context;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        if (category == null) {
            return;
        }

        /**
         * set up the recycler view for each category
         */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.rcvCategory.setLayoutManager(linearLayoutManager);

        holder.tvCategoryName.setText(category.getName());

        TopicAdapter topicAdapter = new TopicAdapter(context);
        topicAdapter.setTopics(category.getTopics());
        holder.rcvCategory.setAdapter(topicAdapter);
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    /**
     * CategoryViewHolder
     * this class is used to hold the view of each item in the recycler view
     */
    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName;
        private RecyclerView rcvCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            rcvCategory = itemView.findViewById(R.id.rcvCategory);
        }
    }
}
