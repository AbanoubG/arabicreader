package org.copticchurchlibrary.arabicreader.object;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.ypyproductions.utils.StringUtils;

/**
 * 
 * 
 *
 * 
 */
public class DBImageLoader extends BaseImageDownloader {
	public static final String TAG = DBImageLoader.class.getSimpleName();

	public DBImageLoader(Context context) {
		super(context);
	}

	@Override
	protected InputStream getStreamFromContent(String imageUri, Object extra) throws FileNotFoundException {
		if (!StringUtils.isEmptyString(imageUri)) {
			Uri uri = Uri.parse(imageUri);
			if(uri!=null){
				try {
					MediaMetadataRetriever mmr = new MediaMetadataRetriever();
					mmr.setDataSource(context, uri);
					byte[] rawArt = mmr.getEmbeddedPicture();
					if(rawArt!=null && rawArt.length>0){
						ByteArrayInputStream mInputStream = new ByteArrayInputStream(rawArt);
						mmr.release();
						return mInputStream;
					}
					else{
						mmr.release();
					}
					
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return super.getStreamFromContent(imageUri, extra);

	}

}
