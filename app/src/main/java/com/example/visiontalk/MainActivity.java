package com.example.visiontalk;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer; // MediaPlayer pour jouer l'audio
    private TextToSpeech textToSpeech; // TextToSpeech pour informer l'utilisateur
    private boolean isCategoryRecognized = false; // Variable pour suivre si la catégorie est reconnue
    private String selectedCategory = ""; // Catégorie sélectionnée
    private Map<String, List<String>> categories = new HashMap<>();
    private Map<String, List<Integer>> stories = new HashMap<>(); // Liste des histoires par catégorie

    {
        categories.put("Action", Arrays.asList("action", "combat", "aventure"));
        categories.put("Comédie", Arrays.asList("comédie", "drôle", "humour"));
        categories.put("Drame", Arrays.asList("drame", "émotion", "tragédie"));

        // Associer chaque catégorie à une liste d'histoires (fichiers audio)
        stories.put("Comédie", Arrays.asList(R.raw.fable, R.raw.alice));
    }

    int n = 0;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser TextToSpeech
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.FRENCH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "Langue non supportée", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Échec de l'initialisation de TTS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        image = findViewById(R.id.imageView);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_NbrClique();
            }
        });
    }

    public void Add_NbrClique() {
        n++;
        if (n == 3) {
            // Vérifier si l'audio est en cours de lecture
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                stopAudio(); // Arrêter l'audio

            } else {
                activateVoiceRecognition(); // Activer la reconnaissance vocale
            }
            n = 0; // Réinitialiser le compteur de clics
        }
    }

    // Capturer audio
    private void activateVoiceRecognition() {
        n = 0;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez maintenant...");

        try {
            startActivityForResult(intent, 1001);
        } catch (Exception e) {
            Toast.makeText(this, "La reconnaissance vocale n'est pas disponible.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (results != null && !results.isEmpty()) {
                String recognizedText = results.get(0); // Premier résultat de la reconnaissance vocale

                if (selectedCategory.isEmpty()) {
                    // Détecter la catégorie
                    String detectedCategory = detectCategory(recognizedText);
                    //Toast.makeText(this, "Catégorie détectée : " + detectedCategory, Toast.LENGTH_LONG).show();

                    if (detectedCategory.equals("Catégorie non reconnue")) {
                        // Si la catégorie n'est pas reconnue, informez l'utilisateur
                        speakTextCategorie("Bonjour cher Youssef, voilà les catégories existantes : enfant, action, comédie.", false);
                    } else {
                        // Catégorie reconnue, demander à l'utilisateur de choisir une histoire
                        selectedCategory = detectedCategory;
                        speakTextCategorie("Pour la catégorie " + detectedCategory + ", et enfant voilà les histoires disponibles : Fable de la Fontaine et Alice au pays des merveilles. Dites votre choix.", true);
                    }
                }
                else {
                    // Gérer le choix de l'utilisateur (histoire ou commande "stop")
                    handleUserChoice(recognizedText);
                }
            }
        }
    }

    private void handleUserChoice(String userChoice) {
        if (userChoice.contains("1") || userChoice.contains("un")) {
            // Jouer l'histoire 1
            playAudio(stories.get("Comédie").get(0));

        } else if (userChoice.contains("2") || userChoice.contains("deux") || userChoice.contains("Alice")) {
            // Jouer l'histoire 2
            playAudio(stories.get("Comédie").get(1));
        } else if (userChoice.contains("stop")) {
            // Arrêter l'audio
            stopAudio();
        } else {
            // Choix non reconnu
            speakTextCategorie("Choix non reconnu. Veuillez dire '1', '2' ou 'stop'.", false);
        }
    }

    private void speakTextCategorie(String text, boolean isCategoryRecognized) {
        if (textToSpeech != null) {
            // Créer un Bundle pour les paramètres supplémentaires
            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "endOfSpeech");

            // Lire le texte avec TTS
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params, "endOfSpeech");

            if (isCategoryRecognized) {
                // Détecter la fin de la parole
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        // Début de la parole
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (utteranceId.equals("endOfSpeech")) {
                            // Activer la reconnaissance vocale pour le choix de l'utilisateur
                            activateVoiceRecognition();
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        // Gérer les erreurs
                    }
                });
            }
        }
    }

    private void playAudio(int audioResourceId) {
        // Arrêter tout audio en cours de lecture
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Jouer le fichier audio correspondant
        mediaPlayer = MediaPlayer.create(this, audioResourceId);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Libérer les ressources du MediaPlayer une fois la lecture terminée
                mp.release();
                mediaPlayer = null;
            }
        });
        mediaPlayer.start();
    }

    private void stopAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Toast.makeText(this, "Audio arrêté.", Toast.LENGTH_SHORT).show();

        }
    }

    private String detectCategory(String userSpeech) {
        for (Map.Entry<String, List<String>> entry : categories.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (userSpeech.contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return "Catégorie non reconnue";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libérer les ressources du MediaPlayer et du TextToSpeech
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}