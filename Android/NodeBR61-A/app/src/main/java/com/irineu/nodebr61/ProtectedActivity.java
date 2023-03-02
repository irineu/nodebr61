package com.irineu.nodebr61;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

public class ProtectedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protected);

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);
        Snackbar.make(layout, "Wellcome!", Snackbar.LENGTH_SHORT).show();

    }
}