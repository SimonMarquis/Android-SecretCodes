package fr.simon.marquis.secretcodes;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.content.AsyncTaskLoader;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class SecretCodeLoader extends AsyncTaskLoader<List<SecretCode>> {


    private final InterestingConfigChanges lastConfig = new InterestingConfigChanges();
    final PackageManager pm;

    private List<SecretCode> codes;
    private PackageIntentReceiver packageObserver;

    public SecretCodeLoader(Context context) {
        super(context);
        pm = getContext().getPackageManager();
    }

    @Override
    public List<SecretCode> loadInBackground() {
        List<SecretCode> entries = Crawler.crawl(this);
        Collections.sort(entries, ALPHA_COMPARATOR);
        return entries;
    }

    @Override
    public void deliverResult(List<SecretCode> codes) {
        this.codes = codes;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately deliver its results.
            super.deliverResult(codes);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (codes != null) {
            // If we currently have a result available, deliver it immediately.
            deliverResult(codes);
        }

        // Start watching for changes in the app data.
        if (packageObserver == null) {
            packageObserver = new PackageIntentReceiver(this);
        }

        // Has something interesting in the configuration changed since we last built the code list?
        boolean configChange = lastConfig.applyNewConfig(getContext().getResources());
        if (takeContentChanged() || codes == null || configChange) {
            // If the data has changed since the last time it was loaded or is not currently available, start a load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(List<SecretCode> codes) {
        super.onCanceled(codes);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (codes != null) {
            codes = null;
        }
        if (packageObserver != null) {
            getContext().unregisterReceiver(packageObserver);
            packageObserver = null;
        }
    }

    /**
     * Helper for determining if the configuration has changed in an interesting
     * way so we need to rebuild the code list.
     */
    public static class InterestingConfigChanges {
        final Configuration mLastConfiguration = new Configuration();

        boolean applyNewConfig(Resources res) {
            int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
            return (configChanges & (ActivityInfo.CONFIG_LOCALE)) != 0;
        }
    }

    private static final Comparator<SecretCode> ALPHA_COMPARATOR = new Comparator<SecretCode>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(SecretCode object1, SecretCode object2) {
            return sCollator.compare(object1.getLabel(), object2.getLabel());
        }
    };
}