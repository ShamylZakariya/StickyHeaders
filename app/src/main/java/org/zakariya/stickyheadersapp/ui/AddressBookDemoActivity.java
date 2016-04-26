package org.zakariya.stickyheadersapp.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import org.zakariya.stickyheadersapp.StickyHeadersDemoApp;
import org.zakariya.stickyheadersapp.adapters.AddressBookAdapter;
import org.zakariya.stickyheadersapp.api.RandomUserLoader;
import org.zakariya.stickyheadersapp.model.Person;

import java.util.List;

/**
 * Created by shamyl on 4/26/16.
 */
public class AddressBookDemoActivity extends DemoActivity {

	private static final String TAG = AddressBookDemoActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		recyclerView.setLayoutManager(new StickyHeaderLayoutManager());

		final AddressBookAdapter adapter = new AddressBookAdapter();
		recyclerView.setAdapter(adapter);

		progressBar.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.GONE);

		getRandomUserLoader().load(new RandomUserLoader.OnLoadCallback() {
			@Override
			public void onRandomUsersDidLoad(List<Person> randomUsers) {
				progressBar.setVisibility(View.GONE);
				recyclerView.setVisibility(View.VISIBLE);
				adapter.setPeople(randomUsers);
			}

			@Override
			public void onFailure(Throwable t) {
				progressBar.setVisibility(View.GONE);
				recyclerView.setVisibility(View.GONE);
				Snackbar.make(recyclerView, "Unable to load fake people", Snackbar.LENGTH_LONG).show();
			}
		});
	}

	private RandomUserLoader getRandomUserLoader() {
		return ((StickyHeadersDemoApp) getApplicationContext()).getRandomUserLoader();
	}
}
