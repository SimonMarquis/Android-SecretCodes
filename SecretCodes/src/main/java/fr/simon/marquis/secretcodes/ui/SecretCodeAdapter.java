/*
 * Copyright (C) 2013 Simon Marquis (http://www.simon-marquis.fr)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package fr.simon.marquis.secretcodes.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.simon.marquis.secretcodes.R;
import fr.simon.marquis.secretcodes.model.SecretCode;
import fr.simon.marquis.secretcodes.service.CrawlerService;
import fr.simon.marquis.secretcodes.util.PlatformVersion;
import fr.simon.marquis.secretcodes.util.Utils;

public class SecretCodeAdapter extends BaseAdapter {

    private final Object mLock = new Object();
    private LayoutInflater layoutInflater;
    private ArrayList<SecretCode> mValues;
    private PackageManager pm;
    private Map<SecretCode, Boolean> mCheckedPositions;
    private int[] mBackgrounds = {R.drawable.card_blueborder, R.drawable.card_goldborder, R.drawable.card_greenborder, R.drawable.card_navyborder, R.drawable.card_purpleborder, R.drawable.card_redborder, R.drawable.card_tealborder, R.drawable.card_yellowborder};

    public SecretCodeAdapter(Context ctx, ArrayList<SecretCode> values) {
        this.layoutInflater = LayoutInflater.from(ctx);
        this.pm = ctx.getPackageManager();
        this.mCheckedPositions = new HashMap<SecretCode, Boolean>();
        synchronized (mLock) {
            this.mValues = values;
            Collections.sort(mValues);
        }
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) mValues.get(position).hashCode();
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_code, parent, false);
            holder = new ViewHolder();
            holder.code = (TextView) convertView.findViewById(R.id.item_code);
            holder.label = (TextView) convertView.findViewById(R.id.item_label);
            holder.image = (ImageView) convertView.findViewById(R.id.item_image);
            holder.selector = (RelativeLayout) convertView.findViewById(R.id.item_selector);
            holder.background = convertView.findViewById(R.id.item_background);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SecretCode secretCode = mValues.get(position);
        holder.background.setBackgroundResource(mBackgrounds[Math.abs(secretCode.getLabel().hashCode() % 8)]);
        Boolean checked = mCheckedPositions.get(secretCode);
        holder.code.setText(secretCode.getCode());
        holder.label.setText(secretCode.getLabel());
        holder.selector.setBackgroundResource(checked != null && checked ? R.drawable.abc_list_pressed_holo_light : R.drawable.abc_list_selector_holo_light);

        boolean hasImg = true;
        if (secretCode.getDrawable() != null) {
            holder.image.setImageDrawable(secretCode.getDrawable());
        } else {
            if (secretCode.getDrawableResource() == 0) {
                holder.image.setImageResource(R.drawable.ic_action_halt);
                hasImg = false;
            } else {
                secretCode.setDrawable(pm.getDrawable(secretCode.getPackageManager(), secretCode.getDrawableResource(), null));
                holder.image.setImageDrawable(secretCode.getDrawable());
            }
        }

        if (PlatformVersion.isAtLeastHoneycomb()) {
            holder.image.setAlpha(hasImg ? 1f : 0.2f);
        } else {
            holder.image.setAlpha(hasImg ? 255 : 50);
        }

        return convertView;
    }

    public void addItem(SecretCode value, CrawlerService.Action action) {
        synchronized (mLock) {
            if (action == CrawlerService.Action.UPDATE) {
                mValues.remove(value);
            }
            mValues.add(value);
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        synchronized (mLock) {
            Collections.sort(mValues);
        }
        super.notifyDataSetChanged();
    }

    public void resetSelection() {
        synchronized (mLock) {
            mCheckedPositions.clear();
        }
        notifyDataSetChanged();
    }

    public void itemCheckedStateChanged(int position, boolean checked) {
        synchronized (mLock) {
            mCheckedPositions.put(mValues.get(position), checked);
        }
        super.notifyDataSetChanged();
    }

    public void deleteSelection(Context ctx) {
        synchronized (mLock) {
            ArrayList<SecretCode> temp = new ArrayList<SecretCode>();
            for (SecretCode secretCode : mValues) {
                if (!mCheckedPositions.containsKey(secretCode) || !mCheckedPositions.get(secretCode)) {
                    mCheckedPositions.remove(secretCode);
                    temp.add(secretCode);
                }
            }
            Utils.saveSecretCodes(ctx, temp);
            mValues = temp;
        }
        super.notifyDataSetChanged();
    }

    public void setSelection(SparseBooleanArray checkedItemPositions) {
        synchronized (mLock) {
            mCheckedPositions.clear();
            for (int i = 0; i < checkedItemPositions.size(); i++) {
                mCheckedPositions.put(mValues.get(checkedItemPositions.keyAt(i)), checkedItemPositions.valueAt(i));
            }
        }
        super.notifyDataSetChanged();
    }

    class ViewHolder {
        private TextView code;
        private TextView label;
        private ImageView image;
        private RelativeLayout selector;
        private View background;
    }
}
