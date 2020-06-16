package com.example.chapter3.homework;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.airbnb.lottie.LottieAnimationView;

public class PlaceholderFragment extends Fragment {

    private LottieAnimationView lottie_AnimationView;
    private ListView list_View;
    private String[] Friends = {"Item 1","Item 2","Item 3","Item 4","Item 5","Item 6","Item 7","Item 8","Item 9","Item 10","Item 11"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件
        return inflater.inflate(R.layout.fragment_placeholder, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lottie_AnimationView = getView().findViewById(R.id.animation_view);
        list_View = getView().findViewById(R.id.list_view);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(list_View.getContext(),R.layout.list_nameitem, Friends);
        list_View.setAdapter(adapter);
        list_View.setAlpha(0);
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 这里会在 5s 后执行
                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(lottie_AnimationView,
                        "alpha",1,0);
                animator1.setRepeatCount(0);

                ObjectAnimator animator2 = ObjectAnimator.ofFloat(list_View,
                        "alpha",0,1);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(animator1,animator2);
                animatorSet.start();
            }
        }, 5000);
    }
}
