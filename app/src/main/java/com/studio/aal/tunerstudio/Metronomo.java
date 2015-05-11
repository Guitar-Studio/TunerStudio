package com.studio.aal.tunerstudio;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
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

    boolean go = true;

    RotateAnimation animRotate;

    float xPivot = 0.494f;
    float yPivot = 0.75f;

    long timeSlow = 90;            //90bpm
    long timeModerate = 110;       //110bpm
    long timeFast = 136;           //136bpm

    long time = 0;

    Button andanteButton;
    Button moderatoButton;
    Button allegroButton;

    MediaPlayer mediaPlayer;

    int anim = 1;

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

        andanteButton = (Button) getView().findViewById(R.id.andante);
        moderatoButton = (Button) getView().findViewById(R.id.moderatto);
        allegroButton = (Button) getView().findViewById(R.id.allegro);

        andanteButton.setOnClickListener(btnClick);
        moderatoButton.setOnClickListener(btnClick);
        allegroButton.setOnClickListener(btnClick);

        mediaPlayer = MediaPlayer.create(getView().getContext(), R.raw.t_sonido);
        mediaPlayer.setOnPreparedListener(prepListener);
    }

    public void rotar() {
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new LinearInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);

        animSet.setAnimationListener(animListener);

        ImageView myImageView = (ImageView) getView().findViewById(R.id.aguja);

        switch (anim) {
            case 1:
                animRotate = new RotateAnimation(0.0f, -45.0f,
                        RotateAnimation.RELATIVE_TO_SELF, xPivot,
                        RotateAnimation.RELATIVE_TO_SELF, yPivot);
                Log.i("Entro", "Estoy aqui");
                break;
            case 2:
                animRotate = new RotateAnimation(-45.0f, 0.0f,
                        RotateAnimation.RELATIVE_TO_SELF, xPivot,
                        RotateAnimation.RELATIVE_TO_SELF, yPivot);
                break;
            case 3:
                animRotate = new RotateAnimation(0.0f, 45.0f,
                        RotateAnimation.RELATIVE_TO_SELF, xPivot,
                        RotateAnimation.RELATIVE_TO_SELF, yPivot);
                break;
            case 4:
                animRotate = new RotateAnimation(45.0f, 0.0f,
                        RotateAnimation.RELATIVE_TO_SELF, xPivot,
                        RotateAnimation.RELATIVE_TO_SELF, yPivot);
                break;
        }

        animRotate.setDuration(time);

        animSet.addAnimation(animRotate);
        myImageView.startAnimation(animSet);
    }


    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.andante:
                    time = 1*60000/timeSlow;
                    break;
                case R.id.moderatto:
                    time = 1*60000/timeModerate;
                    break;
                case R.id.allegro:
                    time = 1*60000/timeFast;
                    break;
            }
            rotar();
        }
    };

    private Animation.AnimationListener animListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (anim == 2 || anim == 4){
                mediaPlayer.start();
            }
            if (anim == 4){
                anim = 1;
            }
            else{
                anim++;
            }

            rotar();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private MediaPlayer.OnPreparedListener prepListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {

        }
    };
}
