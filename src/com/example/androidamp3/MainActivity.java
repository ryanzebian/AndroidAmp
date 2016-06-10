package com.example.androidamp3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void start_tuner(View view){
		Intent tuner_intent = new Intent(this, GuitarTunerActivity.class);
		startActivity(tuner_intent);
	}
	public void start_guitarEffects(View view){
		Intent effect_intent = new Intent(this, GuitarEffectsActivity.class);
		startActivity(effect_intent);
	}

}
