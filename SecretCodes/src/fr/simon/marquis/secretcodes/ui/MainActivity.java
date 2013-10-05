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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import fr.simon.marquis.secretcodes.R;
import fr.simon.marquis.secretcodes.model.SecretCode;
import fr.simon.marquis.secretcodes.util.Utils;

public class MainActivity extends ActionBarActivity {

	private GridView mGridView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGridView = (GridView) findViewById(R.id.gridView);
		mGridView.setAdapter(new SecretCodeAdapter(this, Utils
				.getSecretCodes(this)));
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				sendBroadcast(new Intent(
						"android.provider.Telephony.SECRET_CODE", Uri
								.parse("android_secret_code://"
										+ ((SecretCode) arg0
												.getItemAtPosition(arg2))
												.getCode())));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_reload:
			new FindSecretCodesTask().execute();
			break;
		case R.id.action_reset:
			((SecretCodeAdapter) mGridView.getAdapter()).resetItems();
			Utils.saveSecretCodes(this, new ArrayList<SecretCode>());
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public class FindSecretCodesTask extends AsyncTask<Void, SecretCode, Void> {
		SecretCodeAdapter adapter = (SecretCodeAdapter) mGridView.getAdapter();

		@Override
		protected Void doInBackground(Void... params) {
			long start = System.currentTimeMillis();
			PackageManager pm = getPackageManager();
			Utils.checkBlackList(pm);
			start = System.currentTimeMillis();
			long n2 = 0;
			char characters[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
			StringBuilder sb = new StringBuilder();
			int [] set =  new int [1];
			while(set.length < 8) {
				n2++;
				SecretCode code = Utils.findSecretCode(generateString(set, characters, sb), pm);
				if (code != null) {
					publishProgress(code);
				}
				if(set[set.length-1] != characters.length-1){
					set[set.length-1]++;
				} else {
					for (int i = set.length-1; i >= 0; i--) {
						if(set[i] == characters.length-1){
							if(i == 0){
								set = new int [set.length+1];
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
			Log.e("","duration = "+(System.currentTimeMillis()-start) + " nb = " + n2);

			return null;
		}

		private String generateString(int [] set,char [] characters, StringBuilder sb){
			char [] str = new char [set.length];
			for (int i=0; i<set.length; i++) {
				str[i] = characters[set[i]];
			}
			return new String(str);
		}
		
		private String next(String s) {
			StringBuilder res = new StringBuilder(s);
			for (int i = res.length()-1; i >= 0; i--) {
				if(res.charAt(i) == '9'){
					res.setCharAt(i, '0');
				} else {
					break;
				}
			}
			return "0"+res.toString();
		}

		private String increment(String s) {
			StringBuilder res = new StringBuilder(s);
			char end = res.charAt(res.length()-1);
			if(end != '9'){
				res.setCharAt(res.length()-1, ++end);
				return res.toString();
			} else{
				for (int i = res.length()-1; i >= 0; i--) {
					if(res.charAt(i) == '9'){
						res.setCharAt(i, '0');
					} else {
						char c = res.charAt(i);
						res.setCharAt(i, ++c);
						return res.toString();
					}
				}
			}
			return s;
		}

		private boolean shouldIncrement(String s) {
			for (int i = 0; i < s.length(); i++) {
				if(s.charAt(i) != '9'){
					return true;
				}
			}
			return false;
		}

		@Override
		protected void onProgressUpdate(SecretCode... values) {
			for (SecretCode value : values) {
				if (adapter.addItem(value)) {
					Utils.saveSecretCodes(getApplicationContext(),
							adapter.getItems());
				}
			}
			super.onProgressUpdate(values);
		}
	}
}
