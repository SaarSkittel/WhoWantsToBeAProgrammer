package com.hit.whowantstobeaprogrammer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    @Override
    protected void onPause() {
        super.onPause();
        backgroundMusic.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundMusic.stop();
        musicOnOrOff = sp.getBoolean("status", true);
        musicControl();
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
                builder.setView(dialogView).setPositiveButton(R.string.Start, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = userNameET.getText().toString();
                        name = name.isEmpty()? getResources().getString(R.string.guest):name;
                        SharedPreferences.Editor editor = sp.edit();
                        //editor.putString("user_name", name);
                        editor.putInt("score", 0);
                        editor.putBoolean("status", musicOnOrOff);
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this, GamePlayActivity.class);

                        intent.putExtra("name",name);

                        startActivity(intent);
                    }
                }).show();
            }
        });



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
            builder.setView(dialogView).setNegativeButton(R.string.Back, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }else if(item.getItemId()==R.id.instructions){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.activity_text_dialog, null);
            TextView infoText = dialogView.findViewById(R.id.text);
            infoText.setText(R.string.instructions_text);
            builder.setView(dialogView).setNegativeButton(R.string.Back, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
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
