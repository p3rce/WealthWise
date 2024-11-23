package com.example.wealthwise.activities;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wealthwise.R;

public class AdviceFragment extends Fragment {

    private static final String ARG_ADVICE = "advice";

    public static AdviceFragment newInstance(String advice) {

        AdviceFragment fragment = new AdviceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ADVICE, advice);
        fragment.setArguments(args);

        return fragment;

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advice, container, false);
        TextView adviceText = view.findViewById(R.id.adviceText);

        if(getArguments() != null) {
            adviceText.setText(getArguments().getString(ARG_ADVICE));
        }

        return view;
    }

}
