package com.hit.whowantstobeaprogrammer;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;

public class AnswerButton extends androidx.appcompat.widget.AppCompatButton {
    private static SoundPool soundPool;
    private static int Wrong;
    private static int Correct;


    public AnswerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        soundPool=new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        Wrong = soundPool.load(context,R.raw.wrong_5,1);
        Correct = soundPool.load(context,R.raw.correct,1);
        ButtonReset();

    }
    public void ButtonSelected(){
        this.setClickable(false);
    }

    public void ButtonReset(){
        changeColor(R.drawable.bluebuttonshape);
        this.setClickable(true);
    }
    public void WrongAnswer() {
        soundPool.play(Wrong,1.0f,1.0f,1,0,1.0f);
        this.setClickable(false);
        changeColor(R.drawable.redbuttonshape);
        blink();

    }
    public void CorrectAnswer(boolean bool){
        if(bool) {
            soundPool.play(Correct, 1.0f, 1.0f, 1, 0, 1.0f);
        }
        this.setClickable(false);
        changeColor(R.drawable.greenbuttonshape);
        blink();
    }
    private void changeColor(int color){
        Drawable myDrawable;
        Resources res = getResources();
        try {
            myDrawable = Drawable.createFromXml(res, res.getXml(color));
            this.setBackground(myDrawable);
        } catch (Exception ex) {
            Log.e("Error", "Exception loading drawable");
        }
    }
    private void blink(){
        ObjectAnimator oa = ObjectAnimator.ofFloat(this,"alpha",0.2f).setDuration(100);
        oa.setRepeatCount(5);
        oa.setRepeatMode(ObjectAnimator.REVERSE);
        oa.start();
    }
}

