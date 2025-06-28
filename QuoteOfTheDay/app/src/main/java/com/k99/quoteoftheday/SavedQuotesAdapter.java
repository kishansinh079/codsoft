package com.k99.quoteoftheday;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class SavedQuotesAdapter extends RecyclerView.Adapter<SavedQuotesAdapter.ViewHolder> {

    private final List<String> savedQuotes;
    private final OnQuoteLongClickListener listener;

    public SavedQuotesAdapter(List<String> savedQuotes, OnQuoteLongClickListener listener) {
        this.savedQuotes = savedQuotes;
        this.listener = listener;
    }

    public interface OnQuoteLongClickListener {
        void onQuoteLongClicked(String quote);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuote, tvAuthor;

        public ViewHolder(View itemView) {
            super(itemView);
            tvQuote = itemView.findViewById(R.id.tvSavedQuote);
            tvAuthor = itemView.findViewById(R.id.tvSavedAuthor);
        }
    }

    @NonNull
    @Override
    public SavedQuotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_quote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] parts = savedQuotes.get(position).split("\\|");
        String quote = parts[0].trim();
        String author = parts.length > 1 ? parts[1].trim() : "";

        holder.itemView.setOnLongClickListener(v -> {
            listener.onQuoteLongClicked(savedQuotes.get(position));
            return true;
        });

        holder.tvQuote.setText(quote);
        holder.tvAuthor.setText(author);

        // Generate random gradient
        int startColor = getRandomColor();
        int endColor = getRandomColor();

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.BL_TR,
                new int[]{startColor, endColor}
        );
        gradient.setCornerRadius(20f);
        holder.itemView.setBackground(gradient);

        // Set readable text color
        int avgColor = ColorUtils.blendARGB(startColor, endColor, 0.5f);
        double luminance = ColorUtils.calculateLuminance(avgColor);
        int textColor = luminance < 0.5 ? Color.WHITE : Color.BLACK;

        holder.tvQuote.setTextColor(textColor);
        holder.tvAuthor.setTextColor(textColor);
    }

//    @Override
//    public void onBindViewHolder(@NonNull SavedQuotesAdapter.ViewHolder holder, int position) {
//        String[] parts = savedQuotes.get(position).split("\\|");
//        holder.tvQuote.setText(parts[0].trim());
//        holder.tvAuthor.setText(parts.length > 1 ? parts[1].trim() : "");
//    }

    @Override
    public int getItemCount() {
        return savedQuotes.size();
    }
    private int getRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(156) + 50, random.nextInt(156) + 50, random.nextInt(156) + 50);
    }

}
