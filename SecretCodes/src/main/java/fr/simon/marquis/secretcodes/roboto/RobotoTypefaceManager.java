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
import android.graphics.Typeface;
import android.util.SparseArray;

public class RobotoTypefaceManager {

    public final static int ROBOTO_THIN = 0;
    public final static int ROBOTO_THIN_ITALIC = 1;
    public final static int ROBOTO_LIGHT = 2;
    public final static int ROBOTO_LIGHT_ITALIC = 3;
    public final static int ROBOTO_REGULAR = 4;
    public final static int ROBOTO_ITALIC = 5;
    public final static int ROBOTO_MEDIUM = 6;
    public final static int ROBOTO_MEDIUM_ITALIC = 7;
    public final static int ROBOTO_BOLD = 8;
    public final static int ROBOTO_BOLD_ITALIC = 9;
    public final static int ROBOTO_BLACK = 10;
    public final static int ROBOTO_BLACK_ITALIC = 11;
    public final static int ROBOTO_CONDENSED = 12;
    public final static int ROBOTO_CONDENSED_ITALIC = 13;
    public final static int ROBOTO_CONDENSED_BOLD = 14;
    public final static int ROBOTO_CONDENSED_BOLD_ITALIC = 15;
    public final static int ROBOTOSLAB_THIN = 16;
    public final static int ROBOTOSLAB_LIGHT = 17;
    public final static int ROBOTOSLAB_REGULAR = 18;
    public final static int ROBOTOSLAB_BOLD = 19;

    private final static SparseArray<Typeface> mTypefaces = new SparseArray<Typeface>(20);

    public static Typeface obtaintTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface = mTypefaces.get(typefaceValue);
        if (typeface == null) {
            typeface = createTypeface(context, typefaceValue);
            mTypefaces.put(typefaceValue, typeface);
        }
        return typeface;
    }

    private static Typeface createTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface;
        switch (typefaceValue) {
            case ROBOTO_THIN:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
                break;
            case ROBOTO_THIN_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-ThinItalic.ttf");
                break;
            case ROBOTO_LIGHT:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
                break;
            case ROBOTO_LIGHT_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-LightItalic.ttf");
                break;
            case ROBOTO_REGULAR:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
                break;
            case ROBOTO_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Italic.ttf");
                break;
            case ROBOTO_MEDIUM:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
                break;
            case ROBOTO_MEDIUM_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-MediumItalic.ttf");
                break;
            case ROBOTO_BOLD:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
                break;
            case ROBOTO_BOLD_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-BoldItalic.ttf");
                break;
            case ROBOTO_BLACK:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black.ttf");
                break;
            case ROBOTO_BLACK_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-BlackItalic.ttf");
                break;
            case ROBOTO_CONDENSED:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Condensed.ttf");
                break;
            case ROBOTO_CONDENSED_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-CondensedItalic.ttf");
                break;
            case ROBOTO_CONDENSED_BOLD:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-BoldCondensed.ttf");
                break;
            case ROBOTO_CONDENSED_BOLD_ITALIC:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-BoldCondensedItalic.ttf");
                break;
            case ROBOTOSLAB_THIN:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Thin.ttf");
                break;
            case ROBOTOSLAB_LIGHT:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Light.ttf");
                break;
            case ROBOTOSLAB_REGULAR:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Regular.ttf");
                break;
            case ROBOTOSLAB_BOLD:
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Bold.ttf");
                break;
            default:
                throw new IllegalArgumentException("Unknown `typeface` attribute value " + typefaceValue);
        }
        return typeface;
    }

}
