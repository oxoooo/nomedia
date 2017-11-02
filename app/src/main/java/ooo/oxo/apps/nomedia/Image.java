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

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore.Images.Media;

import java.io.File;

class Image implements Parcelable {

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    final long id;
    final String name;
    final String path;
    final String bucketId;
    final String bucketName;

    private Image(long id, String name, String path, String bucketId, String bucketName) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.bucketId = bucketId;
        this.bucketName = bucketName;
    }

    private Image(Parcel in) {
        id = in.readLong();
        name = in.readString();
        path = in.readString();
        bucketId = in.readString();
        bucketName = in.readString();
    }

    static Image fromCursor(Cursor cursor) {
        final int columnId = cursor.getColumnIndex(Media._ID);
        final int columnTitle = cursor.getColumnIndex(Media.TITLE);
        final int columnData = cursor.getColumnIndex(Media.DATA);
        final int columnBucketId = cursor.getColumnIndex(Media.BUCKET_ID);
        final int columnBucketDisplayName = cursor.getColumnIndex(Media.BUCKET_DISPLAY_NAME);
        return new Image(
                cursor.getLong(columnId),
                cursor.getString(columnTitle),
                cursor.getString(columnData),
                cursor.getString(columnBucketId),
                cursor.getString(columnBucketDisplayName));
    }

    File getFile() {
        return new File(path);
    }

    File getParentFile() {
        return getFile().getParentFile();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(bucketId);
        dest.writeString(bucketName);
    }

}
