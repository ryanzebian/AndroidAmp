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

public class Delay_effect extends Activity {
	private static final String TAG = "DelayEffect";
	private PdUiDispatcher dispatcher;
	private PdService pdService = null;
	private boolean play = true; //for pausing and playing the audio

	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			try {
				
				initPd();
				loadPatch(R.raw.delay_effect,"delay_effect.pd");
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
		setContentView(R.layout.activity_delay_effect);
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
			Intent intent = new Intent(Delay_effect.this,
					Delay_effect.class);
			pdService.startAudio(intent, R.drawable.icon,
					"Delay_effect", "Return to Delay_effect.");
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
	public void record(View view){
		PdBase.sendBang("start"); //sends a bang/signal for the recorder or writesf~ to start in fuzz.pd
	}
	public void stop(View view){
		PdBase.sendBang("stop"); //sends a bang/signal for the writesf~ function to stop in fuzz.pd
	}
}
