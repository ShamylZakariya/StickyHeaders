package io.sportner.stickyheadersapp.ui;

import android.os.Bundle;

import io.sportner.stickyheaders.StickyHeaderLayoutManager;
import io.sportner.stickyheadersapp.adapters.MultiTypeDemoAdapter;

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
