package com.polymtl.jiajing.tp2_localisationmap.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.polymtl.jiajing.tp2_localisationmap.R;
import com.polymtl.jiajing.tp2_localisationmap.model.Tp2Marker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.security.auth.callback.Callback;

/**
 * Created by Zoe on 15-02-27.
 */
public class Tp2InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    private ContentResolver cr;

    public Tp2InfoWindowAdapter (Context context) {

        this.context = context;

    }

    @Override
    public View getInfoWindow (com.google.android.gms.maps.model.Marker marker){
        return null;
    }

    @Override
    public View getInfoContents (com.google.android.gms.maps.model.Marker marker){

       /* String[] projection = {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA};
        Cursor cursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);*/

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = inflater.inflate(R.layout.marker, null);

        ImageView imageView = (ImageView) v.findViewById(R.id.picture);
        imageView.setImageResource(R.drawable.picture);

        /*TextView title = (TextView) v.findViewById(R.id.title);

        title.setText(marker.getTitle());*/

        TextView info = (TextView) v.findViewById(R.id.info);

        info.setText(marker.getSnippet()); //Show maker information

        //TODO load image
        //load the image thumbnail
       /* if (marker.getTitle().length() > 0) {
            Bitmap bitmap = getBitmap(cr, marker.getTitle());
            imageView.setImageBitmap(bitmap);
        }*/

        return v;
    }

    public static Bitmap getBitmap(ContentResolver cr, String fileName) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //select condition.
        String whereClause = MediaStore.Images.Media.DATA + " = '" + fileName + "'";

        //colection of results.
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new String[] { MediaStore.Images.Media._ID }, whereClause,null, null);
        if (cursor == null || cursor.getCount() == 0) {
            if(cursor != null)
                cursor.close();
            return null;
        }
        cursor.moveToFirst();
        //image id in image table.
        String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        cursor.close();
        if (videoId == null) {
            return null;
        }
        long videoIdLong = Long.parseLong(videoId);
        //via imageid get the bimap type thumbnail in thumbnail table.
        bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, videoIdLong, MediaStore.Images.Thumbnails.MINI_KIND, options);
        return bitmap;
    }




//    public static String timeFromUTCSecs(Context ctx, long secs) {
//        return DateUtils.formatDateTime(ctx, secs * 1000,
//                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
//                        | DateUtils.FORMAT_NUMERIC_DATE);
//    }

}

