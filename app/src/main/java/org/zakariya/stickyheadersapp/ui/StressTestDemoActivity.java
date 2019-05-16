package org.zakariya.stickyheadersapp.ui;

import android.os.Bundle;
import android.os.Handler;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import org.zakariya.stickyheadersapp.adapters.SimpleDemoAdapter;

/**
 * Created by shamyl on 6/5/16.
 */
public class StressTestDemoActivity extends DemoActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recyclerView.setLayoutManager(new StickyHeaderLayoutManager());

		// this is a really crude emulation of a scenario where the layout manager
		// has been assigned, but the adapter isn't created and assigned until data is
		// made available (say, because of network latency).

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				recyclerView.setAdapter(new SimpleDemoAdapter(1000, 5, true, true, false, SHOW_ADAPTER_POSITIONS));
			}
		}, 500);
	}
}
