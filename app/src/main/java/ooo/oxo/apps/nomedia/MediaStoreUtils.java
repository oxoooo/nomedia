/*
 * .nomedia - Hide annoying directories from your Gallery
 * Copyright (C) 2017 XiNGRZ <chenxingyu92@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ooo.oxo.apps.nomedia;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class MediaStoreUtils {

    static final Uri URI = Media.EXTERNAL_CONTENT_URI;

    private static final String TAG = "MediaStoreUtils";

    private static final String[] PROJECTION = new String[]{
            Media._ID,
            Media.TITLE,
            Media.DATA,
            Media.BUCKET_ID,
            Media.BUCKET_DISPLAY_NAME,
    };

    static List<Image> getBuckets(Context context) {
        final Cursor cursor = Media.query(context.getContentResolver(), URI, PROJECTION,
                "1 = 1) GROUP BY (" + Media.BUCKET_ID,  // WTF
                Media.DATE_ADDED + " DESC");

        final List<Image> images = new ArrayList<>();
        final File root = Environment.getExternalStorageDirectory();

        if (cursor.moveToFirst()) {
            do {
                final Image image = Image.fromCursor(cursor);
                if (image.getParentFile().equals(root)) {
                    continue;
                }
                images.add(image);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return images;
    }

    static List<Image> getImages(Context context, String bucketId, int limit) {
        final Cursor cursor = Media.query(context.getContentResolver(), URI, PROJECTION,
                Media.BUCKET_ID + " = ?", new String[]{bucketId},
                Media.DATE_ADDED + " DESC");

        final List<Image> images = new ArrayList<>();
        int count = 0;

        if (cursor.moveToFirst()) {
            do {
                images.add(Image.fromCursor(cursor));
                count++;
            } while (count < limit && cursor.moveToNext());
        }

        cursor.close();

        return images;
    }

    private static void scanFile(Context context, File file) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(file)));
    }

    private static boolean createNomedia(File nomedia) {
        if (nomedia.exists()) {
            return true;
        }

        try {
            return nomedia.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Failed to create .nomedia", e);
            return false;
        }
    }

    static void hide(Context context, File directory) {
        final File nomedia = new File(directory, ".nomedia");
        if (createNomedia(nomedia)) {
            scanFile(context, nomedia);
        }
    }

}
