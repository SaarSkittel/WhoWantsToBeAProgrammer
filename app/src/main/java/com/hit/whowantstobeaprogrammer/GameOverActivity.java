package com.hit.whowantstobeaprogrammer;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GameOverActivity extends AppCompatActivity {

    Button replay;
    Button home;
    FloatingActionButton music;
    String name;
    Integer score;
    Boolean musicOnOrOff;
    SharedPreferences sp;
    MediaPlayer backgroundMusic;
    Map<String,Integer> scoreMap;
    TextView scoreTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scoreMap=new HashMap<String, Integer>();
        setContentView(R.layout.activity_game_over);
        scoreTV=findViewById(R.id.score);

        ValueAnimator valueAnimator=ValueAnimator.ofObject(new ArgbEvaluator(), Color.RED,Color.GREEN,Color.BLUE);
        valueAnimator.setDuration(10000);
        valueAnimator.setRepeatCount(Animation.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color=(int)valueAnimator.getAnimatedValue();
                scoreTV.setTextColor(color);
            }
        });

        name =getIntent().getStringExtra("name");
        sp=getSharedPreferences("user_details",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user_name", name);
        editor.commit();
        score =sp.getInt("score",0);

        scoreTV.setText(getResources().getString(R.string.score)+score.toString());
        try {
            loadScoreboard();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(!name.equals(getResources().getString(R.string.guest))){
                saveScoreboard();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        replayButton();

        homeButton();

        musicFloatingButton();
        ObjectAnimator anim1 =ObjectAnimator.ofFloat(replay,"alpha",0.5f).setDuration(1000);
        anim1.setRepeatCount(Animation.INFINITE);
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator anim2=ObjectAnimator.ofFloat(home,"alpha",0.5f).setDuration(1000);
        anim2.setRepeatCount(Animation.INFINITE);
        anim2.setRepeatMode(ValueAnimator.REVERSE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim1,anim2,valueAnimator);
        animatorSet.start();
    }
    private void loadScoreboard() throws IOException {

        FileInputStream fileInputStream= openFileInput("scoreboard.hit");
        ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
        try {
            scoreMap = (HashMap)objectInputStream.readObject();
        } catch (ClassNotFoundException e) {

        }
        objectInputStream.close();
    }
    private void saveScoreboard() throws IOException {
        if(scoreMap.containsKey(name)&& scoreMap.get(name).intValue() < Integer.valueOf(score)) {
            scoreMap.replace(name, Integer.valueOf(score));
        }
        else if(!scoreMap.containsKey(name)){
        scoreMap.put(name,Integer.valueOf(score));
        }

        sortMap();
        FileOutputStream fileOutputStream = openFileOutput("scoreboard.hit", MODE_PRIVATE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(scoreMap);
        objectOutputStream.close();

    }
    private void sortMap(){
        scoreMap = scoreMap.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> -e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> { throw new AssertionError(); },
                        LinkedHashMap::new
                ));
    }
    private void musicFloatingButton() {
        musicOnOrOff = sp.getBoolean("status",true);

        backgroundMusic = new MediaPlayer();
        backgroundMusic = MediaPlayer.create(GameOverActivity.this,R.raw.gameover);

        music = findViewById(R.id.music_btn_gameover);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicOnOrOff=!musicOnOrOff;
                musicControl();
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundMusic.stop();
        musicOnOrOff = sp.getBoolean("status", true);
        musicControl();
    }
    @Override
    protected void onPause() {
        super.onPause();
        backgroundMusic.pause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundMusic.stop();
        finish();
    }

    private void musicControl() {
        if (!musicOnOrOff) {
            backgroundMusic.stop();
            music.setImageResource(android.R.drawable.ic_lock_silent_mode);
        } else {
            backgroundMusic = MediaPlayer.create(GameOverActivity.this, R.raw.gameover);
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
            music.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
        }
    }

    private void homeButton() {
        home=findViewById(R.id.home_btn);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("status",musicOnOrOff);
                editor.commit();
                Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void replayButton() {
        replay=findViewById(R.id.replay_btn);
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("score",0);
                editor.putBoolean("status",musicOnOrOff);
                editor.commit();
                Intent intent = new Intent(GameOverActivity.this, GamePlayActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
                finish();
            }
        });
    }

}