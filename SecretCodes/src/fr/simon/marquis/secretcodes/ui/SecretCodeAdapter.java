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
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.simon.marquis.secretcodes.R;
import fr.simon.marquis.secretcodes.model.SecretCode;

public class SecretCodeAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private ArrayList<SecretCode> mValues;
	private PackageManager pm;

	public SecretCodeAdapter(Context ctx, ArrayList<SecretCode> values) {
		this.layoutInflater = LayoutInflater.from(ctx);
		this.mValues = values;
		this.pm = ctx.getPackageManager();
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.row_code, parent,
					false);
			holder = new ViewHolder();
			holder.code = (TextView) convertView.findViewById(R.id.item_code);
			holder.label = (TextView) convertView.findViewById(R.id.item_label);
			holder.image = (ImageView) convertView.findViewById(R.id.item_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SecretCode secretCode = mValues.get(position);
		holder.code.setText(secretCode.getCode());
		holder.label.setText(secretCode.getLabel());
//		if(secretCode.getDrawable() == null){
//			holder.image.setImageResource(secretCode.getResource());
//		} else {
//			holder.image.setImageDrawable(secretCode.getDrawable());
//		}
		List<ResolveInfo> liste = pm.queryBroadcastReceivers(
				new Intent("android.provider.Telephony.SECRET_CODE", Uri
						.parse("android_secret_code://" + secretCode.getCode())), 0);

		if (liste.size() > 1 && liste.get(0).getIconResource() != 0) {
			holder.image.setImageDrawable(liste.get(0).loadIcon(pm));
		} else {
			holder.image.setImageResource(R.drawable.ic_launcher);
		}

		return convertView;
	}

	class ViewHolder {
		private TextView code;
		private TextView label;
		private ImageView image;
	}

	public boolean addItem(SecretCode value) {
		for (SecretCode v : mValues) {
			if (v.getCode().equals(value.getCode())) {
				return false;
			}
		}
		mValues.add(value);
		super.notifyDataSetChanged();
		return true;
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
		mValues = new ArrayList<SecretCode>();
		super.notifyDataSetChanged();
	}
}
