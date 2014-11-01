package yupe.diandian;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class DrumsetActivity extends Activity implements SensorEventListener {
	private static final String TAG = "drumset";
	private SensorManager mSensorManager;
	private Sensor mOrientation;
	private Sensor mAccelerometer;
	private Util.SoundManager mSoundManager;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView( R.layout.lifx_main_activity_layout);
		mContext = this;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mSensorManager.registerListener(this, mOrientation,SensorManager.SENSOR_DELAY_GAME);

		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);

		mSoundManager = Util.SoundManager.getInstance(mContext);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// testPlayAudio();
	}

	private void testPlayAudio() {
		new Thread() {
			@Override
			public void run() {
				Random sepRandom = new Random(System.currentTimeMillis());
				Random idxRandom = new Random(System.currentTimeMillis()/1000);
				for (int i = 0; i < 240; i++) {
					
					int idx = Math.abs(idxRandom.nextInt())%(DrumsetConstant.DRUMSET_AUDIO.length-1);
					mSoundManager.playSound(idx);

					try {
						int sep = (Math.abs(sepRandom.nextInt())%7) * 200;
						Thread.sleep(sep);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int currentVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:// 音量增大
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					currentVolume + 1, 1);
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:// 音量减小
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					currentVolume - 1, 1);
			break;
		case KeyEvent.KEYCODE_BACK:// 返回键
			// jniOnCallCppEvent();
			return true;
			// return super.onKeyDown(keyCode, event);
		default:
			break;
		}
		return true;
		// return super.onKeyDown(keyCode, event);
	}


	private int oX = 0;
	private int ooX = 0;
	private int deltaO = 0;
	private boolean overZero = true;
	private int initOrientation = 180;
	private int audioIndex = 0;
	private boolean isInited = false;
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			Log.i(TAG + "O", "X[" + x + "] Y[" + y + "] Z[" + z + "]");
			oX = (int)x;
			if(isInited) {
				deltaO = getOrientationDelta(oX);
				audioIndex = getDurmAudioIndex(oX);
				Log.i(TAG + "ADO","delta: " + deltaO + "   audioIdx:" + audioIndex);
			}
		} else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			if (z < 0 ) {
				overZero = true;
			}
			if (z - 10 > 8) {
				Log.i(TAG + "AZERO",""+ (z-10)+ "        " + overZero);
				//if(Math.abs(deltaX) > 50) {
				if(overZero) {
					mSoundManager.playSound(audioIndex);
					ooX = oX;
					overZero = false;
					// init
					if (!isInited) {
						isInited = true;
						initOrientation = oX;
						Log.i(TAG, "init orientation: " + initOrientation);	
					}
				}
			}
			Log.i(TAG + "A", "AX[" + x + "] AY[" + y + "] AZ[" + z + "]");
		}

	}

	private int getOrientationDelta(int currOrientation) {
		if (initOrientation >= 0 && initOrientation < 180) {
			if (currOrientation - initOrientation < 0) {
				return currOrientation - initOrientation;
			} else if (currOrientation - initOrientation >= 0 && currOrientation - initOrientation < 180) {
				return currOrientation - initOrientation;
			} else {// o-initO >= 180
				return currOrientation - 360 - initOrientation;
			}
		} else { // 180 <= initO < 360
			if (currOrientation - initOrientation < -180) {
				return currOrientation + (360 - initOrientation);
			} else if (currOrientation - initOrientation >= -180 && currOrientation - initOrientation < 0) {
				return currOrientation - initOrientation;
			} else { // o-initO >= 0
				return currOrientation - initOrientation;
			}
		}
	}

	private int getDurmAudioIndex(int currOrientation) {
		int delta = getOrientationDelta(currOrientation);
		int wholeSpan = 180;
		int lowerBound = 0 - wholeSpan / 2;
		int upperBound = 0 + wholeSpan / 2;
		int intervalCnt = DrumsetConstant.DRUMSET_AUDIO.length;
		int intervalSpan = wholeSpan / intervalCnt;
		for (int i = 0; i < intervalCnt; i++) {
			int ilb = lowerBound + i * intervalSpan;
			int iub = lowerBound + (i + 1) * intervalSpan;
			if (delta < ilb || (delta >= ilb && delta < iub)) {
				return i;
			}
		}
		return intervalCnt - 1;
	}
}
