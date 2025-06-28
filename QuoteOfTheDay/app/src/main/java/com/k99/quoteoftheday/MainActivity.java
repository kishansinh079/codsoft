package com.k99.quoteoftheday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final List<QuoteModel> quoteList = new ArrayList<>();
    private final Random random = new Random();
    private TextView tvQuote, tvAuthor;
    private RelativeLayout quoteLayout;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        quoteLayout = findViewById(R.id.quoteCard);
        ImageView btnShare = findViewById(R.id.btnShare);
        ImageView btnSave = findViewById(R.id.btnSave);
        ImageView menuIcon = findViewById(R.id.menuIcon);
        tvQuote = findViewById(R.id.tvQuote);
        tvAuthor = findViewById(R.id.tvAuthor);

        databaseReference = FirebaseDatabase.getInstance().getReference("quotes");

//        quoteLayout.setOnClickListener(v -> showRandomQuote()); // Tap to refresh
        quoteLayout.setOnClickListener(v -> createRandomGradient());
        btnShare.setOnClickListener(v -> shareQuote());

        menuIcon.setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
            Set<String> savedQuotesSet = prefs.getStringSet("saved_quotes", new HashSet<>());
            if (savedQuotesSet.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, AllQuotesActivity.class);
                startActivity(intent);
            } else {
                View popupView = getLayoutInflater().inflate(R.layout.drawer_main, null);

                PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        true
                );

                // Show the popup near the menu icon
                int[] location = new int[2];
                menuIcon.getLocationOnScreen(location);
                popupWindow.showAtLocation(menuIcon, Gravity.NO_GRAVITY,
                        location[0], location[1] - popupView.getHeight());

                // Option click listeners
                popupView.findViewById(R.id.saved_).setOnClickListener(opt1 -> {
                    Intent intent = new Intent(MainActivity.this, SavedQuotesActivity.class);
                    startActivity(intent);
                    popupWindow.dismiss();
                });

                popupView.findViewById(R.id.all_).setOnClickListener(opt2 -> {
                    Intent intent = new Intent(MainActivity.this, AllQuotesActivity.class);
                    startActivity(intent);
                    popupWindow.dismiss();
                });
            }
        });
        btnSave.setOnClickListener(v -> {
            String quote = tvQuote.getText().toString();
            String author = tvAuthor.getText().toString();
            String fullQuote = quote + " | " + author;

            SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
            Set<String> savedQuotes = prefs.getStringSet("saved_quotes", new HashSet<>());

            // Make a copy to avoid modifying original set
            Set<String> updatedQuotes = new HashSet<>(savedQuotes);
            updatedQuotes.add(fullQuote); // Add new quote

            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet("saved_quotes", updatedQuotes);
            editor.apply();

            Toast.makeText(MainActivity.this, "Quote saved!", Toast.LENGTH_SHORT).show();
        });

//        btnSave.setOnClickListener(v -> {
//            String quote = tvQuote.getText().toString();
//            String author = tvAuthor.getText().toString();
//
//            SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putString("saved_quote", quote);
//            editor.putString("saved_author", author);
//            editor.apply();
//
//            Toast.makeText(MainActivity.this, "Quote saved!", Toast.LENGTH_SHORT).show();
//        });

        loadQuotes();
    }

    private void loadQuotes() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quoteList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    QuoteModel quote = child.getValue(QuoteModel.class);
                    if (quote != null) {
                        quoteList.add(quote);
                    }
                }
                showRandomQuote(); // Initial display
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvQuote.setText("Failed to load quotes.");
            }
        });
    }

    private void showRandomQuote() {
        if (!quoteList.isEmpty()) {
            int index = random.nextInt(quoteList.size());
            QuoteModel randomQuote = quoteList.get(index);
            tvQuote.setText('"' + randomQuote.getText() + '"');
            tvAuthor.setText("- " + randomQuote.getAuthor());

            createRandomGradient(); // Also change background
        }
    }

    private void shareQuote() {
        String quoteText = tvQuote.getText().toString();
        String authorText = tvAuthor.getText().toString();
        String shareContent = quoteText + "\n" + authorText + "\n\nShared via DQUOTES App";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);

        startActivity(Intent.createChooser(shareIntent, "Share Quote via"));
    }

    private int getRandomColor() {
        // Avoid too bright/dark
        return Color.rgb(random.nextInt(156) + 50, random.nextInt(156) + 50, random.nextInt(156) + 50);
    }

    private void createRandomGradient() {
        int colorStart = getRandomColor();
        int colorEnd = getRandomColor();

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.BL_TR,
                new int[]{colorStart, colorEnd}
        );
        gradient.setCornerRadius(0f);
        quoteLayout.setBackground(gradient);

        int avgColor = ColorUtils.blendARGB(colorStart, colorEnd, 0.5f);
        double luminance = ColorUtils.calculateLuminance(avgColor);
        int textColor = luminance < 0.5 ? Color.WHITE : Color.BLACK;

        tvQuote.setTextColor(textColor);
        tvAuthor.setTextColor(textColor);
    }
}
