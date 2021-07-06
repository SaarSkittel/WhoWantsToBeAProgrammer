package com.hit.whowantstobeaprogrammer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GameOverActivity extends AppCompatActivity {

    Button replay;
    Button home;
    FloatingActionButton music;

    Boolean musicOnOrOff;
    SharedPreferences sp;
    MediaPlayer backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        sp=getSharedPreferences("user_details",MODE_PRIVATE);

        replayButton();

        homeButton();

        musicFloatingButton();

    }

    private void musicFloatingButton() {
        musicOnOrOff = sp.getBoolean("status",true);

        backgroundMusic = new MediaPlayer();
        backgroundMusic = MediaPlayer.create(GameOverActivity.this,R.raw.background);

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
            backgroundMusic = MediaPlayer.create(GameOverActivity.this, R.raw.background);
            backgroundMusic.start();
            music.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
        }
    }

    private void homeButton() {
        home=findViewById(R.id.home_btn);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                Intent intent = new Intent(GameOverActivity.this, GamePlayActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}