package com.example.insbies;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Help extends AppCompatActivity {
    ActionBar actionBar;

    FloatingActionButton fab;

    private TextToSpeech myTTs;
    private SpeechRecognizer mySpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Help");

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //floatingActionButton

        fab=(FloatingActionButton) findViewById(R.id.help);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
                mySpeech.startListening(intent);
            }
        });


        initializeTextToSpeech();
        initializeSpeechRecognizer();



    }

    private void initializeSpeechRecognizer() {

        if(SpeechRecognizer.isRecognitionAvailable(this)){
            mySpeech=SpeechRecognizer.createSpeechRecognizer(this);
            mySpeech.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int i) {

                }

                @Override
                public void onResults(Bundle bundle) {

                    List<String> result=bundle.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                    );

                    processResult(result.get(0));

                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });
        }

    }

    private void processResult(String command) {
        command=command.toLowerCase();

        //what is your name?
        //what is the time?
        //open the browser
        if(command.indexOf("what")!=-1){

            if(command.indexOf("time")!=-1){
                Date now = new Date();
                String time= DateUtils.formatDateTime(this,now.getTime(),
                        DateUtils.FORMAT_SHOW_TIME);
                speak("The time is "+time);
            }
        }
        else if(command.indexOf("upcoming conferences")!=-1){
             speak("2020 IEEE Power & Energy Society Innovative Smart Grid Technologies Conference (ISGT) , 2020 IEEE International Solid- State Circuits Conference - (ISSCC) , " +
                     "2020 IEEE Aerospace Conference, 2020 IEEE Radio and Wireless Symposium (RWS)");
            // Intent intent=new Intent(Intent.ACTION_VIEW,
           //          Uri.parse("https://www.ieee.org/conferences/index.html"));
            // startActivity(intent);
        }

         else if(command.indexOf("publications of IEEE till now")!=-1){
             speak("IEEE Xplore®");
             speak("IEEE Spectrum®");
             speak("IEEE Open");
             speak("IEEE Access");
             speak("Proceedings of the IEEE");
             speak("IEEE DataPort");
            // Intent intent=new Intent(Intent.ACTION_VIEW,
                  //   Uri.parse("https://www.ieee.org/publications/index.html"));
            // startActivity(intent);
         }
        else if(command.indexOf("renew membership")!=-1)
         {
             Intent intent=new Intent(Intent.ACTION_VIEW,
                     Uri.parse("https://www.ieee.org/membership/join-renew.html?WT.mc_id=mem_lp_jor"));
             startActivity(intent);
         }
         else {
             speak("i cann't reach ");
         }

    }

    private void initializeTextToSpeech() {
        myTTs=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(myTTs.getEngines().size()==0)
                {
                    Toast.makeText(Help.this, "There is no TTs engine on device  ",
                            Toast.LENGTH_SHORT).show();

                }
                else
                {
                    myTTs.setLanguage(Locale.US);
                    speak("How can i help you.");
                }

            }
        });
    }

    private void speak(String s) {
        if(Build.VERSION.SDK_INT>=21){
            myTTs.speak(s,TextToSpeech.QUEUE_FLUSH,null,null);
        }else {
            myTTs.speak(s,TextToSpeech.QUEUE_FLUSH,null);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_add_post).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myTTs.shutdown();
    }
}
