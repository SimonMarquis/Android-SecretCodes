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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import fr.simon.marquis.secretcodes.CrawlerService;
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
		case R.id.action_start:
			startService(new Intent(this, CrawlerService.class));
			break;
		case R.id.action_stop:
			stopService(new Intent(this, CrawlerService.class));
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
}
