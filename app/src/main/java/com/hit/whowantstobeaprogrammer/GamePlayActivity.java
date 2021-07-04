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
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.Map;
import java.util.Random;

public class GamePlayActivity extends AppCompatActivity {

    Boolean musicOnOrOff;
    SharedPreferences sp;
    MediaPlayer backgroundMusic;
    AnswerButton Answer1;
    AnswerButton Answer2;
    AnswerButton Answer3;
    AnswerButton Answer4;
    AnswerButton Answer;
    TextView QuestionTV;
    ArrayList<String>Answers;
    ArrayList<Integer>ids;
    CountDownTimer cTimer = null;
    TextView Name;
    private int score;

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
        backgroundMusic.start();
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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("user_details",MODE_PRIVATE);
        musicOnOrOff = sp.getBoolean("status",true);
        setContentView(R.layout.activity_game_play);
        backgroundMusic = new MediaPlayer();
        backgroundMusic = MediaPlayer.create(GamePlayActivity.this,R.raw.background);
        Name = findViewById(R.id.name_score);
        Name.setText(sp.getString("user_name","")+", Score:"+sp.getInt("score",0));
        musicOnOrOff = sp.getBoolean("status",true);
        score=sp.getInt("score",0);
        Field [] fields= R.array.class.getFields();
        ids= new ArrayList<Integer>(fields.length);

        for (int i=0;i<fields.length;++i){
            ids.add(Integer.valueOf(getResources().getIdentifier("array/" + fields[i].getName(), null, getPackageName())));
        }

        if(musicOnOrOff){
            backgroundMusic.start();
        }
        QuestionTV=findViewById(R.id.question_tv);
        Answer1=findViewById(R.id.answer1_btn);
        Answer2=findViewById(R.id.answer2_btn);
        Answer3=findViewById(R.id.answer3_btn);
        Answer4=findViewById(R.id.answer4_btn);
        StartGame();
    }

    public void StartGame() {
        startTimer();
        load();
        Answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerCheck(Answer1);
            }
        });
        Answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerCheck(Answer2);
            }
        });
        Answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerCheck(Answer3);
            }
        });
        Answer4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                answerCheck(Answer4);
            }
        });
    }

    private void answerCheck(AnswerButton clicked){
        clicked.ButtonSelected();
        if(clicked == Answer){
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
            cTimer.cancel();
            clicked.WrongAnswer();
            Answer.CorrectAnswer(false);
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
    }

    private Integer[] randomizeAnswers(){
        Integer[] intArray = {0, 1, 2, 3};
        List<Integer> intList = Arrays.asList(intArray);
        Collections.shuffle(intList);
        return intList.toArray(intArray);
    }

    private int randomizeQuestion(){
        Random r = new Random();
        int result = r.nextInt(ids.size() + 0);

        return result;
    }
    private void gameOver(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("score",score);
        Intent intent = new Intent(GamePlayActivity.this,GameOverActivity.class);
        startActivity(intent);
        finish();

    }
    private void load(){
        cTimer.cancel();
        cTimer.start();
        int questionNumber = randomizeQuestion();
        String [] str=getResources().getStringArray(ids.get(questionNumber).intValue());
        ids.remove(questionNumber);
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
        for(int i=0;i<4;++i){
            if(ran[i]==0){
                indexAnswer =i;
                break;
            }
        }
        if(indexAnswer==0){
            Answer=Answer1;
        }
        else if(indexAnswer==1){
            Answer=Answer2;
        }
        else if(indexAnswer==2){
            Answer=Answer3;
        }
        else if(indexAnswer==3){
            Answer=Answer4;
        }
    }

}