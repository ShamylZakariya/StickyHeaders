package org.zakariya.stickyheadersapp.ui;

import android.os.Bundle;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import org.zakariya.stickyheadersapp.adapters.SimpleDemoAdapter;

/**
 * Created by shamyl on 6/7/16.
 */
public class CollapsingSectionsDemoActivity extends DemoActivity {

	SimpleDemoAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new SimpleDemoAdapter(100, 5, false, false, true, SHOW_ADAPTER_POSITIONS);

		recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
		recyclerView.setAdapter(adapter);
	}
}
