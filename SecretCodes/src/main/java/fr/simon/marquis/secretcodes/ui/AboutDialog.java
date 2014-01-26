package fr.simon.marquis.secretcodes.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import fr.simon.marquis.secretcodes.R;

public class AboutDialog extends DialogFragment {
	private static final String VERSION_UNAVAILABLE = "N/A";
	private boolean mExit;

	public static AboutDialog newInstance(boolean exit) {
		AboutDialog frag = new AboutDialog();
		Bundle args = new Bundle();
		args.putBoolean("exit", exit);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle b = getArguments();
		mExit = b.getBoolean("exit");
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("about", true).commit();
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
		View rootView = layoutInflater.inflate(R.layout.dialog_about, null);
		TextView nameAndVersionView = (TextView) rootView.findViewById(R.id.app_name_and_version);
		nameAndVersionView.setText(Html.fromHtml(getString(R.string.app_name_and_version, versionName)));
		TextView aboutBodyView = (TextView) rootView.findViewById(R.id.about_body);
		aboutBodyView.setText(Html.fromHtml(getString(R.string.about_body)));
		aboutBodyView.setMovementMethod(new LinkMovementMethod());

		return new AlertDialog.Builder(getActivity()).setView(rootView)
				.setPositiveButton(mExit ? R.string.action_exit : R.string.action_close, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						if (mExit) {
							getActivity().finish();
						}
					}
				}).create();
	}

}
