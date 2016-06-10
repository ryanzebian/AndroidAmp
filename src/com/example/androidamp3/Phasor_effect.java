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
import android.widget.EditText;
import android.widget.TextView;


public class Phasor_effect extends Activity {

	private static final String TAG = "PhaserEffect";
	private PdUiDispatcher dispatcher;
	private PdService pdService = null;

	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			try {
				
				initPd();
				loadPatch(R.raw.phaser,"phaser.pd");
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
		setContentView(R.layout.activity_phasor_effect);
		bindService(new Intent(this, PdService.class), pdConnection, BIND_AUTO_CREATE);	
		/*
		//also initialize the value for the number boxes in the PD project
		//send the float value to the speed number in the PD project
		PdBase.sendFloat("speed", (float) 0.7);
		//sends the float value to the depth number box in the PD project
		PdBase.sendFloat("depth", (float) 2.4);
		//does the same and send to the feedback
		PdBase.sendFloat("feedback", (float) -0.3);
	*/}

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
			Intent intent = new Intent(Phasor_effect.this,
					Phasor_effect.class);
			pdService.startAudio(intent, R.drawable.icon,
					"Phasor_effect", "Return to Phasor_effect.");
		}
	}

	private void loadPatch(int res, String name) throws IOException {
		File dir = getFilesDir();
		IoUtils.extractZipResource(
				getResources().openRawResource(res), dir, true);
		File patchFile = new File(dir, name);
		PdBase.openPatch(patchFile.getAbsolutePath());
	}
	//start recroding
	public void start(View view){
		PdBase.sendBang("start"); //sends a bang/signal for the recorder or writesf~ to start in fuzz.pd
	}
	//stop recording
	public void stop(View view){
		PdBase.sendBang("stop"); //sends a bang/signal for the writesf~ function to stop in fuzz.pd
	}
	//configure method being called to interact with the phasor.pd
	public void Configure(View view){
		EditText depth_text = (EditText)findViewById(R.id.depth_phasor);
		float depth = Float.valueOf(depth_text.getText().toString());
		
		EditText speed_text = (EditText)findViewById(R.id.speed_phasor_value);
		float speed = Float.valueOf(speed_text.getText().toString());
		
		//send the float value to the speed number in the PD project
		PdBase.sendFloat("speed", speed);
		//sends the float value to the depth number box in the PD project
		PdBase.sendFloat("depth", depth);
	
		;}
	
}
