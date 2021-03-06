package org.copticchurchlibrary.arabicreader;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ypyproductions.abtractclass.fragment.DBFragment;
import com.ypyproductions.dialog.utils.AlertDialogUtils;
import com.ypyproductions.dialog.utils.AlertDialogUtils.IOnDialogListener;
import com.ypyproductions.dialog.utils.IDialogFragmentListener;
import org.dialog.materialdialogs.MaterialDialog;
import org.copticchurchlibrary.arabicreader.constants.IMyMusicPlayerConstants;
import org.copticchurchlibrary.arabicreader.playerservice.IMusicConstant;
import org.copticchurchlibrary.arabicreader.playerservice.MusicService;
import com.ypyproductions.task.IDBCallback;
import com.ypyproductions.task.IDBConstantURL;
import com.ypyproductions.utils.ApplicationUtils;
import com.ypyproductions.utils.ResolutionUtils;
import com.ypyproductions.utils.ShareActionUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 *
 * @author:YPY Productions
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: dotrungbao@gmail.com
 * @Website: www.ypyproductions.com
 * @Project:AndroidCloundMusicPlayer
 * @Date:Dec 14, 2014 
 *
 */
public class DBFragmentActivity extends ActionBarActivity implements IDBConstantURL, 
	IDialogFragmentListener, IMyMusicPlayerConstants {
	
	public static final String TAG = DBFragmentActivity.class.getSimpleName();
	private Dialog mProgressDialog;

	private int screenWidth;
	private int screenHeight;
	
	public ArrayList<Fragment> mListFragments;
	
	public Typeface mTypefaceNormal;
	public Typeface mTypefaceLight;
	public Typeface mTypefaceBold;
	public Typeface mTypefaceLogo;
	
	private InterstitialAd mInterstitial;
	private Random mRando;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFormat(PixelFormat.RGBA_8888);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.createProgressDialog();
		
		mTypefaceNormal=Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
		mTypefaceLight=Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
		mTypefaceBold=Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
		mTypefaceLogo=Typeface.createFromAsset(getAssets(), "fonts/Biko_Regular.otf");

		int[] mRes=ResolutionUtils.getDeviceResolution(this);
		if(mRes!=null && mRes.length==2){
			screenWidth=mRes[0];
			screenHeight=mRes[1];
		}
		
		mRando = new Random();
		
	}

	public void setUpBackground(){
		RelativeLayout mRootLayout = (RelativeLayout) findViewById(R.id.layout_root);
		boolean b=USE_BACKGROUND;
		if(b){
			mRootLayout.setBackgroundResource(R.drawable.b1);
		}
		else{
			mRootLayout.setBackgroundColor(Color.TRANSPARENT);
		}
	}
	
	public void showIntertestialAds(final IDBCallback mCallback) {
		boolean b=SHOW_ADVERTISEMENT;
		if(b){
			if(ApplicationUtils.isOnline(this)){
				mInterstitial = new InterstitialAd(getApplicationContext());
				mInterstitial.setAdUnitId(ADMOB_ID_INTERTESTIAL);
				AdRequest adRequest = new AdRequest.Builder().build();
				mInterstitial.loadAd(adRequest);
				mInterstitial.setAdListener(new AdListener() {
					@Override
					public void onAdLoaded() {
						super.onAdLoaded();
						mInterstitial.show();
					}

					@Override
					public void onAdFailedToLoad(int errorCode) {
						super.onAdFailedToLoad(errorCode);
						if(mCallback!=null){
							mCallback.onAction();
						}
					}

					@Override
					public void onAdClosed() {
						super.onAdClosed();
						if(mCallback!=null){
							mCallback.onAction();
						}
					}
				});
				return;
			}
		}
		if(mCallback!=null){
			mCallback.onAction();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showQuitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void showDialogFragment(int idDialog) {
		switch (idDialog) {
		case DIALOG_LOSE_CONNECTION:
			createWarningDialog(R.string.title_warning, R.string.info_lose_internet).show();
			break;
		case DIALOG_EMPTY:
			createWarningDialog(R.string.title_warning, R.string.info_empty).show();
			break;
		case DIALOG_SEVER_ERROR:
			createWarningDialog(R.string.title_warning, R.string.info_server_error).show();
			break;
		default:
			break;
		}
	}

	public MaterialDialog createWarningDialog(int titleId, int messageId) {
		MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(this);
		mBuilder.backgroundColor(getResources().getColor(R.color.white));
		mBuilder.title(titleId);
		mBuilder.titleColor(getResources().getColor(R.color.black_text));
		mBuilder.content(messageId);
		mBuilder.contentColor(getResources().getColor(R.color.black_text));
		mBuilder.positiveColor(getResources().getColor(R.color.main_color));
		mBuilder.negativeColor(getResources().getColor(R.color.black_secondary_text));
		mBuilder.positiveText(R.string.title_ok);
		mBuilder.autoDismiss(true);
		mBuilder.typeface(mTypefaceBold, mTypefaceLight);
		return mBuilder.build();
	}


	public MaterialDialog createFullDialog(int iconId, int mTitleId, int mYesId, int mNoId, int messageId, final IDBCallback mCallback, final IDBCallback mNeCallback) {
		MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(this);
		mBuilder.backgroundColor(getResources().getColor(R.color.white));
		mBuilder.title(mTitleId);
		if (iconId != -1) {
			mBuilder.iconRes(iconId);
		}
		mBuilder.titleColor(getResources().getColor(R.color.black_text));
		mBuilder.content(messageId);
		mBuilder.contentColor(getResources().getColor(R.color.black_text));
		mBuilder.positiveColor(getResources().getColor(R.color.main_color));
		mBuilder.negativeColor(getResources().getColor(R.color.black_secondary_text));
		mBuilder.negativeText(mNoId);
		mBuilder.positiveText(mYesId);
		mBuilder.autoDismiss(true);
		mBuilder.typeface(mTypefaceBold, mTypefaceLight);
		mBuilder.callback(new MaterialDialog.ButtonCallback() {
			@Override
			public void onPositive(MaterialDialog dialog) {
				super.onPositive(dialog);
				if (mCallback != null) {
					mCallback.onAction();
				}
			}

			@Override
			public void onNegative(MaterialDialog dialog) {
				super.onNegative(dialog);
				if (mNeCallback != null) {
					mNeCallback.onAction();
				}
			}
		});
		return mBuilder.build();
	}

	public MaterialDialog createInfoDialog(int iconId, int mTitleId, int mYesId, int messageId, final IDBCallback mCallback) {
		return createInfoDialog(iconId, mTitleId, mYesId, getString(messageId), mCallback);
	}

	public MaterialDialog createInfoDialog(int iconId, int mTitleId, int mYesId, String messageId, final IDBCallback mCallback) {
		MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(this);
		mBuilder.backgroundColor(getResources().getColor(R.color.white));
		mBuilder.title(mTitleId);
		if (iconId != -1) {
			mBuilder.iconRes(iconId);
		}
		mBuilder.titleColor(getResources().getColor(R.color.black_text));
		mBuilder.content(messageId);
		mBuilder.contentColor(getResources().getColor(R.color.black_text));
		mBuilder.positiveColor(getResources().getColor(R.color.main_color));
		mBuilder.positiveText(mYesId);
		mBuilder.autoDismiss(true);
		mBuilder.typeface(mTypefaceBold, mTypefaceLight);
		mBuilder.callback(new MaterialDialog.ButtonCallback() {
			@Override
			public void onPositive(MaterialDialog dialog) {
				super.onPositive(dialog);
				if (mCallback != null) {
					mCallback.onAction();
				}
			}
		});
		return mBuilder.build();
	}
	
	public void showFullDialog(int titleId, int message,int idPositive,int idNegative, final IDBCallback mDBCallback) {
		createFullDialog(-1, titleId, idPositive, idNegative, message, mDBCallback, null).show();
	}

	public void showQuitDialog() {
		final int index = mRando.nextInt(5);
		int mNoId = R.string.title_rate_us;
		if(index%2!=0){
			mNoId=R.string.title_more_apps;
		}
		int mTitleId = R.string.title_confirm;
		int mYesId = R.string.title_yes;
		int iconId = R.drawable.ic_launcher;
		int messageId = R.string.info_close_app;
		
		createFullDialog(iconId, mTitleId, mYesId, mNoId, messageId, new IDBCallback() {
			@Override
			public void onAction() {
				onDestroyData();
				finish();
			}
		}, new IDBCallback() {
			
			@Override
			public void onAction() {
				if(index%2!=0){
					ShareActionUtils.goToUrl(DBFragmentActivity.this, URL_MORE_APPS);
				}
				else{
					String url =String.format(URL_FORMAT_LINK_APP, getPackageName());
					ShareActionUtils.goToUrl(DBFragmentActivity.this, url);
				}
			}
		}).show();
		
	}

	private void createProgressDialog() {
		this.mProgressDialog = new Dialog(this);
		this.mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.mProgressDialog.setContentView(R.layout.item_progress_bar);
		TextView mTvMessage = (TextView) mProgressDialog.findViewById(R.id.tv_message);
		mTvMessage.setTypeface(mTypefaceLight);
		this.mProgressDialog.setCancelable(false);
		this.mProgressDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});
	}

	public void showProgressDialog() {
		if (mProgressDialog != null) {
			TextView mTvMessage = (TextView) mProgressDialog.findViewById(R.id.tv_message);
			mTvMessage.setText(R.string.loading);
			if (!mProgressDialog.isShowing()) {
				mProgressDialog.show();
			}
		}
	}

	public void showProgressDialog(int messageId) {
		if (mProgressDialog != null) {
			TextView mTvMessage = (TextView) mProgressDialog.findViewById(R.id.tv_message);
			mTvMessage.setText(messageId);
			if (!mProgressDialog.isShowing()) {
				mProgressDialog.show();
			}
		}
	}

	public void showProgressDialog(String message) {
		if (mProgressDialog != null) {
			TextView mTvMessage = (TextView) mProgressDialog.findViewById(R.id.tv_message);
			mTvMessage.setText(message);
			if (!mProgressDialog.isShowing()) {
				mProgressDialog.show();
			}
		}
	}
	
	public void dimissProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}
	
	public void showToast(int resId) {
		showToast(getString(resId));
	}

	public void showToast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	public void showToastWithLongTime(int resId) {
		showToastWithLongTime(getString(resId));
	}
	
	public void showToastWithLongTime(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	@Override
	public void doPositiveClick(int idDialog) {
		switch (idDialog) {
		case DIALOG_QUIT_APPLICATION:
			onDestroyData();
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void doNegativeClick(int idDialog) {
		
	}
	
	public void onDestroyData(){
		
	}
	
	public void createArrayFragment(){
		mListFragments=new ArrayList<Fragment>();
	}
	
	public void addFragment(Fragment mFragment){
		if(mFragment!=null && mListFragments!=null){
			synchronized (mListFragments) {
				mListFragments.add(mFragment);
			}
		}
	}
	
	public void showDialogTurnOnInternetConnection(final IDBCallback mCallback) {
		Dialog mDialog = AlertDialogUtils.createFullDialog(this, 0, R.string.title_warning, R.string.title_settings, R.string.title_cancel, R.string.info_lose_internet,
				new IOnDialogListener() {
					@Override
					public void onClickButtonPositive() {
						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
						if(mCallback!=null){
							mCallback.onAction();
						}
					}

					@Override
					public void onClickButtonNegative() {
					}
				});
		mDialog.show();
	}
	
	public boolean backStack(IDBCallback mCallback){
		if(mListFragments!=null && mListFragments.size()>0){
			int count =mListFragments.size();
			if(count>0){
				synchronized (mListFragments) {
					Fragment mFragment = mListFragments.remove(count-1);
					if(mFragment!=null){
						if(mFragment instanceof DBFragment){
							((DBFragment)mFragment).backToHome(this);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	public void deleteSongFromMediaStore(long id) {
		try {
			Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
			getContentResolver().delete(uri, null, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startMusicService(String action){
		Intent mIntent = new Intent(this, MusicService.class);
		mIntent.setAction(getPackageName()+action);
		startService(mIntent);
	}
	public void startMusicService(String action,int value){
		Intent mIntent = new Intent(this, MusicService.class);
		mIntent.setAction(getPackageName()+action);
		mIntent.putExtra(IMusicConstant.KEY_POSITION, value);
		startService(mIntent);
	}
}
