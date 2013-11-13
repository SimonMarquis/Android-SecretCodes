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
package fr.simon.marquis.secretcodes.roboto;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import fr.simon.marquis.secretcodes.R;

public class RobotoTextView extends TextView {

	public RobotoTextView(Context context) {
		super(context);
		onInitTypeface(context, null, 0);
	}

	public RobotoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		onInitTypeface(context, attrs, 0);
	}

	public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		onInitTypeface(context, attrs, defStyle);
	}

	private void onInitTypeface(Context context, AttributeSet attrs, int defStyle) {
		if (isInEditMode()) {
			return;
		}

		int typefaceValue = 0;
		if (attrs != null) {
			TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.RobotoTextView, defStyle, 0);
			typefaceValue = values.getInt(R.styleable.RobotoTextView_typeface, 0);
			values.recycle();
		}

		Typeface robotoTypeface = RobotoTypefaceManager.obtaintTypeface(context, typefaceValue);
		setTypeface(robotoTypeface);
	}

}
