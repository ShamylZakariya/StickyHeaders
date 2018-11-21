package io.sportner.stickyheadersapp.ui;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;

import io.sportner.stickyheaders.StickyHeaderLayoutManager;
import io.sportner.stickyheadersapp.R;
import io.sportner.stickyheadersapp.StickyHeadersDemoApp;
import io.sportner.stickyheadersapp.adapters.AddressBookDemoAdapter;
import io.sportner.stickyheadersapp.api.RandomUserLoader;
import io.sportner.stickyheadersapp.model.Person;

import java.util.List;

/**
 * Shows a fake addressbook listing, loaded from randomuser.me, where the people are sorted
 * into sections by the first letter of last name.
 */
public class AddressBookDemoActivity extends DemoActivity implements RandomUserLoader.OnLoadCallback {

	private static final String TAG = AddressBookDemoActivity.class.getSimpleName();
	AddressBookDemoAdapter adapter = new AddressBookDemoAdapter();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
		recyclerView.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		progressBar.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.GONE);
		getRandomUserLoader().load(this);
	}

	@Override
	public void onRandomUsersDidLoad(List<Person> randomUsers) {
		progressBar.setVisibility(View.GONE);
		recyclerView.setVisibility(View.VISIBLE);
		adapter.setPeople(randomUsers);
	}

	@Override
	public void onRandomUserLoadFailure(final Throwable t) {
		Log.e(TAG, "onRandomUserLoadFailure: Unable to load people, e:" + t.getLocalizedMessage() );

		progressBar.setVisibility(View.GONE);
		recyclerView.setVisibility(View.GONE);

		Snackbar snackbar = Snackbar.make(recyclerView, "Unable to load addressbook", Snackbar.LENGTH_LONG);
		snackbar.setAction(R.string.demo_addressbook_load_error_action, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showRandomUserLoadError(t.getLocalizedMessage());
			}
		});
		snackbar.show();
	}

	private void showRandomUserLoadError(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.demo_addressbook_load_error_dialog_title)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, null)
				.show();
	}

	private RandomUserLoader getRandomUserLoader() {
		return ((StickyHeadersDemoApp) getApplicationContext()).getRandomUserLoader();
	}
}
