package org.zakariya.stickyheadersapp.ui;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import org.zakariya.stickyheadersapp.adapters.SimpleDemoAdapter;

/**
 * Demonstrates use of StickyHeaderLayoutManager::setHeaderPositionChangedCallback
 * to change appearance of a header when it's "sticky" vs when its in its natural position.
 * Here we're using elevation to cast a small shadow from sticky headers.
 */
public class HeaderCallbacksDemoActivity extends DemoActivity {

	private static final String TAG = HeaderCallbacksDemoActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StickyHeaderLayoutManager stickyHeaderLayoutManager = new StickyHeaderLayoutManager();
		recyclerView.setLayoutManager(stickyHeaderLayoutManager);

		// set a header position callback to set elevation on sticky headers, because why not
		stickyHeaderLayoutManager.setHeaderPositionChangedCallback(new StickyHeaderLayoutManager.HeaderPositionChangedCallback() {
			@Override
			public void onHeaderPositionChanged(int sectionIndex, View header, StickyHeaderLayoutManager.HeaderPosition oldPosition, StickyHeaderLayoutManager.HeaderPosition newPosition) {
				Log.i(TAG, "onHeaderPositionChanged: section: " + sectionIndex + " -> old: " + oldPosition.name() + " new: " + newPosition.name());
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					boolean elevated = newPosition == StickyHeaderLayoutManager.HeaderPosition.STICKY;
					header.setElevation(elevated ? 8 : 0);
				}
			}
		});

		recyclerView.setAdapter(new SimpleDemoAdapter(5, 5, true, false, false, SHOW_ADAPTER_POSITIONS));
	}
}
