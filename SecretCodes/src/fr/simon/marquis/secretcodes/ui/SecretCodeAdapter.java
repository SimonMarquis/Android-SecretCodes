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
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
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
import fr.simon.marquis.secretcodes.util.PlatformVersion;
import fr.simon.marquis.secretcodes.util.Utils;

public class SecretCodeAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private ArrayList<SecretCode> mValues;
	private PackageManager pm;
	private Map<SecretCode, Boolean> mCheckedPositions;

	public SecretCodeAdapter(Context ctx, ArrayList<SecretCode> values) {
		this.layoutInflater = LayoutInflater.from(ctx);
		this.mValues = values;
		this.pm = ctx.getPackageManager();
		this.mCheckedPositions = new HashMap<SecretCode, Boolean>();
		Collections.sort(mValues);
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
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.row_code, parent,
					false);
			holder = new ViewHolder();
			holder.code = (TextView) convertView.findViewById(R.id.item_code);
			holder.label = (TextView) convertView.findViewById(R.id.item_label);
			holder.image = (ImageView) convertView
					.findViewById(R.id.item_image);
			holder.selector = (RelativeLayout) convertView
					.findViewById(R.id.item_selector);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SecretCode secretCode = mValues.get(position);
		Boolean checked = mCheckedPositions.get(secretCode);
		holder.code.setText(secretCode.getCode());
		holder.label.setText(secretCode.getLabel());
		holder.selector.setBackgroundResource((checked != null && checked
				.booleanValue()) ? R.drawable.abc_list_pressed_holo_light
				: R.drawable.abc_list_selector_holo_light);
		List<ResolveInfo> liste = pm
				.queryBroadcastReceivers(
						new Intent("android.provider.Telephony.SECRET_CODE",
								Uri.parse("android_secret_code://"
										+ secretCode.getCode())), 0);

		boolean hasImg = liste.size() > 1
				&& liste.get(0).getIconResource() != 0;
		if (PlatformVersion.isAtLeastHoneycomb()) {
			holder.image.setAlpha(hasImg ? 1f : 0.2f);
		} else {
			holder.image.setAlpha(hasImg ? 255 : 50);
		}

		if (hasImg) {
			holder.image.setImageDrawable(liste.get(0).loadIcon(pm));
		} else {
			holder.image.setImageResource(R.drawable.ic_action_halt);
		}

		return convertView;
	}

	class ViewHolder {
		private TextView code;
		private TextView label;
		private ImageView image;
		private RelativeLayout selector;
	}

	public void addItem(SecretCode value) {
		if (!mValues.contains(value)) {
			mValues.add(value);
			notifyDataSetChanged();
		}
	}

	@Override
	public void notifyDataSetChanged() {
		Collections.sort(mValues);
		super.notifyDataSetChanged();
	}

	public ArrayList<SecretCode> getItems() {
		return mValues;
	}

	public void resetItems() {
		mValues.clear();
		mCheckedPositions.clear();
		notifyDataSetChanged();
	}

	public void resetSelection() {
		mCheckedPositions.clear();
		notifyDataSetChanged();
	}

	public void itemCheckedStateChanged(int position, boolean checked) {
		mCheckedPositions.put(mValues.get(position), checked);
		super.notifyDataSetChanged();
	}

	public void deleteSelection(Context ctx) {
		ArrayList<SecretCode> temp = new ArrayList<SecretCode>();
		for (SecretCode secretCode : mValues) {
			if (!mCheckedPositions.containsKey(secretCode)
					|| !mCheckedPositions.get(secretCode).booleanValue()) {
				mCheckedPositions.remove(secretCode);
				temp.add(secretCode);
			}
		}
		Utils.saveSecretCodes(ctx, temp);
		mValues = temp;
		super.notifyDataSetChanged();
	}

	public void setSelection(SparseBooleanArray checkedItemPositions) {
		mCheckedPositions.clear();
		for (int i = 0; i < checkedItemPositions.size(); i++) {
			mCheckedPositions.put(mValues.get(checkedItemPositions.keyAt(i)),
					checkedItemPositions.valueAt(i));
		}
		super.notifyDataSetChanged();
	}
}
