package com.hit.whowantstobeaprogrammer;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import java.util.Random;

public class GamePlayActivity extends AppCompatActivity {
    LinearLayout gameLayout;
    FloatingActionButton music;
    Boolean musicOnOrOff;
    public SharedPreferences sp;
    MediaPlayer backgroundMusic;
    AnswerButton Answer1;
    AnswerButton Answer2;
    AnswerButton Answer3;
    AnswerButton Answer4;
    AnswerButton Answer;
    TextView QuestionTV;
    ArrayList<String>Answers;
    ArrayList<Integer>easyIds;
    ArrayList<Integer>mediumIds;
    ArrayList<Integer>hardIds;
    int level = 1;
    CountDownTimer cTimer = null;
    TextView ScoreTV;
    TextView NameTV;
    SharedPreferences.Editor editor;
    private int score;
    int answerCunter = 0;
    int life = 3;
    ImageView life1EV;
    ImageView life2EV;
    ImageView life3EV;

    void startTimer() {
        cTimer = new CountDownTimer(10000, 1000) {
            TextView timer=findViewById(R.id.timer_Tv);
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText("" + millisUntilFinished / 1000);
            }
            @Override
            public void onFinish() {
                gameOver();
            }
        }.start();
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
        editor =sp.edit();
        editor.putBoolean("status",musicOnOrOff);
        editor.commit();
        backgroundMusic.pause();
        cTimer.cancel();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor =sp.edit();
        editor.putBoolean("status",musicOnOrOff);
        editor.commit();
        backgroundMusic.stop();
        cTimer.cancel();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        gameLayout=findViewById(R.id.game_layout);
        sp = getSharedPreferences("user_details",MODE_PRIVATE);
        musicOnOrOff = sp.getBoolean("status",true);
        backgroundMusic = new MediaPlayer();
        backgroundMusic = MediaPlayer.create(GamePlayActivity.this,R.raw.gameplay);
        score=sp.getInt("score",0);
        NameTV=findViewById(R.id.name);
        NameTV.setText(sp.getString("user_name","")+"");
        ScoreTV = findViewById(R.id.score);
        //ScoreTV.setText(sp.getString("user_name","")+", Score:"+ score);
        SplitLevelQuestions();
        QuestionTV=findViewById(R.id.question_tv);
        Answer1=findViewById(R.id.answer1_btn);
        Answer2=findViewById(R.id.answer2_btn);
        Answer3=findViewById(R.id.answer3_btn);
        Answer4=findViewById(R.id.answer4_btn);
        life1EV=findViewById(R.id.life1);
        life2EV=findViewById(R.id.life2);
        life3EV=findViewById(R.id.life3);
        music =findViewById(R.id.music_game_btn);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicOnOrOff=!musicOnOrOff;
                musicControl();
            }
        });
        musicControl();
        StartGame();
    }

    public void StartGame() {
        startTimer();
        load();
        Answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerClickable(false);
                answerCheck(Answer1);
            }
        });
        Answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerClickable(false);
                answerCheck(Answer2);
            }
        });
        Answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerClickable(false);
                answerCheck(Answer3);
            }
        });
        Answer4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AnswerClickable(false);
                answerCheck(Answer4);
            }
        });
    }


    private void AnswerClickable(boolean bool){
        Answer1.setClickable(bool);
        Answer2.setClickable(bool);
        Answer3.setClickable(bool);
        Answer4.setClickable(bool);
    }


    private void answerCheck(AnswerButton clicked){
        cTimer.cancel();

        clicked.ButtonSelected();
        if(clicked == Answer){
            score+= level==1?100:level==2?200:300;
            answerCunter++;
            level = answerCunter<4?1:answerCunter<8?2:3;
            clicked.CorrectAnswer(true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clicked.ButtonReset();
                    load();
                }
            }, 2000);
        }
        else {
            life--;
            cTimer.cancel();
            updateLifeImege();
            clicked.WrongAnswer();
            Answer.CorrectAnswer(false);
            if(life==0) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clicked.ButtonReset();
                        Answer.ButtonReset();
                        gameOver();
                    }
                }, 2000);
            }
            else{
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clicked.ButtonReset();
                        Answer.ButtonReset();
                        load();
                    }
                }, 2000);
            }
        }
    }

    private void updateLifeImege(){
        if(life==2){
            life3EV.setImageResource(0);
        }
        else if(life==1){
            life2EV.setImageResource(0);
        }
        else if(life==0){
            life1EV.setImageResource(0);
        }
    }

    private Integer[] randomizeAnswers(){
        Integer[] intArray = {0, 1, 2, 3};
        List<Integer> intList = Arrays.asList(intArray);
        Collections.shuffle(intList);
        return intList.toArray(intArray);
    }

    private int randomizeQuestion(ArrayList<Integer> curIds){
        Random r = new Random();
        int result = r.nextInt(curIds.size() + 0);
        return result;
    }

    private void gameOver(){
        editor = sp.edit();
        editor.putInt("score",score);
        editor.putBoolean("status",musicOnOrOff);
        editor.commit();
        Intent intent = new Intent(GamePlayActivity.this,GameOverActivity.class);
        startActivity(intent);
        finish();

    }

    private void load(){
        if(!hardIds.isEmpty()) {
            cTimer.cancel();
            cTimer.start();
            ScoreTV.setText(score+"");
            AnswerClickable(true);
            String [] str = null;
            if (level == 1) {
                gameLayout.setBackground(getDrawable(R.drawable.backgroundgreen));
                int questionNumber = randomizeQuestion(easyIds);
                str = getResources().getStringArray(easyIds.get(questionNumber).intValue());
                easyIds.remove(questionNumber);
            } else if (level == 2) {
                gameLayout.setBackground(getDrawable(R.drawable.gameplaybackground));
                int questionNumber = randomizeQuestion(mediumIds);
                str = getResources().getStringArray(mediumIds.get(questionNumber).intValue());
                mediumIds.remove(questionNumber);
            } else if (level == 3) {
                gameLayout.setBackground(getDrawable(R.drawable.backgroundred));
                int questionNumber = randomizeQuestion(hardIds);
                str = getResources().getStringArray(hardIds.get(questionNumber).intValue());
                hardIds.remove(questionNumber);
            }
            Answers = new ArrayList<>();
            Answers.add(str[1]);
            Answers.add(str[2]);
            Answers.add(str[3]);
            Answers.add(str[4]);
            QuestionTV.setText(str[0]);
            Integer[] ran = randomizeAnswers();
            Answer1.setText(Answers.get(ran[0]));
            Answer2.setText(Answers.get(ran[1]));
            Answer3.setText(Answers.get(ran[2]));
            Answer4.setText(Answers.get(ran[3]));
            int indexAnswer = -1;
            for (int i = 0; i < 4; ++i) {
                if (ran[i] == 0) {
                    indexAnswer = i;
                    break;
                }
            }
            if (indexAnswer == 0) {
                Answer = Answer1;
            } else if (indexAnswer == 1) {
                Answer = Answer2;
            } else if (indexAnswer == 2) {
                Answer = Answer3;
            } else if (indexAnswer == 3) {
                Answer = Answer4;
            }
        }
        else{
            gameOver();
        }
    }

    private void musicControl() {
        if (!musicOnOrOff) {
            backgroundMusic.stop();
            music.setImageResource(android.R.drawable.ic_lock_silent_mode);
        } else {
            backgroundMusic = MediaPlayer.create(GamePlayActivity.this, R.raw.gameplay);
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
            music.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
        }
    }

    private void SplitLevelQuestions(){
        Field [] fields= R.array.class.getFields();
        easyIds= new ArrayList<Integer>();
        mediumIds= new ArrayList<Integer>();
        hardIds= new ArrayList<Integer>();
        for (int i=0;i<fields.length;++i){
            String questionName = fields[i].getName();
            char current = questionName.charAt(0);
            if(current == 'E'){
                easyIds.add(Integer.valueOf(getResources().getIdentifier("array/" + fields[i].getName(), null, getPackageName())));
            }
            else if(current == 'M'){
                mediumIds.add(Integer.valueOf(getResources().getIdentifier("array/" + fields[i].getName(), null, getPackageName())));
            }
            else if(current == 'H'){
                hardIds.add(Integer.valueOf(getResources().getIdentifier("array/" + fields[i].getName(), null, getPackageName())));
            }
        }
    }
}