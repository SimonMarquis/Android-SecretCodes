package fr.simon.marquis.secretcodes;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class Crawler {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
    private static final String ANDROID_MANIFEST = "AndroidManifest.xml";
    private static final String ATTR_VAL_LABEL = "label";
    private static final String ATTR_VAL_ICON = "icon";
    private static final String ATTR_VAL_HOST = "host";
    private static final String ATTR_VAL_SCHEME = "scheme";
    private static final String ATTR_VAL_ANDROID_SECRET_CODE = "android_secret_code";
    private static final String TAG_APPLICATION = "application";
    private static final String TAG_ACTIVITY = "activity";
    private static final String TAG_ACTIVITY_ALIAS = "activity-alias";
    private static final String TAG_SERVICE = "service";
    private static final String TAG_RECEIVER = "receiver";
    private static final String TAG_INTENT_FILTER = "intent-filter";
    private static final String TAG_DATA = "data";

    private Crawler() {
    }

    @NonNull
    public static List<SecretCode> crawl(@NonNull SecretCodeLoader loader) {
        final Context context = loader.getContext();
        final PackageManager pm = loader.pm;
        Set<String> codes = new HashSet<>();
        ArrayList<SecretCode> secretCodes = new ArrayList<>();
        List<PackageInfo> pil = pm.getInstalledPackages(PackageManager.GET_INTENT_FILTERS);
        if (pil == null) {
            return secretCodes;
        }
        long max = pil.size();
        long cur = 0;
        int currentProgress = 0;

        for (PackageInfo pi : pil) {
            if (loader.isLoadInBackgroundCanceled()) {
                return secretCodes;
            }
            cur++;
            if (currentProgress != (int) (100 * cur / max)) {
                currentProgress = (int) (100 * cur / max);
                //CrawlerNotification.notify(getContext(), secretCodes, currentProgress);
            }
            try {
                XmlResourceParser xrp = context.createPackageContext(pi.packageName, 0).getAssets().openXmlResourceParser(ANDROID_MANIFEST);

                int applicationLabel = 0;
                int componentLabel = 0;
                int intentFilterLabel = 0;

                int applicationIcon = 0;
                int componentIcon = 0;
                int intentFilterIcon = 0;


                ArrayList<String> path = new ArrayList<>();
                while (xrp.next() != XmlPullParser.END_DOCUMENT) {
                    if (loader.isLoadInBackgroundCanceled()) {
                        return secretCodes;
                    }

                    if (xrp.getEventType() == XmlPullParser.END_TAG) {
                        if (!path.isEmpty()) {
                            path.remove(path.size() - 1);
                        }
                    }
                    if (xrp.getEventType() == XmlPullParser.START_TAG) {

                        // application
                        // |
                        // |--- activity, activity-alias, service, receiver
                        // |      |
                        // |      |--- intent filter
                        // |      |      |
                        // |      |      |--- data

                        String name = xrp.getName();
                        path.add(name);
                        switch (name) {
                            case TAG_APPLICATION:
                                applicationLabel = xrp.getAttributeResourceValue(NAMESPACE, ATTR_VAL_LABEL, 0);
                                applicationIcon = xrp.getAttributeResourceValue(NAMESPACE, ATTR_VAL_ICON, 0);
                                break;
                            case TAG_ACTIVITY:
                            case TAG_ACTIVITY_ALIAS:
                            case TAG_SERVICE:
                            case TAG_RECEIVER:
                                componentLabel = xrp.getAttributeResourceValue(NAMESPACE, ATTR_VAL_LABEL, 0);
                                componentIcon = xrp.getAttributeResourceValue(NAMESPACE, ATTR_VAL_ICON, 0);
                                break;
                            case TAG_INTENT_FILTER:
                                intentFilterLabel = xrp.getAttributeResourceValue(NAMESPACE, ATTR_VAL_LABEL, 0);
                                intentFilterIcon = xrp.getAttributeResourceValue(NAMESPACE, ATTR_VAL_ICON, 0);
                                break;
                            case TAG_DATA:
                                if (ATTR_VAL_ANDROID_SECRET_CODE.equals(xrp.getAttributeValue(NAMESPACE, ATTR_VAL_SCHEME))) {
                                    String c = xrp.getAttributeValue(NAMESPACE, ATTR_VAL_HOST);
                                    if (!TextUtils.isEmpty(c) && !codes.contains(c)) {
                                        codes.add(c);
                                        String label = getBestString(pi, pm, applicationLabel, componentLabel, intentFilterLabel);
                                        Uri icon = getBestIcon(pi, applicationIcon, componentIcon, intentFilterIcon);
                                        secretCodes.add(new SecretCode(c, icon, label));
                                    }
                                }
                                break;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException | IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        return secretCodes;
    }


    private static Uri getBestIcon(PackageInfo p, int application, int component, int intentFilter) {
        int icon = intentFilter != 0 ? intentFilter : component != 0 ? component : application;
        return new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).authority(p.packageName).appendPath(Integer.toString(icon)).build();
        // p.applicationInfo.loadIcon(pm);
    }

    private static String getBestString(PackageInfo p, PackageManager pm, int application, int component, int intentFilter) {
        return String.valueOf(pm.getText(p.packageName, intentFilter != 0 ? intentFilter : component != 0 ? component : application, p.applicationInfo));
        // p.applicationInfo.loadLabel(pm);
    }
}
