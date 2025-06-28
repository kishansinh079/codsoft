package com.k99.quoteoftheday;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class AllQuotesAdapter extends RecyclerView.Adapter<AllQuotesAdapter.ViewHolder> {

    private final List<QuoteModel> quoteList;
    private final Random random = new Random();
    private final Context context;

    public AllQuotesAdapter(Context context, List<QuoteModel> quoteList) {
        this.context = context;
        this.quoteList = quoteList;
    }

//    public AllQuotesAdapter(List<QuoteModel> quoteList) {
//        this.quoteList = quoteList;
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView quoteText;
        ImageView bookmarkIcon;
        RelativeLayout cardLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            quoteText = itemView.findViewById(R.id.quoteText);
            bookmarkIcon = itemView.findViewById(R.id.bookmarkIcon);
            cardLayout = (RelativeLayout) itemView.findViewById(R.id.quoteText).getParent();
        }
    }

    @NonNull
    @Override
    public AllQuotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllQuotesAdapter.ViewHolder holder, int position) {
        QuoteModel quote = quoteList.get(position);
        String fullText = quote.getText() + "\n\n— " + quote.getAuthor();
        holder.quoteText.setText(fullText);

        // Gradient background
        int startColor = getRandomColor();
        int endColor = getRandomColor();
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{startColor, endColor}
        );
        gradient.setCornerRadius(24f);
        holder.cardLayout.setBackground(gradient);

        // Font color for readability
        int avgColor = ColorUtils.blendARGB(startColor, endColor, 0.5f);
        int textColor = ColorUtils.calculateLuminance(avgColor) < 0.5 ? Color.WHITE : Color.BLACK;
        holder.quoteText.setTextColor(textColor);
        holder.bookmarkIcon.setOnClickListener(v -> {
            String fullQuote = quote.getText() + " | " + quote.getAuthor();

            SharedPreferences prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
            Set<String> savedQuotes = prefs.getStringSet("saved_quotes", new HashSet<>());

            Set<String> updatedQuotes = new HashSet<>(savedQuotes);
            boolean added = updatedQuotes.add(fullQuote); // returns false if already present

            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet("saved_quotes", updatedQuotes);
            editor.apply();

            Toast.makeText(context,
                    added ? "Quote saved!" : "Already saved.",
                    Toast.LENGTH_SHORT).show();
        });

    }

    private int getRandomColor() {
        return Color.rgb(random.nextInt(156) + 50,
                random.nextInt(156) + 50,
                random.nextInt(156) + 50);
    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }
}

