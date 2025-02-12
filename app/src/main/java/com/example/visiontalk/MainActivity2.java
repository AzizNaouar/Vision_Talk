package com.example.visiontalk;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.logging.Handler;

public class MainActivity2 extends AppCompatActivity {
    Animation top_anim, bottom_anim;
    ImageView image;
    TextView tv1, tv2;
    private static int SPLASH_SCREEN = 2900;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        //load animation
        top_anim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottom_anim = AnimationUtils.loadAnimation(this, R.anim.buttom_animation);

        image = findViewById(R.id.logo);
        tv1 = findViewById(R.id.textView);
        tv2 = findViewById(R.id.textView3);

        image.setAnimation(top_anim);
        tv1.setAnimation(bottom_anim);
        tv2.setAnimation(bottom_anim);


        new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(image, "mlogo");
                pairs[1] = new Pair<View, String>(tv1, "textlogo");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity2.this, pairs);
                    startActivity(intent, options.toBundle());
                }
            }
        }, SPLASH_SCREEN);




    }


}