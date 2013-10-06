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

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import fr.simon.marquis.secretcodes.R;
import fr.simon.marquis.secretcodes.model.SecretCode;
import fr.simon.marquis.secretcodes.service.CrawlerService;
import fr.simon.marquis.secretcodes.util.Utils;

public class MainActivity extends ActionBarActivity {

	private GridView mGridView;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				switch (bundle.getInt(CrawlerService.ACTION)) {
				case CrawlerService.ACTION_START:
					supportInvalidateOptionsMenu();
					break;
				case CrawlerService.ACTION_ADD:
					String obj = bundle
							.getString(CrawlerService.SECRETCODE_KEY);
					if (obj != null) {
						try {
							SecretCode sc = SecretCode.fromJSON(new JSONObject(
									obj));
							if (mGridView != null) {
								((SecretCodeAdapter) mGridView.getAdapter())
										.addItem(sc);
							}
						} catch (JSONException e) {
							// No-op
						}
					}
					break;
				case CrawlerService.ACTION_END:
					supportInvalidateOptionsMenu();
					break;
				default:
					break;
				}
			}
		}
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getActionBar().setTitle(
				Utils.applyCustomTypeFace(getString(R.string.app_name), this));
		setContentView(R.layout.activity_main);
		mGridView = (GridView) findViewById(R.id.gridView);
		mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
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

		mGridView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				((SecretCodeAdapter) mGridView.getAdapter())
						.itemCheckedStateChanged(position, checked);
				mode.setTitle(Html.fromHtml("<b>"
						+ mGridView.getCheckedItemCount() + "</b>"));
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.action_delete:
					((SecretCodeAdapter) mGridView.getAdapter())
							.deleteSelection(getApplicationContext());
					mode.finish();
					return true;
				case R.id.action_select_all:
					boolean check = mGridView.getCheckedItemCount() != mGridView
							.getCount();
					for (int i = 0; i < mGridView.getCount(); i++) {
						mGridView.setItemChecked(i, check);
					}
					return true;
				default:
					return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.cab, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				((SecretCodeAdapter) mGridView.getAdapter()).resetSelection();
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

		});

		supportInvalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		setSupportProgressBarIndeterminateVisibility(CrawlerService.isCrawling);
		menu.findItem(R.id.action_scan).setVisible(!CrawlerService.isCrawling);
		menu.findItem(R.id.action_cancel).setVisible(CrawlerService.isCrawling);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_scan:
			startService(new Intent(this, CrawlerService.class));
			break;
		case R.id.action_cancel:
			stopService(new Intent(this, CrawlerService.class));
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		((SecretCodeAdapter) mGridView.getAdapter()).setSelection(mGridView
				.getCheckedItemPositions());
		registerReceiver(receiver, new IntentFilter(
				CrawlerService.BROADCAST_INTENT));
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

}
