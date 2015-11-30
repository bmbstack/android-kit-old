package com.github.bmbstack.androidkit.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.bmbstack.androidkit.app.R;
import com.github.bmbstack.androidkit.app.activity.BackPageActivity;

/**
 * Created by GYJC on 2015/4/28.
 */
public class MyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_my, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout ll_my_profile = (LinearLayout) view.findViewById(R.id.ll_my_profile);
        LinearLayout ll_my_homepage = (LinearLayout) view.findViewById(R.id.ll_my_homepage);
        LinearLayout ll_my_clinic = (LinearLayout) view.findViewById(R.id.ll_my_clinic);
        LinearLayout ll_my_expert = (LinearLayout) view.findViewById(R.id.ll_my_expert);
        LinearLayout ll_my_collection = (LinearLayout) view.findViewById(R.id.ll_my_collection);
        LinearLayout ll_my_learn_history = (LinearLayout) view.findViewById(R.id.ll_my_learn_history);
        LinearLayout ll_settings = (LinearLayout) view.findViewById(R.id.ll_settings);

        ll_settings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BackPageActivity.class);
                intent.putExtra(BackPageActivity.BUNDLE_KEY_BACK_PAGE, BackPageActivity.BackPage.PHOTOPICKSAMPLE.getValue());
                getActivity().startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
