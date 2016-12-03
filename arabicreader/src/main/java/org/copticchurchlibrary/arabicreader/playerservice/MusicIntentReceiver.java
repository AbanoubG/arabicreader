package org.copticchurchlibrary.arabicreader.playerservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import org.copticchurchlibrary.arabicreader.dataMng.SoundCloundDataMng;
import org.copticchurchlibrary.arabicreader.object.TrackObject;
import org.copticchurchlibrary.arabicreader.setting.SettingManager;
import com.ypyproductions.utils.StringUtils;

import java.util.ArrayList;

public class MusicIntentReceiver extends BroadcastReceiver implements IMusicConstant {

	public static final String TAG = MusicIntentReceiver.class.getSimpleName();

	private ArrayList<TrackObject> mListTrack;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null) {
			return;
		}
		String action = intent.getAction();
		if (StringUtils.isEmptyString(action)) {
			return;
		}
		mListTrack = SoundCloundDataMng.getInstance().getListPlayingTrackObjects();
		String packageName = context.getPackageName();
		if (action.equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
			startService(context,ACTION_PAUSE);
		}
		else if (action.equals(packageName + ACTION_NEXT)) {
			startService(context,ACTION_NEXT);
		}
		else if (action.equals(packageName + ACTION_TOGGLE_PLAYBACK)) {
			startService(context,ACTION_TOGGLE_PLAYBACK);
		}
		else if (action.equals(packageName + ACTION_PREVIOUS)) {
			startService(context,ACTION_PREVIOUS);
		}
		else if (action.equals(packageName + ACTION_STOP)) {
			startService(context,ACTION_STOP);
		}
		else if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {
			if (mListTrack == null || mListTrack.size() == 0) {
				SoundCloundDataMng.getInstance().onDestroy();
				startService(context,ACTION_STOP);
				return;
			}
			KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
			if (keyEvent.getAction() != KeyEvent.ACTION_DOWN) {
				return;
			}
			switch (keyEvent.getKeyCode()) {
			case KeyEvent.KEYCODE_HEADSETHOOK:
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				if (SettingManager.getOnline(context)) {
					startService(context,ACTION_TOGGLE_PLAYBACK);
				}
				else {
					startService(context,ACTION_STOP);
				}
				break;
			case KeyEvent.KEYCODE_MEDIA_PLAY:
				startService(context,ACTION_PLAY);
				break;
			case KeyEvent.KEYCODE_MEDIA_PAUSE:
				if (SettingManager.getOnline(context)) {
					startService(context,ACTION_PAUSE);
				}
				else {
					startService(context,ACTION_STOP);
				}
				break;
			case KeyEvent.KEYCODE_MEDIA_STOP:
				startService(context,ACTION_STOP);
				break;
			case KeyEvent.KEYCODE_MEDIA_NEXT:
				startService(context,ACTION_NEXT);
				break;
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
				startService(context,ACTION_PREVIOUS);
				break;
			}
		}
	}

	private void startService(Context mContext,String action){
		Intent mIntent = new Intent(mContext,MusicService.class);
		mIntent.setAction(mContext.getPackageName()+action);
		mContext.startService(mIntent);
	}
}
