package org.zakariya.stickyheadersapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.zakariya.stickyheaders.EndlessRecyclerViewScrollListener;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import org.zakariya.stickyheadersapp.R;
import org.zakariya.stickyheadersapp.adapters.EndlessDemoAdapter;
import org.zakariya.stickyheadersapp.api.EndlessDemoMockLoader;

public class EndlessScrollDemoActivity extends DemoActivity {

	private static final String TAG = EndlessScrollDemoActivity.class.getSimpleName();

	EndlessDemoAdapter adapter;
	EndlessDemoMockLoader loader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loader = new EndlessDemoMockLoader();

		adapter = new EndlessDemoAdapter();
		adapter.addSection(loader.vendSection());

		StickyHeaderLayoutManager layoutManager = new StickyHeaderLayoutManager();
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(adapter);

		recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.d(TAG, "onLoadMore() called with: " + "page = [" + page + "], totalItemsCount = [" + totalItemsCount + "]");
				adapter.addSection(loader.vendSection());
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_endless_scroll_demo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
