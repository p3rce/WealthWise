package com.example.wealthwise.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.wealthwise.fragments.AdviceFragment;

import java.util.List;

public class AdvicePageAdapter extends FragmentStateAdapter {

    private final List<String> adviceList;

    public AdvicePageAdapter(@NonNull FragmentActivity fragmentActivity, List<String> adviceList) {
        super(fragmentActivity);
        this.adviceList = adviceList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return AdviceFragment.newInstance(adviceList.get(position));
    }

    @Override
    public int getItemCount() {
        return adviceList.size();
    }
}
