package com.meteordevelopments.study.ui.gallery;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ChaptersPagerAdapter extends FragmentStateAdapter {

    private final List<String> chapterTitles;

    public ChaptersPagerAdapter(@NonNull Fragment fragment, List<String> titles) {
        super(fragment);
        this.chapterTitles = titles;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ChapterFragment.newInstance(chapterTitles.get(position));
    }

    @Override
    public int getItemCount() {
        return chapterTitles.size();
    }
}

