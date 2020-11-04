package com.example.musico;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class PlayerActivity extends AppCompatActivity {

    static MediaPlayer mp;//assigning memory loc once or else multiple songs will play at once
    int position;
    SeekBar sb;
    ArrayList<File> mySongs;
    Thread updateSeekBar;
    Button pause,next,previous;
    TextView songNameText;
    TextToSpeech textToSpeech;


    String sname;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_ui);

        songNameText = (TextView) findViewById(R.id.txtSongLabel);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Now Playing");

        pause = (Button)findViewById(R.id.pause);

        previous = (Button)findViewById(R.id.previous);
        next = (Button)findViewById(R.id.next);

        sb=(SeekBar)findViewById(R.id.seekBar);

        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
         public void onInit(int status) {
             if(status != TextToSpeech.ERROR) {
                 textToSpeech.setLanguage(Locale.UK);
             }
         }
        });


        updateSeekBar=new Thread(){
            @Override
            public void run(){
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
                while(currentPosition < totalDuration){
                    try{
                        sleep(500);
                        currentPosition=mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    }
                    catch (InterruptedException e){

                        }
                }
            }
        };




        if(mp != null){
            mp.stop();
            mp.release();
        }
        Intent i = getIntent();
        Bundle b = i.getExtras();


        mySongs = (ArrayList) b.getParcelableArrayList("songs");

        sname = mySongs.get(position).getName().toString();

        String SongName = i.getStringExtra("songname");
        songNameText.setText(SongName);
        songNameText.setSelected(true);

        position = b.getInt("pos",0);
        Uri u = Uri.parse(mySongs.get(position).toString());

        mp = MediaPlayer.create(getApplicationContext(),u);
        mp.start();
        sb.setMax(mp.getDuration());
        updateSeekBar.start();
        sb.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        sb.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        sb.setOnSeekBarChangeListener(new
                                              SeekBar.OnSeekBarChangeListener() {
                                                  @Override
                                                  public void onProgressChanged(SeekBar seekBar, int i,
                                                                                boolean b) {
                                                  }
                                                  @Override
                                                  public void onStartTrackingTouch(SeekBar seekBar) {
                                                  }
                                                  @Override
                                                  public void onStopTrackingTouch(SeekBar seekBar) {
                                                      mp.seekTo(seekBar.getProgress());

                                                  }
                                              });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String speak = pause.getText().toString().trim();
                speakMethod(speak);
                sb.setMax(mp.getDuration());
                if(mp.isPlaying()){
                    pause.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                    mp.pause();

                }
                else {
                    pause.setBackgroundResource(R.drawable.pause);
                    mp.start();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String speak = next.getText().toString().trim();
                speakMethod(speak);
                mp.stop();
                mp.release();
                position=((position+1)%mySongs.size());
                Uri u = Uri.parse(mySongs.get( position).toString());
               // songNameText.setText(getSongName);
                mp = MediaPlayer.create(getApplicationContext(),u);

                sname = mySongs.get(position).getName().toString();
                songNameText.setText(sname);

                try{
                    mp.start();
                }catch(Exception e){}

            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String speak = previous.getText().toString().trim();
                speakMethod(speak);
                //songNameText.setText(getSongName);
                mp.stop();
                mp.release();

                position=((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(),u);
                sname = mySongs.get(position).getName().toString();
                songNameText.setText(sname);
                mp.start();
            }
        });


    }

    private void speakMethod(String speak) {
        textToSpeech.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
           onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}