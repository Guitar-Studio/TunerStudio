package com.studio.aal.tunerstudio;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Layout;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class Minijuego extends Fragment {

    private Chronometer crono_num;

    List<String> notes = new LinkedList(Arrays.asList("E6", "A5", "D4", "G3", "B2", "E1"));
    List<String> notesCopy = new LinkedList(Arrays.asList("E6", "A5", "D4", "G3", "B2", "E1"));

    List<String> notesSelected = new ArrayList<String>();

    Integer[] timeStamp = {0, 1840, 3600, 5240, 7000, 8800};

    int contError = 0;
    int contNotes = 0;

    Button[] buttons = new Button[3];
    Integer correctButtonId;
    boolean getCorrectButtonId;

    MediaPlayer mediaPlayer;
    Integer selectedTimeStamp;

    Handler mHandler;

    public Minijuego() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_minijuego, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mediaPlayer = MediaPlayer.create(getView().getContext(), R.raw.c_sonido);
        mediaPlayer.setOnSeekCompleteListener(timeStampDetector);
        mediaPlayer.setOnPreparedListener(prepListener);

        Animation alpha_0 = AnimationUtils.loadAnimation(getView().getContext(),
                R.anim.alpha_0);

        RelativeLayout layout = (RelativeLayout) getView().findViewById(R.id.layouterror);
        buttons[0] = (Button) getView().findViewById(R.id.b1);
        buttons[1] = (Button) getView().findViewById(R.id.b2);
        buttons[2] = (Button) getView().findViewById(R.id.b3);

        layout.startAnimation(alpha_0);
        buttons[0].startAnimation(alpha_0);
        buttons[1].startAnimation(alpha_0);
        buttons[2].startAnimation(alpha_0);

        buttons[0].setOnClickListener(clickListener);
        buttons[1].setOnClickListener(clickListener);
        buttons[2].setOnClickListener(clickListener);

        crono_num = (Chronometer) getView().findViewById(R.id.cuadrocrono);
        crono_num.start();
    }

    public void createButtons(){
        getCorrectButtonId = false;

        for(int i=0;i<3;i++){
            selectNotes(i);
        }

        notes = new ArrayList<>(notesCopy);

        for(int i=0;i<3;i++){
            createButton(i);
        }

        fadeIn();
        contNotes++;
    }

    public void selectNotes(int pos){
        Random rand = new Random();
        int randomNum = rand.nextInt(notes.size());

        notesSelected.add(notes.get(randomNum));

        if (pos == 0){
            if(randomNum+1 != notes.size()){
                selectedTimeStamp = timeStamp[randomNum+1] - timeStamp[randomNum];
            }
            else{
                selectedTimeStamp = mediaPlayer.getDuration() - timeStamp[randomNum];
            }
            mediaPlayer.seekTo(timeStamp[randomNum]);
        }
        notes.remove(randomNum);
    }

    public void createButton(int pos) {
        Random rand = new Random();
        int randomNum = rand.nextInt(notesSelected.size());
        Log.i("SIZE", "" + notesSelected.size());
        Log.i("RANDOM", ""+randomNum);

        if (randomNum == 0 && !getCorrectButtonId){
            correctButtonId = buttons[pos].getId();
            getCorrectButtonId = true;
        }

        buttons[pos].setText(notesSelected.get(randomNum));

        notesSelected.remove(randomNum);
    }

    private void fadeIn(){
        Animation animFadein = AnimationUtils.loadAnimation(getView().getContext(),
                R.anim.fade_in_button);
        buttons[0].startAnimation(animFadein);
        buttons[1].startAnimation(animFadein);
        buttons[2].startAnimation(animFadein);
    }

    private void fadeOut(){
        Animation animFadeout = AnimationUtils.loadAnimation(getView().getContext(),
                R.anim.fade_out_button);
        buttons[0].startAnimation(animFadeout);
        buttons[1].startAnimation(animFadeout);
        buttons[2].startAnimation(animFadeout);
    }

    private MediaPlayer.OnSeekCompleteListener timeStampDetector = new MediaPlayer.OnSeekCompleteListener (){
        public void onSeekComplete (MediaPlayer mp) {
            mediaPlayer.start();
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    mediaPlayer.pause();
                }
            }, selectedTimeStamp);
        }
    };

    private MediaPlayer.OnPreparedListener prepListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            createButtons();
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == correctButtonId){
                if(contNotes==2){
                    showDialog();
                    return;
                }
                Log.i("MENSAJE", "Has acertado!!!");
                fadeOut();
                createButtons();
            }
            else {
                RelativeLayout layout = (RelativeLayout) getView().findViewById(R.id.layouterror);

                Animation animFadein = AnimationUtils.loadAnimation(getView().getContext(),
                        R.anim.fade_in_error);
                Animation animFadeout = AnimationUtils.loadAnimation(getView().getContext(),
                        R.anim.fade_out_error);

                layout.startAnimation(animFadein);
                layout.startAnimation(animFadeout);

                Log.i("FADE", "Colorito rojito");

                MediaPlayer errorPlayer = MediaPlayer.create(getView().getContext(), R.raw.e_sonido);
                errorPlayer.start();

                long timeError = crono_num.getBase();
                long extraTime = 5000;
                crono_num.setBase(timeError-extraTime);

                contError++;
            }
        }
    };

    private DialogInterface.OnClickListener clickDialogListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            crono_num.start();

        }
    };

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(  getView().getContext(), R.style.AppTheme));
        builder.setMessage("Tiempo: "+ crono_num.getText() + "\nError/es:" + contError );
        builder.setPositiveButton(R.string.Accept, clickDialogListener);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        contError=0;
        contNotes=0;
        String a = (String) crono_num.getText();
        String[] tiempo = a.split(":");
        long milisegundos = (Long.parseLong(tiempo[0])*60000+Long.parseLong(tiempo[1])*1000);
        crono_num.setBase(crono_num.getBase() + milisegundos);
        crono_num.stop();

    }
}




