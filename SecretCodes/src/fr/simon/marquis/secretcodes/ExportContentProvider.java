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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import fr.simon.marquis.secretcodes.model.SecretCode;
import fr.simon.marquis.secretcodes.util.Utils;

public class ExportContentProvider extends ContentProvider {

	private static final String ZIP_FILE_NAME = "SecretCodes.zip";
	private static final String JSON_FILE_NAME = "SecretCodes.json";
	private static final int BUFFER = 2048;

	public static final Uri CONTENT_URI = Uri.parse("content://fr.simon.marquis.secretcodes.data/" + ZIP_FILE_NAME);

	public ExportContentProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		if (!CONTENT_URI.equals(uri)) {
			throw new FileNotFoundException(uri.getPath());
		}

		ArrayList<SecretCode> secretCodes = Utils.getSecretCodes(getContext());

		deletePreviousData();
		saveJsonFile(secretCodes);
		saveImageFiles(secretCodes);
		saveZipFile();

		File zipFile = new File(getContext().getFilesDir(), ZIP_FILE_NAME);
		if (zipFile.exists()) {
			return ParcelFileDescriptor.open(zipFile, ParcelFileDescriptor.MODE_READ_ONLY);
		}
		throw new FileNotFoundException(uri.getPath());
	}

	private void saveZipFile() {
		File[] files = getContext().getFilesDir().listFiles();
		String zipPath = getContext().getFilesDir().getAbsolutePath() + "/" + ZIP_FILE_NAME;
		try {
			BufferedInputStream origin = null;
			FileOutputStream zipFile = new FileOutputStream(zipPath);

			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(zipFile));

			byte data[] = new byte[BUFFER];
			for (int i = 0; i < files.length; i++) {
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(files[i].getAbsolutePath().substring(files[i].getAbsolutePath().lastIndexOf("/") + 1));
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}

			out.close();
			Log.d(this.getClass().getSimpleName(), "zipFile created at " + zipPath + " with " + files.length + " files");
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(this.getClass().getSimpleName(), "error while zipping at " + zipPath + " with " + files.length + " files", e);
		}

	}

	private void saveImageFiles(ArrayList<SecretCode> secretCodes) {
		for (SecretCode secretCode : secretCodes) {
			try {
				// Bitmap bm = BitmapFactory.decodeResource(getResources(),
				// secretCode.getDrawableResource());
				if (secretCode.getDrawableResource() == 0) {
					continue;
				}
				Drawable drawable = getContext().getPackageManager().getDrawable(secretCode.getPackageManager(), secretCode.getDrawableResource(),
						null);
				if (drawable == null) {
					continue;
				}
				int height = drawable.getIntrinsicHeight();
				int width = drawable.getIntrinsicWidth();
				Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
				if (createBitmap == null) {
					continue;
				}
				Canvas canvas = new Canvas(createBitmap);
				drawable.setBounds(0, 0, width, height);
				drawable.draw(canvas);

				FileOutputStream openFileOutput = getContext().openFileOutput(secretCode.getCode() + ".png", Context.MODE_PRIVATE);
				createBitmap.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput);
				openFileOutput.flush();
				openFileOutput.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void saveJsonFile(ArrayList<SecretCode> secretCodes) {
		JSONArray json = new JSONArray();
		for (SecretCode secretCode : secretCodes) {
			json.put(secretCode.toJSON());
		}

		try {
			FileOutputStream openFileOutput = getContext().openFileOutput(JSON_FILE_NAME, Context.MODE_PRIVATE);
			openFileOutput.write(json.toString(4).getBytes());
			openFileOutput.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void deletePreviousData() {
		String[] fileList = getContext().fileList();
		for (String file : fileList) {
			getContext().deleteFile(file);
		}
	}
}
