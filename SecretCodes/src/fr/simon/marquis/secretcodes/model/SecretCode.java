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
package fr.simon.marquis.secretcodes.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class SecretCode implements Comparable<SecretCode> {

	private static final String KEY_CODE = "CODE";
	private static final String KEY_LABEL = "LABEL";
	private static final String KEY_RESOURCE = "RESOURCE";

	private String mCode;
	private String mLabel;
	private int mResource;
	private Drawable mDrawable;

	public SecretCode(String mCode, String mLabel, int mResource) {
		this.mCode = mCode;
		this.mLabel = mLabel;
		this.mResource = mResource;
	}

	public String getCode() {
		return mCode;
	}

	public void setCode(String code) {
		this.mCode = code;
	}

	public String getLabel() {
		return mLabel;
	}

	public void setLabel(String label) {
		this.mLabel = label;
	}

	public int getResource() {
		return mResource;
	}

	public void setResource(int resource) {
		this.mResource = resource;
	}

	public Drawable getDrawable() {
		return mDrawable;
	}

	public void setDrawable(Drawable drawable) {
		this.mDrawable = drawable;
	}

	public static SecretCode fromJSON(JSONObject obj) {
		try {
			return new SecretCode(obj.getString(KEY_CODE),
					obj.getString(KEY_LABEL), obj.getInt(KEY_RESOURCE));
		} catch (JSONException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	public JSONObject toJSON() {
		try {
			JSONObject obj = new JSONObject();
			obj.put(KEY_CODE, mCode);
			obj.put(KEY_LABEL, mLabel);
			obj.put(KEY_RESOURCE, mResource);
			return obj;
		} catch (JSONException e) {
			return null;
		}
	}

	@Override
	public int compareTo(SecretCode another) {
		int length1 = this.getCode().length();
		int length2 = another.getCode().length();
		if (length1 == length2) {
			return this.getCode().compareTo(another.getCode());
		}
		return length1 - length2;
	}

	public static SecretCode fromResolveInfo(String code,
			ResolveInfo resolveInfo, PackageManager pm) {
		return new SecretCode(code, String.valueOf(resolveInfo.loadLabel(pm)),
				resolveInfo.getIconResource());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mCode == null) ? 0 : mCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SecretCode other = (SecretCode) obj;
		if (mCode == null) {
			if (other.mCode != null)
				return false;
		} else if (!mCode.equals(other.mCode))
			return false;
		return true;
	}

}
