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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import java.util.List;

class BucketsAdapter extends RecyclerView.Adapter<BucketsAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final RequestManager glide;
    private final OnBucketClickListener listener;

    private List<Image> buckets;

    BucketsAdapter(Context context, RequestManager glide, OnBucketClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.glide = glide;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_bucket, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Image bucket = buckets.get(position);
        holder.nameView.setText(bucket.bucketName);
        glide.load(bucket.getFile()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return buckets == null ? 0 : buckets.size();
    }

    void setBuckets(List<Image> buckets) {
        this.buckets = buckets;
        notifyDataSetChanged();
    }

    private void onItemClick(int position) {
        listener.onBucketClick(buckets.get(position));
    }

    interface OnBucketClickListener {

        void onBucketClick(Image bucket);

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;
        final TextView nameView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            nameView = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(v -> onItemClick(getLayoutPosition()));
        }

    }

}
