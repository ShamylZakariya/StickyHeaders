package org.zakariya.stickyheadersapp.ui;

import android.os.Bundle;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import org.zakariya.stickyheadersapp.adapters.MultiTypeDemoAdapter;

/**
 * Created by shamyl on 7/11/16.
 */
public class MultiTypeItemDemoActivity extends DemoActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
		recyclerView.setAdapter(new MultiTypeDemoAdapter(20,20));
	}

}
