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

import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements BucketsAdapter.OnBucketClickListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_WRITE = 1;

    private BucketsAdapter bucketsAdapter;

    private boolean granted;

    private final ContentObserver observer = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            runOnUiThread(MainActivity.this::reload);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        bucketsAdapter = new BucketsAdapter(
                this, Glide.with(this), this);

        final RecyclerView bucketsView = findViewById(R.id.buckets);
        bucketsView.setLayoutManager(new GridLayoutManager(this, 2));
        bucketsView.setAdapter(bucketsAdapter);

        if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            granted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(MediaStoreUtils.URI,
                true, observer);
        reload();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(observer);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE:
                if (permissions.length == 1 && grantResults.length == 1 &&
                        permissions[0].equals(WRITE_EXTERNAL_STORAGE) &&
                        grantResults[0] == PERMISSION_GRANTED) {
                    granted = true;
                    reload();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void reload() {
        if (!granted) {
            return;
        }

        bucketsAdapter.setBuckets(MediaStoreUtils.getBuckets(this));
    }

    @Override
    public void onBucketClick(Image bucket) {
        startActivity(new Intent(this, BucketActivity.class)
                .putExtra("bucket", bucket));
    }

}
