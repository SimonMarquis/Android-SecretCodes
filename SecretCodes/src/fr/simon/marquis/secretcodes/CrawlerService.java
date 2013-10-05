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
package fr.simon.marquis.secretcodes;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import fr.simon.marquis.secretcodes.model.SecretCode;
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

	private FindSecretCodesTask findSecretCodesTask;

	public CrawlerService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("", "onStartCommand");
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
		// TODO: Return the communication channel to the service.
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
			ArrayList<SecretCode> secretCodes = new ArrayList<SecretCode>();
			final int length = 6;
			char characters[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
					'9' };
			long max = 0;
			for (int i = 1; i <= length; i++) {
				max += Math.pow(characters.length, i);
			}
			long cur = 0;
			int currentProgress = 0;
			CrawlerNotification.notify(getApplicationContext(), secretCodes,
					currentProgress);

			long start = System.currentTimeMillis();
			PackageManager pm = getPackageManager();
			Utils.checkBlackList(pm);
			start = System.currentTimeMillis();
			StringBuilder sb = new StringBuilder();
			int[] set = new int[1];
			while (!isCancelled() && set.length <= length) {
				cur++;
				SecretCode code = Utils.findSecretCode(
						generateString(set, characters, sb), pm);
				if (code != null) {
					secretCodes.add(code);
				}

				if (code != null || currentProgress != (int) (100 * cur / max)) {
					currentProgress = (int) (100 * cur / max);
					CrawlerNotification.notify(getApplicationContext(),
							secretCodes, currentProgress);
					if (code != null) {
						publishProgress(code);
					}
				}

				if (set[set.length - 1] != characters.length - 1) {
					set[set.length - 1]++;
				} else {
					for (int i = set.length - 1; i >= 0; i--) {
						if (set[i] == characters.length - 1) {
							if (i == 0) {
								set = new int[set.length + 1];
								break;
							}
							set[i] = 0;
						} else {
							set[i]++;
							break;
						}

					}
				}
			}
			Log.e("", "duration = " + (System.currentTimeMillis() - start)
					+ " nb = " + cur);

			return null;
		}

		private String generateString(int[] set, char[] characters,
				StringBuilder sb) {
			char[] str = new char[set.length];
			for (int i = 0; i < set.length; i++) {
				str[i] = characters[set[i]];
			}
			return new String(str);
		}

		@Override
		protected void onProgressUpdate(SecretCode... values) {
			for (SecretCode value : values) {
				Utils.addSecretCode(getApplicationContext(), value);
				broadcastAdd(value);
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
