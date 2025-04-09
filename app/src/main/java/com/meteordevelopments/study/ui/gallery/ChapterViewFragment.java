package com.meteordevelopments.study.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.noties.markwon.Markwon;
import io.noties.markwon.image.glide.GlideImagesPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ChapterViewFragment extends Fragment {

    private static final String ARG_TITLE = "chapter_title";

    public static ChapterViewFragment newInstance(String title) {
        ChapterViewFragment fragment = new ChapterViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ScrollView scrollView = new ScrollView(getContext());
        TextView textView = new TextView(getContext());
        textView.setPadding(32, 32, 32, 32);
        scrollView.addView(textView);

        String title = getArguments() != null ? getArguments().getString(ARG_TITLE) : "chapter1";
        File file = new File(requireContext().getFilesDir(), title + ".md");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.FileInputStream(file)));
            StringBuilder markdown = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                markdown.append(line).append("\n");
            }
            reader.close();

            Markwon markwon = Markwon.builder(requireContext())
                    .usePlugin(GlideImagesPlugin.create(requireContext()))
                    .build();

            markwon.setMarkdown(textView, markdown.toString());

        } catch (Exception e) {
            textView.setText("Failed to load chapter.");
        }

        return scrollView;
    }
}

