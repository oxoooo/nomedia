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

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

public class BucketActivity extends AppCompatActivity {

    private static final String TAG = "BucketActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket);

        setSupportActionBar(findViewById(R.id.toolbar));

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Image bucket = getIntent().getParcelableExtra("bucket");
        setTitle(bucket.bucketName);

        final File parentFile = bucket.getParentFile();

        final TextView pathView = findViewById(R.id.path);
        pathView.setText(parentFile.getAbsolutePath());

        findViewById(R.id.hide_it).setOnClickListener(v -> hideIt(parentFile));

        final ImagesAdapter imagesAdapter = new ImagesAdapter(
                this, Glide.with(this));

        final RecyclerView imagesView = findViewById(R.id.images);
        imagesView.setLayoutManager(new GridLayoutManager(this, 3));
        imagesView.setAdapter(imagesAdapter);

        imagesAdapter.setImages(MediaStoreUtils.getImages(this, bucket.bucketId, 30));
    }

    private void hideIt(File directory) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_title)
                .setMessage(R.string.confirm_message)
                .setNegativeButton(R.string.confirm_cancel, null)
                .setPositiveButton(R.string.confirm_ok, (dialog, which) -> {
                    MediaStoreUtils.hide(this, directory);
                    finish();
                })
                .show();
    }

}
