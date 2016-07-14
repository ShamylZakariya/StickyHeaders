package org.zakariya.stickyheadersapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.zakariya.stickyheaders.PagedLoadScrollListener;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import org.zakariya.stickyheadersapp.R;
import org.zakariya.stickyheadersapp.adapters.PagedDemoAdapter;
import org.zakariya.stickyheadersapp.api.PagedMockLoader;

public class PagedScrollDemoActivity extends DemoActivity implements PagedMockLoader.Listener {

	private static final String TAG = PagedScrollDemoActivity.class.getSimpleName();
	private static final int NUM_PAGES = 3;


	PagedDemoAdapter adapter;
	PagedMockLoader loader;
	PagedLoadScrollListener.LoadCompleteNotifier loadCompleteNotifier;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loader = new PagedMockLoader(NUM_PAGES);
		adapter = new PagedDemoAdapter();
		adapter.addSection(loader.vendSection(0));

		StickyHeaderLayoutManager layoutManager = new StickyHeaderLayoutManager();
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(adapter);

		recyclerView.addOnScrollListener(new PagedLoadScrollListener(layoutManager) {
			@Override
			public void onLoadMore(int page, LoadCompleteNotifier loadCompleteNotifier) {
				Log.d(TAG, "onLoadMore() called with: " + "page = [" + page + "]");

				// start load, and hold on to loadCompleteNotifier so we can notify completion of load later
				PagedScrollDemoActivity.this.loadCompleteNotifier = loadCompleteNotifier;
				loader.load(page, PagedScrollDemoActivity.this);
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

	@Override
	public void onSectionLoadBegun() {
		adapter.showLoadingIndicator();
	}

	@Override
	public void onSectionLoadProgress(float progress) {
		adapter.updateLoadingIndicatorProgress(progress);
	}

	@Override
	public void onSectionLoaded(PagedMockLoader.SectionModel sectionModel) {
		Log.i(TAG, "onSectionLoaded: notifying load complete");

		adapter.hideLoadingIndicator();
		adapter.addSection(sectionModel);
		loadCompleteNotifier.notifyLoadComplete();
	}

	@Override
	public void onSectionsExhausted() {
		Log.i(TAG, "onSectionsExhausted: notifying that there are no more sections to load");

		adapter.hideLoadingIndicator();
		adapter.showLoadExhaustedIndicator();
		loadCompleteNotifier.notifyLoadExhausted();
	}
}
