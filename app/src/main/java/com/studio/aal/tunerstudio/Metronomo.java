package com.studio.aal.tunerstudio;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Metronomo extends Fragment {

    boolean start = false;
    boolean right = true;

    RotateAnimation animRotate;

    float xPivot = 0.494f;
    float yPivot = 0.75f;

    int time = 1500;

    Button startButton;

    public Metronomo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_metronomo, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        startButton = (Button) getView().findViewById(R.id.start);
        startButton.setOnClickListener(btnClick);
    }

    public void rotar(){
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new LinearInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);

        animSet.setAnimationListener(animListener);

        ImageView myImageView = (ImageView)getView().findViewById(R.id.aguja);

        if(start){
            animRotate = new RotateAnimation(0.0f, -45.0f,
                    RotateAnimation.RELATIVE_TO_SELF, xPivot,
                    RotateAnimation.RELATIVE_TO_SELF, yPivot);

            animRotate.setFillAfter(true);
            animRotate.setDuration(time);

            start = !start;
        }
        else{
            if(right){
                animRotate = new RotateAnimation(-45.0f, 45.0f,
                        RotateAnimation.RELATIVE_TO_SELF, xPivot,
                        RotateAnimation.RELATIVE_TO_SELF, yPivot);

            }
            else{
                animRotate = new RotateAnimation(45.0f, -45.0f,
                        RotateAnimation.RELATIVE_TO_SELF, xPivot,
                        RotateAnimation.RELATIVE_TO_SELF, yPivot);

            }

            animRotate.setFillAfter(true);
            animRotate.setDuration(time);

            right = !right;
        }
        animRotate.setFillAfter(true);
        animSet.addAnimation(animRotate);

        myImageView.startAnimation(animSet);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            rotar();
        }
    };

    private Animation.AnimationListener animListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            rotar();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
}
