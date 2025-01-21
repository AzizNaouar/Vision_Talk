package com.example.visiontalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.RecognizerIntent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
public class MainActivity extends AppCompatActivity
{
    private GestureDetector gestureDetector;

    Map<String, List<String>> categories = new HashMap<>();
    {
        categories.put("Action", Arrays.asList("action", "combat", "aventure"));
        categories.put("Comédie", Arrays.asList("comédie", "drôle", "humour"));
        categories.put("Drame", Arrays.asList("drame", "émotion", "tragédie"));
    }


    TextView t;
    int n=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         this.t = findViewById(R.id.r);

        ConstraintLayout l = findViewById(R.id.lay);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_NbrClique();
            }
        });


    }
    public void Add_NbrClique()
    {
        n++;
        if(n==3)
        {
            activateVoiceRecognition();
        }
    }

    private void activateVoiceRecognition()
    {
        n=0;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez maintenant...");

        try
        {

            startActivityForResult(intent, 1001);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "La reconnaissance vocale n'est pas disponible.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {

            // Récupérer le texte détecté
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (results != null && !results.isEmpty()) {
                String recognizedText = results.get(0); // Premier résultat de la reconnaissance vocale
               // Toast.makeText(this, "Texte détecté : " + recognizedText, Toast.LENGTH_LONG).show();
                String detectedCategory=this.detectCategory(recognizedText);
                this.t.setText(detectedCategory);
            }
        }
    }

    private String detectCategory(String userSpeech)
    {
        for (Map.Entry<String, List<String>> entry : categories.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (userSpeech.contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return "Catégorie non reconnue";
    }




}