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
package fr.simon.marquis.secretcodes.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import fr.simon.marquis.secretcodes.model.SecretCode;
import fr.simon.marquis.secretcodes.ui.CrawlerNotification;
import fr.simon.marquis.secretcodes.util.Utils;

public class CrawlerService extends Service {

	public static boolean isCrawling = false;
	public static final String CANCEL_ACTION = "CANCEL_ACTION";
	public static final String BROADCAST_INTENT = "fr.simon.marquis.secretcodes";
	public static final String SECRETCODE_KEY = "SECRETCODE_KEY";
	public static final String ACTION = "ACTION";
	public static final int ACTION_START = 1;
	public static final int ACTION_ADD = 2;
	public static final int ACTION_END = 3;
	public static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
	public static final String ANDROID_MANIFEST = "AndroidManifest.xml";
	public static final String ATTR_VAL_LABEL = "label";
	public static final String ATTR_VAL_ICON = "icon";
	public static final String ATTR_VAL_HOST = "host";
	public static final String ATTR_VAL_SCHEME = "scheme";
	public static final String ATTR_VAL_ANDROID_SECRET_CODE = "android_secret_code";
	public static final String TAG_APPLICATION = "application";
	public static final String TAG_ACTIVITY = "activity";
	public static final String TAG_INTENT_FILTER = "intent-filter";
	public static final String TAG_DATA = "data";

	private FindSecretCodesTask findSecretCodesTask;

	public CrawlerService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			return Service.START_STICKY;
		}
		if (intent.getBooleanExtra(CANCEL_ACTION, false)) {
			stopSelf();
		} else if (!isCrawling) {
			findSecretCodesTask = new FindSecretCodesTask();
			findSecretCodesTask.execute();
		}
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onDestroy() {
		cancelCrawlTask();
		CrawlerNotification.cancel(getApplicationContext());
		isCrawling = false;
		broadcastEnd();
		super.onDestroy();
	}

	private void cancelCrawlTask() {
		if (findSecretCodesTask != null && !findSecretCodesTask.isCancelled()) {
			findSecretCodesTask.cancel(true);
			findSecretCodesTask = null;
		}
	}

	public class FindSecretCodesTask extends AsyncTask<Void, SecretCode, Void> {

		@Override
		protected void onPreExecute() {
			isCrawling = true;
			broadcastStart();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			PackageManager pm = getPackageManager();
			ArrayList<SecretCode> secretCodes = new ArrayList<SecretCode>();
			List<android.content.pm.PackageInfo> pil = pm.getInstalledPackages(PackageManager.GET_DISABLED_COMPONENTS);
			long max = pil == null ? 0 : pil.size();
			long cur = 0;
			int currentProgress = 0;
			CrawlerNotification.notify(getApplicationContext(), secretCodes, currentProgress);

			for (PackageInfo p : pil) {
				if (isCancelled()) {
					return null;
				}
				cur++;
				if (currentProgress != (int) (100 * cur / max)) {
					currentProgress = (int) (100 * cur / max);
					CrawlerNotification.notify(getApplicationContext(), secretCodes, currentProgress);
				}
				try {
					XmlResourceParser xrp = createPackageContext(p.packageName, 0).getAssets().openXmlResourceParser(ANDROID_MANIFEST);

					int applicationLabel = 0;
					int activityLabel = 0;
					int intentFilterLabel = 0;

					int applicationIcon = 0;
					int activityIcon = 0;
					int intentFilterIcon = 0;

					while (xrp.next() != XmlPullParser.END_DOCUMENT) {
						if (isCancelled()) {
							return null;
						}

						if (xrp.getEventType() == XmlPullParser.START_TAG) {
							if (TAG_APPLICATION.equals(xrp.getName())) {
								applicationLabel = xrp.getAttributeResourceValue(NAMESPACE, ATTR_VAL_LABEL, 0);
								applicationIcon = xrp.getAttributeResourceValue(NAMESPACE, ATTR_VAL_ICON, 0);
							}
							if (TAG_ACTIVITY.equals(xrp.getName())) {
								activityLabel = xrp.getAttributeResourceValue(NAMESPACE, ATTR_VAL_LABEL, 0);
								activityIcon = xrp.getAttributeResourceValue(NAMESPACE, ATTR_VAL_ICON, 0);
							}
							if (TAG_INTENT_FILTER.equals(xrp.getName())) {
								intentFilterLabel = xrp.getAttributeResourceValue(NAMESPACE, ATTR_VAL_LABEL, 0);
								intentFilterIcon = xrp.getAttributeResourceValue(NAMESPACE, ATTR_VAL_ICON, 0);
							}
							if (TAG_DATA.equals(xrp.getName())
									&& ATTR_VAL_ANDROID_SECRET_CODE.equals(xrp.getAttributeValue(NAMESPACE, ATTR_VAL_SCHEME))) {
								String c = xrp.getAttributeValue(NAMESPACE, ATTR_VAL_HOST);
								if (!TextUtils.isEmpty(c)/*
														 * &&
														 * TextUtils.isDigitsOnly
														 * (c)
														 */) {
									SecretCode code = new SecretCode(c, getBestString(p, pm, applicationLabel, activityLabel, intentFilterLabel),
											p.packageName, getBestIcon(p, pm, applicationIcon, activityIcon, intentFilterIcon));
									secretCodes.add(code);
									CrawlerNotification.notify(getApplicationContext(), secretCodes, currentProgress);
									publishProgress(code);
								}
							}
						}
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		private int getBestIcon(PackageInfo p, PackageManager pm, int applicationIcon, int activityIcon, int intentFilterIcon) {
			return intentFilterIcon == 0 ? (activityIcon == 0 ? applicationIcon : activityIcon) : intentFilterIcon;
		}

		private String getBestString(PackageInfo p, PackageManager pm, int applicationLabel, int activityLabel, int intentFilterLabel) {
			return String.valueOf(pm.getText(p.packageName, intentFilterLabel == 0 ? (activityLabel == 0 ? applicationLabel : activityLabel)
					: intentFilterLabel, p.applicationInfo));
		}

		@Override
		protected void onProgressUpdate(SecretCode... values) {
			for (SecretCode value : values) {
				if (Utils.addSecretCode(getApplicationContext(), value)) {
					broadcastAdd(value);
				}
			}
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			broadcastEnd();
			CrawlerNotification.cancel(getApplicationContext());
			stopSelf();
			super.onPostExecute(result);
		}
	}

	private void broadcastStart() {
		Intent intent = new Intent(BROADCAST_INTENT);
		intent.putExtra(ACTION, ACTION_START);
		sendBroadcast(intent);
	}

	private void broadcastAdd(SecretCode value) {
		Intent intent = new Intent(BROADCAST_INTENT);
		intent.putExtra(ACTION, ACTION_ADD);
		intent.putExtra(SECRETCODE_KEY, value.toJSON().toString());
		sendBroadcast(intent);
	}

	private void broadcastEnd() {
		Intent intent = new Intent(BROADCAST_INTENT);
		intent.putExtra(ACTION, ACTION_END);
		sendBroadcast(intent);
	}
}
