package com.studio.aal.tunerstudio;


import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Debug;
import android.os.Environment;
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
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class Afinador extends Fragment {

    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    int[] bufferData;
    int mPeakPos;
    double[] absNormalizedSignal;
    final int mNumberOfFFTPoints = 4096;

    public float frequency;

    RotateAnimation animRotate;
    float xPivot = 0.5f;
    float yPivot = 0.5f;

    float startRotate = 0.0f;
    float stopRotate = 0.0f;

    boolean isAnimating = false;

    public Afinador() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        bufferSize = AudioRecord.getMinBufferSize
                (RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING)*4;

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_afinador, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        startRecording();
    }

    //_______USANDO EL MICRO_______________________________________________
    public void startRecording(){               //recogerSonido
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);

        recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {

            public void run() {
                writeAudioDataToFile();
            }
        });

        recordingThread.start();
    }

    public void updateUI(){
        if(absNormalizedSignal != null){
            Log.i("FFT", ""+RECORDER_SAMPLERATE);
            frequency = mPeakPos*(RECORDER_SAMPLERATE/mNumberOfFFTPoints);

            TextView texto = (TextView) getView().findViewById(R.id.frecuencia);
            Log.i("Frecuencia", ""+frequency);

            getView().findViewById(R.id.parriba).bringToFront();

            if (frequency > 121 && frequency < 131){//E1 126
                texto.setBackgroundColor(Color.GREEN);
                texto.setText("E " + frequency);
                stopRotate = 15f*4;
            }
            else if (frequency > 91 && frequency < 101){//B 96
                texto.setBackgroundColor(Color.GREEN);
                texto.setText("B " + frequency);
                stopRotate = 15f*1;
            }
            else if (frequency > 73 && frequency < 83) {//G 78
                texto.setBackgroundColor(Color.GREEN);
                texto.setText("G " + frequency);
                stopRotate = 15f*6;
            }
            else if (frequency > 49 && frequency < 59) {//D 54
                texto.setBackgroundColor(Color.GREEN);
                texto.setText("D " + frequency);
                stopRotate = 15f*3;
            }
            else if (frequency > 205 && frequency < 215) {//A 210
                texto.setBackgroundColor(Color.GREEN);
                texto.setText("A " + frequency);
                stopRotate = 0.0f;
            }
            else if (frequency > 61 && frequency < 71) {//E6 66
                texto.setBackgroundColor(Color.GREEN);
                texto.setText("E " + frequency);
                stopRotate = 15f*4;
            }
            else{
                texto.setBackgroundColor(Color.BLACK);
            }

            rotar();
        }
    }

    private void writeAudioDataToFile(){        //guardarSonido
        byte data[] = new byte[bufferSize];     //array tipo byte -> está creado para guardar el audio, el sonido
        String filename = getTempFilename();    //donde se va a guardar el sonido (ruta+nombre del archivo)
        FileOutputStream os = null;             //lo lleva a la ruta

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;

        if(null != os){
            while(isRecording){
                read = recorder.read(data, 0, bufferSize);
                if(read > 0){
                    absNormalizedSignal = calculateFFT(data); // --> HERE ^__^
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            updateUI();
                        }
                    });
                }



                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRecording(){ //dejarRecogerSonido
        if(null != recorder){
            isRecording = false;

            recorder.stop();
            recorder.release();

            recorder = null;
        }

        copyWaveFile(getTempFilename(), getFilename());
    }
    //____DEJANDO DE USAR EL MICRO________________________________________

    private String getTempFilename(){               //cogerTemporal
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

        if(tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private String getFilename(){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private void copyWaveFile(String inFilename,String outFilename){ //copiarArchivoDeOnda
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            Log.i("AVISO", "File size: " + totalDataLen);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double[] calculateFFT(byte[] signal)
    {
        double mMaxFFTSample;

        double temp;
        Complex[] y;
        Complex[] complexSignal = new Complex[mNumberOfFFTPoints];
        double[] absSignal = new double[mNumberOfFFTPoints/2];

        for(int i = 0; i < mNumberOfFFTPoints-1; i++){
            temp = (double)((signal[2*i] & 0xFF) | (signal[2*i+1] << 8)) / 32768.0F;
            complexSignal[i] = new Complex(temp,0.0);
        }

        y = FFT.fft(complexSignal); // --> Here I use FFT class

        mMaxFFTSample = 0.0;
        mPeakPos = 0;
        for(int i = 0; i < (mNumberOfFFTPoints/2); i++)
        {
            absSignal[i] = Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2));
            if(absSignal[i] > mMaxFFTSample)
            {
                mMaxFFTSample = absSignal[i];
                mPeakPos = i;
            }
        }
        return absSignal;
    }

    public void rotar(){
        if (!isAnimating){
            AnimationSet animSet = new AnimationSet(true);
            animSet.setInterpolator(new LinearInterpolator());
            animSet.setFillAfter(true);
            animSet.setFillEnabled(true);

            ImageView myImageView = (ImageView) getView().findViewById(R.id.ruedotaa);

            animRotate = new RotateAnimation(-startRotate, -stopRotate,
                    RotateAnimation.RELATIVE_TO_SELF, xPivot,
                    RotateAnimation.RELATIVE_TO_SELF, yPivot);

            animRotate.setDuration(2000);
            animSet.setAnimationListener(animListener);

            animSet.addAnimation(animRotate);
            myImageView.startAnimation(animSet);

            Log.i("Start Rotate", ""+startRotate);
            Log.i("Stop Rotate", ""+stopRotate);

            startRotate = stopRotate;
        }
    }

    private Animation.AnimationListener animListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isAnimating = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isAnimating = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
}
