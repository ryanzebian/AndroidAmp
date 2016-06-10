package com.example.androidamp3;

import java.io.File;
import java.io.IOException;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;


public class GuitarEffectsActivity extends Activity {
	private static final String TAG = "GuitarEffects";
	private PdUiDispatcher dispatcher;
	private PdService pdService = null;
	private boolean play = true;

	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			try {
				
				initPd();
				loadPatch(R.raw.recorder,"recorder.pd");
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// this method will never be called
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//initSystemServices();
		setContentView(R.layout.activity_guitar_effects);
		bindService(new Intent(this, PdService.class), pdConnection, BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(pdConnection);
	}
	//Initiliazes the Pure Data enviroment
	private void  initPd() throws IOException {
		// Configure the audio glue
		AudioParameters.init(this);
		int sampleRate = AudioParameters.suggestSampleRate();
		pdService.initAudio(sampleRate, 1, 1, 10.0f);//The guitar effects have one input channel and one outpuchannel (this could be changed later)
		start(); 

		// Create and install the dispatcher
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
	}

	private void start() {
		if (!pdService.isRunning()) {
			Intent intent = new Intent(GuitarEffectsActivity.this,
					GuitarEffectsActivity.class);
			pdService.startAudio(intent, R.drawable.icon,
					"GuitarEffects", "Return to GuitarEffects.");
		}
	}

	private void loadPatch(int res, String name) throws IOException {
		File dir = getFilesDir();
		IoUtils.extractZipResource(
				getResources().openRawResource(res), dir, true);
		File patchFile = new File(dir, name);
		PdBase.openPatch(patchFile.getAbsolutePath());
	}
	public void on_clicked(View view){
	
		if(play){
			
		PdBase.pauseAudio();
		}
		else{
	
		PdBase.startAudio();
		}
		play = !play;
	}
	public void playbuttonpressed(View view){
		PdBase.sendBang("start");

	}
	public void stopbuttonpressed(View view){
		PdBase.sendBang("stop");
	}
	//start the selected effect as a seperate activity through the use of intents
	public void RadioButtonClicked(View v) throws IOException{
		Intent effect = null;
		
		switch(v.getId()){
		case R.id.radio0://none guitar effect
			break;
		case R.id.radio1://Tremelo guitar effect
		
			effect = new Intent(this, Tremelo_effect.class);
			
			break;
		case R.id.radio2: //Fuzz guitar effect
			effect = new Intent(this, Fuzz_effect.class);
			
			break;
		case R.id.radio3://delay guitar effect
			effect = new Intent(this,Delay_effect.class);
			break;
		case R.id.radio4://Phasor Guitar Effect
			effect = new Intent(this,Phasor_effect.class);
			break;
		case R.id.radio5:// Whaauto Guitar Effect
			effect = new Intent(this,Whaauto_effect.class);
			break;
		}
		startActivity(effect);
	}
	//play the selected audio file
public void playaudio(View v){
	String playfile="";
	switch(v.getId()){
	case R.id.fuzzfile:
		playfile="playfuzz";
		break;
	case R.id.recorderfile:
		playfile="playrecorder";
		break;
	case R.id.tremelofile:
		playfile="playtremelo";
		break;
	case R.id.delayfile:
		playfile="playdelay";
		break;
	case R.id.phasorfile:
		playfile="playphasor";
		break;
	case R.id.Whaautofile:
		playfile="playwha";
		break;
	}
	PdBase.sendBang(playfile);
	}

	
	
	/*
	//In case of receiving a phone call when this applicati
	 * on is running
	private void initSystemServices() {
		TelephonyManager telephonyManager =
				(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				if (pdService == null) return;
				if (state == TelephonyManager.CALL_STATE_IDLE) {
					start(); } else {
						pdService.stopAudio(); }
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}


*/}