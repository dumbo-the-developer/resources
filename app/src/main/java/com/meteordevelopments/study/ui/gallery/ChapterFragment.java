package com.meteordevelopments.study.ui.gallery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.*;

import org.json.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChapterFragment extends Fragment {

    private static final String ARG_SUBJECT_PREFIX = "chem_";
    private static final String API_URL = "https://api.github.com/repos/dumbo-the-developer/resources/contents/chapters";
    private static final String RAW_BASE_URL = "https://raw.githubusercontent.com/dumbo-the-developer/resources/main/chapters/";

    public static ChapterFragment newInstance(String subjectPrefix) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SUBJECT_PREFIX, subjectPrefix);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = requireContext();
        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(layout);

        String prefix = getArguments() != null ? getArguments().getString(ARG_SUBJECT_PREFIX, "") : "";

        if (isConnected(context)) {
            fetchChaptersOnline(layout, prefix);
        } else {
            loadChaptersOffline(layout, prefix);
        }

        return scrollView;
    }

    private void fetchChaptersOnline(LinearLayout layout, String prefix) {
        new Thread(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();

                JSONArray files = new JSONArray(json.toString());
                List<String> chapterNames = new ArrayList<>();
                for (int i = 0; i < files.length(); i++) {
                    JSONObject file = files.getJSONObject(i);
                    String name = file.getString("name");
                    if (name.endsWith(".md") && name.startsWith(prefix)) {
                        downloadChapter(name);
                        chapterNames.add(name.replace(".md", "").replace(prefix, ""));
                    }
                }

                showChapterList(layout, chapterNames, prefix);
            } catch (Exception e) {
                showError(layout, "Error fetching chapters: " + e.getMessage());
            }
        }).start();
    }

    private void loadChaptersOffline(LinearLayout layout, String prefix) {
        File dir = requireContext().getFilesDir();
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".md") && name.startsWith(prefix));
        if (files != null && files.length > 0) {
            List<String> chapterNames = new ArrayList<>();
            for (File file : files) {
                chapterNames.add(file.getName().replace(".md", "").replace(prefix, ""));
            }
            showChapterList(layout, chapterNames, prefix);
        } else {
            showError(layout, "No chapters available offline.");
        }
    }

    private void downloadChapter(String fileName) {
        try {
            URL url = new URL(RAW_BASE_URL + fileName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            File file = new File(requireContext().getFilesDir(), fileName);
            FileOutputStream output = new FileOutputStream(file);
            int c;
            while ((c = reader.read()) != -1) {
                output.write(c);
            }
            reader.close();
            output.close();
        } catch (Exception ignored) {}
    }

    private void showChapterList(LinearLayout layout, List<String> chapters, String prefix) {
        requireActivity().runOnUiThread(() -> {
            layout.removeAllViews();
            for (String title : chapters) {
                TextView tv = new TextView(requireContext());
                tv.setText(title);
                tv.setTextSize(20);
                tv.setPadding(32, 32, 32, 32);
                tv.setOnClickListener(v -> openChapter(prefix + title));
                layout.addView(tv);
            }
        });
    }

    private void openChapter(String fullName) {
        Fragment fragment = ChapterViewFragment.newInstance(fullName);
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(this.getId(), fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void showError(LinearLayout layout, String message) {
        requireActivity().runOnUiThread(() -> {
            TextView error = new TextView(requireContext());
            error.setText(message);
            error.setPadding(32, 32, 32, 32);
            layout.addView(error);
        });
    }

    private boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info != null && info.isConnected();
        }
        return false;
    }
}
