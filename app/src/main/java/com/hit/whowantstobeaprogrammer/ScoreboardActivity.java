package com.hit.whowantstobeaprogrammer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.ArrayMap;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardActivity extends AppCompatActivity {
    Map<String, Integer> scoreMap;
    ListView scoreboard;
    MediaPlayer backgroundMusic;
    Boolean musicOnOrOff;
    public SharedPreferences sp;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        sp = getSharedPreferences("user_details",MODE_PRIVATE);
        musicOnOrOff = sp.getBoolean("status",true);
        backgroundMusic = new MediaPlayer();
        musicControl();

        scoreboard = findViewById(R.id.scoreboard_LV);
        scoreMap = new HashMap<String, Integer>();
        try {
            loadScoreboard();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mapToArr());
        //scoreboard.setAdapter(arrayAdapter);
        SimpleAdapter simpleAdapter = createSimpleAdapter();
        scoreboard.setAdapter(simpleAdapter);
    }

    private SimpleAdapter createSimpleAdapter(){
        String[] names = scoreMap.keySet().toArray(new String[0]);
        Integer[] scores=scoreMap.values().toArray(new Integer[0]);
        List<Map<String,Object>> data = new ArrayList<>();
        for(int i=0; i<names.length; ++i){
            HashMap<String,Object> item = new HashMap<>();
            item.put("name",names[i]);
            item.put("score",scores[i]);
            if(i==0){
                item.put("img",R.drawable.cup1);
            }
            else if(i==1){
                item.put("img",R.drawable.cup2);
            }
            else{
                item.put("img",R.drawable.cup3);
            }
            data.add(item);
        }
        String[] from = {"name","score","img"};
        int[] ids = {R.id.nameboard,R.id.scoreboard,R.id.cup};
        return new SimpleAdapter(this,data,R.layout.scoreboard_cell,from,ids);
    }
    /*private ArrayList<String> mapToArr(){
        String[] names = scoreMap.keySet().toArray(new String[0]);
        Integer[] scores=scoreMap.values().toArray(new Integer[0]);
        ArrayList<String>namesAndScores= new ArrayList<String>();

        for(int i=0;i<names.length;++i){
            namesAndScores.add(i,"name:"+names[i]+" score:"+scores[i].toString());
        }
        return namesAndScores;
    }*/
    private void loadScoreboard() throws IOException {

        FileInputStream fileInputStream= openFileInput("scoreboard.hit");
        ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
        try {
            scoreMap = (HashMap)objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        objectInputStream.close();
    }

    private void musicControl() {
        if (!musicOnOrOff) {
            backgroundMusic.stop();
        } else {
            backgroundMusic = MediaPlayer.create(ScoreboardActivity.this, R.raw.gameover);
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
        }
    }
}