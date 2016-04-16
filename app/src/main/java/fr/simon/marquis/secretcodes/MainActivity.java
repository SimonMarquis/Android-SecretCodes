package fr.simon.marquis.secretcodes;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<SecretCode>>, SecretCodeAdapter.ItemClickListener {

    private static final int LOADER_ID = 1;

    private RecyclerView recyclerView;
    private View emptyView;
    private View progressView;
    private TextView countView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressView = findViewById(R.id.progressView);
        emptyView = findViewById(R.id.emptyView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.numColumns), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new fr.simon.marquis.secretcodes.SecretCodeAdapter(this, this));

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setCustomView(R.layout.count_view);
        bar.setDisplayShowCustomEnabled(true);
        countView = (TextView) bar.getCustomView();

        // emptyView.animate().alpha(0).setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                showAboutDialog();
                break;
            case R.id.action_online_database:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.online_database_url))));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showAboutDialog() {
        AboutDialog.show(getSupportFragmentManager());
    }

    @Override
    public Loader<List<SecretCode>> onCreateLoader(int id, Bundle args) {
        Log.e("Loader", "onCreateLoader");
        return new SecretCodeLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<SecretCode>> loader, List<SecretCode> data) {
        int count = data.size();
        progressView.setVisibility(View.GONE);
        emptyView.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        countView.setText(String.valueOf(count));
        tada(countView, 2).start();
        ((fr.simon.marquis.secretcodes.SecretCodeAdapter) recyclerView.getAdapter()).setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<SecretCode>> loader) {
        ((fr.simon.marquis.secretcodes.SecretCodeAdapter) recyclerView.getAdapter()).setData(null);
    }

    @Override
    public void itemClicked(SecretCode code) {
        Toast.makeText(MainActivity.this, getString(R.string.execute_code, code.getCode()), Toast.LENGTH_SHORT).show();
        try {
            sendBroadcast(new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://" + code.getCode())));
        } catch (SecurityException se) {
            Toast.makeText(MainActivity.this, R.string.security_exception, Toast.LENGTH_LONG).show();
        }
    }

    private static ObjectAnimator tada(final View view, final float shakeFactor) {
        final PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X, Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f), Keyframe.ofFloat(.2f, .9f), Keyframe.ofFloat(.3f, 1.1f), Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f), Keyframe.ofFloat(.6f, 1.1f), Keyframe.ofFloat(.7f, 1.1f), Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f), Keyframe.ofFloat(1f, 1f));

        final PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y, Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f), Keyframe.ofFloat(.2f, .9f), Keyframe.ofFloat(.3f, 1.1f), Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f), Keyframe.ofFloat(.6f, 1.1f), Keyframe.ofFloat(.7f, 1.1f), Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f), Keyframe.ofFloat(1f, 1f));

        final PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION, Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, -3f * shakeFactor), Keyframe.ofFloat(.2f, -3f * shakeFactor), Keyframe.ofFloat(.3f, 3f * shakeFactor),
                Keyframe.ofFloat(.4f, -3f * shakeFactor), Keyframe.ofFloat(.5f, 3f * shakeFactor), Keyframe.ofFloat(.6f, -3f * shakeFactor),
                Keyframe.ofFloat(.7f, 3f * shakeFactor), Keyframe.ofFloat(.8f, -3f * shakeFactor), Keyframe.ofFloat(.9f, 3f * shakeFactor),
                Keyframe.ofFloat(1f, 0));

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY, pvhRotate).setDuration(1000);
    }
}
