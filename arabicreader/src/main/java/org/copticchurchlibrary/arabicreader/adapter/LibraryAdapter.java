package org.copticchurchlibrary.arabicreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ypyproductions.abtractclass.DBBaseAdapter;
import org.dialog.materialdialogs.MaterialDialog;
import org.dialog.materialdialogs.MaterialDialog.ButtonCallback;
import org.dialog.materialdialogs.MaterialDialog.ListCallback;
import org.copticchurchlibrary.arabicreader.constants.IMyMusicPlayerConstants;
import org.copticchurchlibrary.arabicreader.object.TrackObject;
import com.ypyproductions.utils.StringUtils;

import java.util.ArrayList;

/**
 * 
 * 
 *
 * 
 */
public class LibraryAdapter extends DBBaseAdapter implements IMyMusicPlayerConstants {
	public static final String TAG = LibraryAdapter.class.getSimpleName();
	private final boolean isHasBg;

	private Typeface mTypefaceBold;

	private Typeface mTypefaceLight;

	private OnLibraryListener onTrackListener;

	private DisplayImageOptions mImgOptions;

	public LibraryAdapter(Activity mContext, ArrayList<TrackObject> listTrackObjects,
			Typeface mTypefaceBold, Typeface mTypefaceLight,DisplayImageOptions mImgOptions) {
		super(mContext, listTrackObjects);
		this.mTypefaceBold = mTypefaceBold;
		this.mTypefaceLight = mTypefaceLight;
		this.mImgOptions=mImgOptions;
		this.isHasBg=USE_BACKGROUND;
	}

	@Override
	public View getAnimatedView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	@Override
	public View getNormalView(int position, View convertView, ViewGroup parent) {
		final ViewHolder mHolder;
		LayoutInflater mInflater;
		if (convertView == null) {
			mHolder = new ViewHolder();
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(org.copticchurchlibrary.arabicreader.R.layout.item_local_track, null);
			convertView.setTag(mHolder);
			
			mHolder.mImgMenu = (ImageView) convertView.findViewById(org.copticchurchlibrary.arabicreader.R.id.img_menu);
			mHolder.mTvSongName = (TextView) convertView.findViewById(org.copticchurchlibrary.arabicreader.R.id.tv_song);
			mHolder.mTvDuration = (TextView) convertView.findViewById(org.copticchurchlibrary.arabicreader.R.id.tv_duration);
			mHolder.mImgTrack = (ImageView) convertView.findViewById(org.copticchurchlibrary.arabicreader.R.id.img_songs);
			mHolder.mLayoutRoot = (RelativeLayout) convertView.findViewById(org.copticchurchlibrary.arabicreader.R.id.layout_root);
			mHolder.mLayoutRoot.setBackgroundResource(isHasBg? org.copticchurchlibrary.arabicreader.R.drawable.bg_transparent_list_selector: org.copticchurchlibrary.arabicreader.R.drawable.bg_white_list_selector);

			mHolder.mTvSongName.setTypeface(mTypefaceBold);
			mHolder.mTvDuration.setTypeface(mTypefaceLight);
		}
		else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		final TrackObject mTrackObject = (TrackObject) mListObjects.get(position);
		mHolder.mTvSongName.setText(mTrackObject.getTitle());
		if(!StringUtils.isEmptyString(mTrackObject.getPath())){
			Uri mUri = mTrackObject.getURI();
			if (mUri != null) {
				String uri = mUri.toString();
				ImageLoader.getInstance().displayImage(uri, mHolder.mImgTrack, mImgOptions);
			}
			else {
				mHolder.mImgTrack.setImageResource(org.copticchurchlibrary.arabicreader.R.drawable.music_note);
			}
		}
		long duration = mTrackObject.getDuration() / 1000;
		String minute = String.valueOf((int) (duration / 60));
		String seconds = String.valueOf((int) (duration % 60));
		if (minute.length() < 2) {
			minute = "0" + minute;
		}
		if (seconds.length() < 2) {
			seconds = "0" + seconds;
		}
		mHolder.mTvDuration.setText(minute + ":" + seconds);
		mHolder.mLayoutRoot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onTrackListener != null) {
					onTrackListener.onPlayItem(mTrackObject);
				}
			}
		});
		mHolder.mImgMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialogOptions(mTrackObject);
			}
		});

		return convertView;
	}
	
	public void showDialogOptions(final TrackObject mTrackObject) {
		MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(mContext);
		mBuilder.backgroundColor(mContext.getResources().getColor(org.copticchurchlibrary.arabicreader.R.color.white));
		mBuilder.title(org.copticchurchlibrary.arabicreader.R.string.title_options);
		mBuilder.titleColor(mContext.getResources().getColor(org.copticchurchlibrary.arabicreader.R.color.black_text));
		mBuilder.items(org.copticchurchlibrary.arabicreader.R.array.list_options);
		mBuilder.contentColor(mContext.getResources().getColor(org.copticchurchlibrary.arabicreader.R.color.black_text));
		mBuilder.positiveColor(mContext.getResources().getColor(org.copticchurchlibrary.arabicreader.R.color.main_color));
		mBuilder.negativeColor(mContext.getResources().getColor(org.copticchurchlibrary.arabicreader.R.color.black_secondary_text));
		mBuilder.positiveText(org.copticchurchlibrary.arabicreader.R.string.title_cancel);
		mBuilder.autoDismiss(true);
		mBuilder.typeface(mTypefaceBold, mTypefaceLight);
		mBuilder.itemsCallback(new ListCallback() {
			
			@Override
			public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
				if (which == 0) {
					if (onTrackListener != null) {
						onTrackListener.onAddToPlaylist(mTrackObject);
					}
				}
				else if (which == 1) {
					if (onTrackListener != null) {
						onTrackListener.onDeleteItem(mTrackObject);
					}
				}
				else if (which == 2) {
					if (onTrackListener != null) {
						onTrackListener.onSetAsRingtone(mTrackObject);
					}
				}
				else if (which == 3) {
					if (onTrackListener != null) {
						onTrackListener.onSetAsNotification(mTrackObject);
					}
				}
			}
		});
		mBuilder.callback(new ButtonCallback() {
			@Override
			public void onNegative(MaterialDialog dialog) {
				super.onNegative(dialog);
			}
		});
		mBuilder.build().show();
	}



	public void setOnLibraryListener(OnLibraryListener onDownloadedListener) {
		this.onTrackListener = onDownloadedListener;
	}

	public interface OnLibraryListener {
		public void onAddToPlaylist(TrackObject mTrackObject);
		public void onDeleteItem(TrackObject mTrackObject);
		public void onPlayItem(TrackObject mTrackObject);
		public void onSetAsRingtone(TrackObject mTrackObject);
		public void onSetAsNotification(TrackObject mTrackObject);
	}

	private static class ViewHolder {
		public ImageView mImgMenu;
		public ImageView mImgTrack;
		public TextView mTvSongName;
		public TextView mTvDuration;
		public RelativeLayout mLayoutRoot;
	}


}
