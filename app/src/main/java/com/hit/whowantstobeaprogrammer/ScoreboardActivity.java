package com.hit.whowantstobeaprogrammer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.ArrayMap;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardActivity extends AppCompatActivity {
    Map<String, Integer> scoreMap;
    ListView scoreboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        scoreboard = findViewById(R.id.scoreboard_LV);
        scoreMap = new HashMap<String, Integer>();
        try {
            loadScoreboard();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mapToArr());
        scoreboard.setAdapter(arrayAdapter);
    }
    private ArrayList<String> mapToArr(){
        String[] names = scoreMap.keySet().toArray(new String[0]);
        Integer[] scores=scoreMap.values().toArray(new Integer[0]);
        ArrayList<String>namesAndScores= new ArrayList<String>();

        for(int i=0;i<names.length;++i){
            namesAndScores.add(i,"name:"+names[i]+" score:"+scores[i].toString());
        }
        return namesAndScores;
    }
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
}