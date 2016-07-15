package org.zakariya.stickyheadersapp.ui;

import android.os.Bundle;

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
		recyclerView.setAdapter(new SimpleDemoAdapter(1000, 5, true, false, false, false));
	}
}
