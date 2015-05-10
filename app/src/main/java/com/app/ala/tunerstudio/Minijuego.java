package com.app.ala.tunerstudio;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
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
    List<String> notesCopy;

    List<String> notesSelected = new ArrayList<String>();

    Integer[] timeStamp = {0, 1840, 3600, 5240, 7000, 8800};

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

        notesCopy = notes;

        mediaPlayer = MediaPlayer.create(getView().getContext(), R.raw.c_sonido);
        mediaPlayer.setOnSeekCompleteListener(timeStampDetector);
        mediaPlayer.setOnPreparedListener(prepListener);

        crono_num = (Chronometer) getView().findViewById(R.id.cuadrocrono);
        crono_num.start();
        crono_num.setOnChronometerTickListener(cronoList);



    }

    public void createButtons(){
        buttons[0] = (Button) getView().findViewById(R.id.b1);
        buttons[1] = (Button) getView().findViewById(R.id.b2);
        buttons[2] = (Button) getView().findViewById(R.id.b3);

        buttons[0].setOnClickListener(clickListener);
        buttons[1].setOnClickListener(clickListener);
        buttons[2].setOnClickListener(clickListener);

        getCorrectButtonId = false;

        for(int i=0;i<3;i++){
            selectNotes(i);
        }

        notes = notesCopy;

        for(int i=0;i<3;i++){
            createButton(i);
        }
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
        Log.i("SIZE", ""+notesSelected.size());
        Log.i("RANDOM", ""+randomNum);

        if (randomNum == 0 && !getCorrectButtonId){
            correctButtonId = buttons[pos].getId();
            getCorrectButtonId = true;
        }

        buttons[pos].setText(notesSelected.get(randomNum));

        notesSelected.remove(randomNum);
    }

    private MediaPlayer.OnSeekCompleteListener timeStampDetector = new MediaPlayer.OnSeekCompleteListener (){
        public void onSeekComplete (MediaPlayer mp) {
            mediaPlayer.start();
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    mediaPlayer.stop();
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
                Log.i("MENSAJE", "Has acertado!!!");
                createButtons();
            }
        }
    };

    private Chronometer.OnChronometerTickListener cronoList = new Chronometer.OnChronometerTickListener(){
        @Override
        public void onChronometerTick(Chronometer chronometer){
            TextView numero_cronometro = (TextView) getView().findViewById(R.id.nums_cron);
            numero_cronometro.setText("" + chronometer.getBase());
        }
    };
}
