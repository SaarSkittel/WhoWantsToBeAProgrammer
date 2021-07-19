package com.hit.whowantstobeaprogrammer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    Button playButton;
    private static MediaPlayer backgroundMusic;
    private int musicLoader;
    FloatingActionButton music;
    Button scores;
    SharedPreferences sp;
    private boolean musicOnOrOff;
    AnimatorSet animatorSet;

    @Override
    protected void onPause() {
        super.onPause();
        backgroundMusic.pause();
        animatorSet.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundMusic.stop();
        musicOnOrOff = sp.getBoolean("status", true);
        musicControl();
        animatorSet.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backgroundMusic = new MediaPlayer();
        setContentView(R.layout.activity_main);

        playButton = findViewById(R.id.play_btn);
        sp = getSharedPreferences("user_details", MODE_PRIVATE);
        musicOnOrOff = sp.getBoolean("status", true);
        scores = findViewById(R.id.scores_btn);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.activity_play_dialog, null);
                EditText userNameET = dialogView.findViewById(R.id.UserName);
                Button start = dialogView.findViewById(R.id.start_btn);
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = userNameET.getText().toString();
                        name = name.isEmpty()? getResources().getString(R.string.guest):name;
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("score", 0);
                        editor.putBoolean("status", musicOnOrOff);
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this, GamePlayActivity.class);
                        intent.putExtra("name",name);
                        startActivity(intent);
                    }
                });
                builder.setView(dialogView).show();
            }
        });

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(playButton,"alpha",0.5f).setDuration(1000);
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.setRepeatCount(Animation.INFINITE);

        music = findViewById(R.id.music_btn);
        musicControl();
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicOnOrOff=!musicOnOrOff;
                musicControl();
            }
        });
        scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("status", musicOnOrOff);
                editor.commit();
                Intent intent=  new Intent(MainActivity.this,ScoreboardActivity.class);
                startActivity(intent);
            }
        });

        ObjectAnimator anim2 = ObjectAnimator.ofFloat(scores,"alpha",0.5f).setDuration(1000);
        anim2.setRepeatMode(ValueAnimator.REVERSE);
        anim2.setRepeatCount(Animation.INFINITE);
        animatorSet=new AnimatorSet();
        animatorSet.playTogether(anim1,anim2);
        animatorSet.start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.about){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.activity_text_dialog, null);
            TextView infoText = dialogView.findViewById(R.id.text);
            infoText.setText(R.string.about_text);
            builder.setView(dialogView).show();
        }
        else if(item.getItemId()==R.id.instructions){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.activity_text_dialog, null);
            TextView infoText = dialogView.findViewById(R.id.text);
            infoText.setText(R.string.instructions_text);
            builder.setView(dialogView).show();
        }
        return super.onOptionsItemSelected(item);
    }
    private void musicControl() {
        if (!musicOnOrOff) {
            backgroundMusic.stop();
            music.setImageResource(android.R.drawable.ic_lock_silent_mode);
        } else {
            backgroundMusic = MediaPlayer.create(MainActivity.this, R.raw.main);
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
            music.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
        }
    }

}
