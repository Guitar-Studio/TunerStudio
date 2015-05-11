package com.studio.aal.tunerstudio;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class PantallaCarga extends ActionBarActivity {

    RotateAnimation animRotate;
    float xPivot = 0.5f;
    float yPivot = 0.4f;

    float startRotate = 0.0f;
    float stopRotate = 45.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);

        rotar();

        Thread timer = new Thread(){
            //El nuevo Thread exige el metodo run
            public void run(){
                try{
                    sleep(5000);
                }catch(InterruptedException e){
                    //Si no puedo ejecutar el sleep muestro el error
                    e.printStackTrace();
                }finally{
                    saltar();
                }
            }
        };
        //ejecuto el thread
        timer.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pantalla_carga, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saltar(){
        Intent actividaPrincipal = new Intent(this, Menu_Publi.class);
        startActivity(actividaPrincipal);
    }

    public void rotar(){
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new LinearInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);

        animSet.setAnimationListener(animListener);

        ImageView myImageView = (ImageView)findViewById(R.id.carga);

            animRotate = new RotateAnimation(startRotate, stopRotate,
                    RotateAnimation.RELATIVE_TO_SELF, xPivot,
                    RotateAnimation.RELATIVE_TO_SELF, yPivot);

            animRotate.setDuration(100);
            animRotate.setStartOffset(500);

        startRotate+=45;
        stopRotate+=45;

        animSet.addAnimation(animRotate);
        myImageView.startAnimation(animSet);
    }

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
