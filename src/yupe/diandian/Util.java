package yupe.diandian;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.SparseArray;

public class Util {
	public static void playAudio(Context ctx, int resId) {
		MediaPlayer a = new MediaPlayer();
		try {
			a = MediaPlayer.create(ctx, resId);
			a.prepare();
			a.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static class SoundManager {
	    private static boolean sIsSoundTurnedOff = false;
	    private static SoundManager sSoundManager;   
	    private static final int MAX_SOUNDS = 8;
	    private static final SoundPool sSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0); 
	    private static final SparseArray <Integer> sSoundPoolMap = new SparseArray<Integer>();  
	    private AudioManager  mAudioManager;

	    public static SoundManager getInstance(Context context)
	    {
	        if (sSoundManager == null){
	            sSoundManager = new SoundManager(context);
	        }
	        return sSoundManager;
	   }

	    public SoundManager(Context mContext)
	    {
	        mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
	        sSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
	            public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
	               
	            }
	        });
	        for (int i = 0; i < DrumsetConstant.DRUMSET_AUDIO.length; i++) {
	        	sSoundPoolMap.put(i, sSoundPool.load(mContext, DrumsetConstant.DRUMSET_AUDIO[i], 1));
	        }
	        // testing simultaneous playing
	        // int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
	        // mSoundPool.play(sSoundPoolMap.get(0), streamVolume, streamVolume, 1, 20, 1f); 
	        //mSoundPool.play(sSoundPoolMap.get(1), streamVolume, streamVolume, 1, 2, 1f);
	        // mSoundPool.play(sSoundPoolMap.get(2), streamVolume, streamVolume, 1, 0, 1f);
	    } 

	    public void playSound(int index) { 
	        if (sIsSoundTurnedOff)
	            return;
	         int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
	         sSoundPool.play(sSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f); 
	    }
	    
	    public void turnOff(boolean off) {
	    	sIsSoundTurnedOff = off;
	    }
	}
}
