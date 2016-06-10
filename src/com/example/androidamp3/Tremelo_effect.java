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


public class Tremelo_effect extends Activity {
	private static final String TAG = "TremeloEffect";
	private PdUiDispatcher dispatcher;
	private PdService pdService = null;

	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			try {
				
				initPd();
				loadPatch(R.raw.tremelo,"tremelo.pd");
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
		setContentView(R.layout.activity_tremelo_effect);
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
			Intent intent = new Intent(Tremelo_effect.this,
					Tremelo_effect.class);
			pdService.startAudio(intent, R.drawable.icon,
					"Tremelo_effect", "Return to Tremelo_effect.");
		}
	}

	private void loadPatch(int res, String name) throws IOException {
		File dir = getFilesDir();
		IoUtils.extractZipResource(
				getResources().openRawResource(res), dir, true);
		File patchFile = new File(dir, name);
		PdBase.openPatch(patchFile.getAbsolutePath());
	}
	public void configure(View view){ 
		EditText ratetext = (EditText)findViewById(R.id.rate);
		EditText shapetext = (EditText)findViewById(R.id.shape);
		EditText depthtext = (EditText)findViewById(R.id.depth);
		Float rate = Float.valueOf(ratetext.getText().toString());
		Float shape = Float.valueOf(shapetext.getText().toString());
		Float depth = Float.valueOf(depthtext.getText().toString());
		
		PdBase.sendFloat("rate",rate);
		PdBase.sendFloat("shape",shape);
		PdBase.sendFloat("depth",depth);
		
	}
//start recording the recordbutton has been pressed
	public void recordbuttonpressed(View view){
		PdBase.sendBang("start");

	}
	//stop recording
	public void stopbuttonpressed(View view){
		PdBase.sendBang("stop");
	}

}
