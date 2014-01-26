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
package fr.simon.marquis.secretcodes.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import fr.simon.marquis.secretcodes.model.SecretCode;
import fr.simon.marquis.secretcodes.roboto.RobotoTypefaceManager;

public class Utils {

	private static final String KEY_SECRET_CODES = "SECRET_CODES";
	private static Set<String> blackList = new HashSet<String>();

	public static ArrayList<SecretCode> getSecretCodes(Context ctx) {
		ArrayList<SecretCode> res = new ArrayList<SecretCode>();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		try {
			JSONArray array = new JSONArray(prefs.getString(KEY_SECRET_CODES, ""));
			for (int i = 0; i < array.length(); i++) {
				SecretCode code = SecretCode.fromJSON(array.optJSONObject(i));
				if (code != null) {
					res.add(code);
				}
			}
			return res;
		} catch (JSONException e) {
			return res;
		}
	}

	public static void saveSecretCodes(Context ctx, ArrayList<SecretCode> secretCodes) {
		Editor ed = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
		JSONArray array = new JSONArray();
		for (SecretCode code : secretCodes) {
			array.put(code.toJSON());
		}
		ed.putString(KEY_SECRET_CODES, array.toString());
		ed.commit();
	}

	public static SecretCode findSecretCode(String code, PackageManager pm) {
		List<ResolveInfo> liste = pm.queryBroadcastReceivers(
				new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://" + code)), 0);
		for (ResolveInfo r : liste) {
			if (!isBlacklisted(r, pm)) {
				return SecretCode.fromResolveInfo(code, r, pm);
			}
		}
		return null;
	}

	private static boolean isBlacklisted(ResolveInfo r, PackageManager pm) {
		if (blackList == null) {
			blackList = new HashSet<String>();
		}
		return blackList.contains(r.loadLabel(pm));
	}

	public static void print(ResolveInfo r, PackageManager pm) {
		Log.e("", "r.labelRes=[" + r.labelRes + "], r.priority=[" + r.priority + "], r.resolvePackageName=[" + r.resolvePackageName
				+ "], r.getIconResource()=[" + r.getIconResource() + "], r.loadLabel(pm)=[" + r.loadLabel(pm) + "], r.preferredOrder=["
				+ r.preferredOrder + "], r.priority=[" + r.priority + "], r.specificIndex=[" + r.specificIndex + "], r.isDefault=[" + r.isDefault
				+ "], r.activityInfo.icon=[" + (r.activityInfo == null ? null : r.activityInfo.icon) + "], r.activityInfo.logo=["
				+ (r.activityInfo == null ? null : r.activityInfo.logo) + "], r.activityInfo.packageName =["
				+ (r.activityInfo == null ? null : r.activityInfo.packageName) + "], r.serviceInfo.icon=["
				+ (r.serviceInfo == null ? null : r.serviceInfo.icon) + "], r.serviceInfo.logo=["
				+ (r.serviceInfo == null ? null : r.serviceInfo.logo) + "], r.serviceInfo.packageName=["
				+ (r.serviceInfo == null ? null : r.serviceInfo.packageName) + "]");
	}

	public static void checkBlackList(PackageManager pm) {
		List<ResolveInfo> liste = pm.queryBroadcastReceivers(
				new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://0")), 0);
		for (ResolveInfo r : liste) {
			blackList.add(r.loadLabel(pm).toString());
		}
	}

	public static boolean addSecretCode(Context ctx, SecretCode value) {
		HashSet<SecretCode> secretCodes = new HashSet<SecretCode>(getSecretCodes(ctx));
		boolean exist = secretCodes.contains(value);
		if(!exist){
			secretCodes.add(value);
			saveSecretCodes(ctx, new ArrayList<SecretCode>(secretCodes));
		}
		return !exist;
	}

	public static SpannableString applyCustomTypeFace(CharSequence src, Context ctx) {
		SpannableString span = new SpannableString(src);

		span.setSpan(new CustomTypefaceSpan("", RobotoTypefaceManager.obtaintTypeface(ctx, RobotoTypefaceManager.ROBOTOSLAB_REGULAR)), 0,
				span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return span;
	}
}
