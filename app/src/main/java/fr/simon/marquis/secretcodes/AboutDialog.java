package fr.simon.marquis.secretcodes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AboutDialog extends DialogFragment {

    private static final String TAG = "About";

    private static final String VERSION_UNAVAILABLE = "N/A";

    public static void show(FragmentManager fm) {
        new AboutDialog().show(fm, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(TAG, true).commit();
        PackageManager pm = getActivity().getPackageManager();
        String packageName = getActivity().getPackageName();
        String versionName;
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = VERSION_UNAVAILABLE;
        }

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View rootView = layoutInflater.inflate(R.layout.dialog_about, null);
        TextView nameAndVersionView = (TextView) rootView.findViewById(R.id.app_name_and_version);
        nameAndVersionView.setText(Html.fromHtml(getString(R.string.app_name_and_version, versionName)));
        TextView aboutBodyView = (TextView) rootView.findViewById(R.id.about_body);
        aboutBodyView.setText(Html.fromHtml(getString(R.string.about_body)));
        aboutBodyView.setMovementMethod(new LinkMovementMethod());

        return new AlertDialog.Builder(getActivity()).setView(rootView)
                .setPositiveButton(R.string.action_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).create();
    }

}
