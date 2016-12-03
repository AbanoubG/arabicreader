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
 */
public class DetailPlaylistAdapter extends DBBaseAdapter implements IMyMusicPlayerConstants {
	public static final String TAG = DetailPlaylistAdapter.class.getSimpleName();
	private final boolean isHasBg;

	private Typeface mTypefaceBold;

	private DisplayImageOptions mImgOptions;
	private IDetailPlaylistAdapterListener trackAdapter;

	private Typeface mTypefaceLight;

	public DetailPlaylistAdapter(Activity mContext, ArrayList<TrackObject> listDrawerObjects,
			Typeface mTypefaceBold, Typeface mTypefaceLight, DisplayImageOptions mImgOptions) {
		super(mContext, listDrawerObjects);
		this.mTypefaceBold = mTypefaceBold;
		this.mTypefaceLight = mTypefaceLight;

		this.mImgOptions = mImgOptions;
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
			convertView = mInflater.inflate(org.copticchurchlibrary.arabicreader.R.layout.item_detail_playlist, null);
			convertView.setTag(mHolder);

			mHolder.mImgSongs = (ImageView) convertView.findViewById(org.copticchurchlibrary.arabicreader.R.id.img_songs);
			mHolder.mImgMenu = (ImageView) convertView.findViewById(org.copticchurchlibrary.arabicreader.R.id.img_menu);
			
			mHolder.mTvSongName = (TextView) convertView.findViewById(org.copticchurchlibrary.arabicreader.R.id.tv_song);
			mHolder.mTvSinger = (TextView) convertView.findViewById(org.copticchurchlibrary.arabicreader.R.id.tv_singer);
			mHolder.mRootLayout = (RelativeLayout) convertView.findViewById(org.copticchurchlibrary.arabicreader.R.id.layout_root);
			mHolder.mRootLayout.setBackgroundResource(isHasBg? org.copticchurchlibrary.arabicreader.R.drawable.bg_transparent_list_selector: org.copticchurchlibrary.arabicreader.R.drawable.bg_white_list_selector);

			mHolder.mTvSongName.setTypeface(mTypefaceBold);
			mHolder.mTvSinger.setTypeface(mTypefaceLight);
		}
		else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		final TrackObject mTrackObject = (TrackObject) mListObjects.get(position);
		mHolder.mTvSongName.setText(mTrackObject.getTitle());

		mHolder.mTvSinger.setText(mTrackObject.getUsername());

		if(!StringUtils.isEmptyString(mTrackObject.getPath())){
			Uri mUri = mTrackObject.getURI();
			if (mUri != null) {
				String uri = mUri.toString();
				ImageLoader.getInstance().displayImage(uri, mHolder.mImgSongs, mImgOptions);
			}
			else {
				mHolder.mImgSongs.setImageResource(org.copticchurchlibrary.arabicreader.R.drawable.music_note);
			}
		}
		
		mHolder.mRootLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (trackAdapter != null) {
					trackAdapter.onPlayingTrack(mTrackObject);
				}
			}
		});
		mHolder.mImgMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDiaglogOptions(mTrackObject);
			}
		});
		return convertView;
	}
	
	public void showDiaglogOptions(final TrackObject mTrackObject) {
		MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(mContext);
		mBuilder.backgroundColor(mContext.getResources().getColor(org.copticchurchlibrary.arabicreader.R.color.white));
		mBuilder.title(org.copticchurchlibrary.arabicreader.R.string.title_options);
		mBuilder.titleColor(mContext.getResources().getColor(org.copticchurchlibrary.arabicreader.R.color.black_text));
		mBuilder.items(org.copticchurchlibrary.arabicreader.R.array.list_track_playlist);
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
					if(trackAdapter!=null){
						trackAdapter.onRemoveToPlaylist(mTrackObject);
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


	public interface IDetailPlaylistAdapterListener {
		public void onRemoveToPlaylist(TrackObject mTrackObject);
		public void onPlayingTrack(TrackObject mTrackObject);
	}

	public void setDetailPlaylistAdapterListener(IDetailPlaylistAdapterListener trackAdapter) {
		this.trackAdapter = trackAdapter;
	}

	private static class ViewHolder {
		public RelativeLayout mRootLayout;
		public ImageView mImgSongs;
		public ImageView mImgMenu;
		public TextView mTvSongName;
		public TextView mTvSinger;
	}

}
