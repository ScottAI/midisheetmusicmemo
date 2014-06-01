/*
 * Copyright (c) 2011-2012 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */

package com.midisheetmusicmemo;

import java.util.*;

import com.midisheetmusicmemo.R;

import android.widget.*;
import android.util.Log;
import android.view.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;

/** @class IconArrayAdapter
 *  The ListAdapter for displaying the list of songs,
 *  and for displaying the list of files in a directory.
 *
 *  Similar to the array adapter, but adds an icon
 *  to the left side of each item displayed.
 *  Midi files show a NotePair icon.
 */
class IconArrayAdapter<T> extends ArrayAdapter<T> {
    private LayoutInflater inflater;
    private static Bitmap midiIcon;       /* The midi icon */
    private static Bitmap directoryIcon;  /* The directory icon */
    private SharedPreferences prefs_play_count, prefs_quizcount;
    private SharedPreferences prefs_last_played, prefs_colorscheme;

    /** Load the NotePair image into memory. */
    public void LoadImages(Context context) {
        if (midiIcon == null) {
            Resources res = context.getResources();
            midiIcon = BitmapFactory.decodeResource(res, R.drawable.notepair);
            directoryIcon = BitmapFactory.decodeResource(res, R.drawable.directoryicon);
        }
    }

    /** Create a new IconArrayAdapter. Load the NotePair image */
    public IconArrayAdapter(Context context, int resourceId, List<T> objects) {
        super(context, resourceId, objects);
        LoadImages(context);
        inflater = LayoutInflater.from(context); 
        prefs_play_count = context.getSharedPreferences("playcounts", Context.MODE_PRIVATE);
        prefs_last_played= context.getSharedPreferences("lastplayed", Context.MODE_PRIVATE);
        prefs_colorscheme = context.getSharedPreferences("colorscheme", Context.MODE_PRIVATE);
        prefs_quizcount  = context.getSharedPreferences("quizcounts", Context.MODE_PRIVATE);
    }

    /** Create a view for displaying a song in the ListView.
     *  The view consists of a Note Pair icon on the left-side,
     *  and the name of the song.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.choose_song_item, null);
         }
         TextView text = (TextView)convertView.findViewById(R.id.choose_song_name);
         ImageView image = (ImageView)convertView.findViewById(R.id.choose_song_icon);
         text.setHighlightColor(Color.WHITE);
         TextView tommy_text = (TextView)convertView.findViewById(R.id.choose_song_stats);
         TextView color_indicator = (TextView)convertView.findViewById(R.id.choose_song_color);
         
         FileUri file = (FileUri) this.getItem(position);
         if (file.isDirectory()) {
             image.setImageBitmap(directoryIcon);
             text.setText(file.getUri().getPath());
             tommy_text.setText("");
         }
         else {
             image.setImageBitmap(midiIcon);
             text.setText(file.toString());

             int play_count = prefs_play_count.getInt(file.toStringFull(), 0);
             int quiz_count = prefs_quizcount.getInt(file.toStringFull(), 0);
             String sz_played = play_count + " plays, " + quiz_count + " quizzees";
             String sz_lastplay;
             long date_millis = prefs_last_played.getLong(file.toString(), -1);
             if(date_millis == -1) {
            	 sz_lastplay = "";
             } else {
            	 Date d = new Date(date_millis);
            	 sz_lastplay = "\nLast played " + d.toLocaleString();
             }
             tommy_text.setText(sz_played + sz_lastplay);
             
             // Color scheme
             int cidx = prefs_colorscheme.getInt(file.toStringFull(), -1);
             if(cidx < 0 || cidx >= TommyConfig.styles.length) {
            	 color_indicator.setBackgroundColor(0xFF000000);
             } else {
            	 int bkcolor = TommyConfig.getStyleByIdx(cidx).background_color;
            	 color_indicator.setBackgroundColor(bkcolor);
             }
         }
         return convertView;
    }
}
