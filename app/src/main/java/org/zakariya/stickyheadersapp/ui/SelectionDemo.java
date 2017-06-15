package org.zakariya.stickyheadersapp.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import org.zakariya.stickyheadersapp.R;
import org.zakariya.stickyheadersapp.adapters.SelectionDemoAdapter;

/**
 * Created by shamyl on 7/26/16.
 */
public class SelectionDemo extends DemoActivity implements ActionMode.Callback {

	private static final String TAG = "SelectionDemo";

	GestureDetectorCompat gestureDetector;
	SelectionDemoAdapter adapter;
	ActionMode actionMode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new SelectionDemoAdapter(1000, 5, SHOW_ADAPTER_POSITIONS);
		recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
		recyclerView.setAdapter(adapter);

		recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
			@Override
			public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
				gestureDetector.onTouchEvent(e);
				return false;
			}

			@Override
			public void onTouchEvent(RecyclerView rv, MotionEvent e) {
			}

			@Override
			public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
			}
		});

		gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
				Log.i(TAG, "onSingleTapConfirmed: view: " + view);
				int adapterPosition = recyclerView.getChildAdapterPosition(view);

				if (actionMode != null) {
					toggleSelection(adapterPosition);
				} else {
					int sectionIndex = adapter.getSectionForAdapterPosition(adapterPosition);
					int footerAdapterPosition = adapter.getAdapterPositionForSectionFooter(sectionIndex);

					if (footerAdapterPosition == adapterPosition) {
						onFooterTapped(sectionIndex);
					} else {
						int itemIndex = adapter.getPositionOfItemInSection(sectionIndex, adapterPosition);
						if (itemIndex >= 0) {
							onItemTapped(sectionIndex, itemIndex);
						} else {
							onSectionTapped(sectionIndex);
						}
					}
				}


				return super.onSingleTapConfirmed(e);
			}

			@Override
			public void onLongPress(MotionEvent e) {
				if (actionMode == null) {
					actionMode = startActionMode(SelectionDemo.this);

					View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
					int adapterPosition = recyclerView.getChildAdapterPosition(view);
					toggleSelection(adapterPosition);
				}

				super.onLongPress(e);
			}
		});
	}

	@Override
	@LayoutRes
	protected int getContentViewLayout() {
		return R.layout.activity_selection_demo;
	}

	@Override
	protected void onResume() {
		super.onResume();
		final Snackbar snack = Snackbar.make(recyclerView, R.string.hint_demo_select_long_press, Snackbar.LENGTH_INDEFINITE);
		snack.setAction(android.R.string.ok, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				snack.dismiss();
			}
		}).show();
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
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		appBarLayout.setExpanded(false, true);

		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.menu_cab_selectiondemo, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_delete: {
				adapter.deleteSelection();
				mode.finish();
				return true;
			}

			case R.id.menu_duplicate:
				adapter.duplicateSelection();
				mode.finish();
				return true;
		}
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		this.actionMode = null;
		adapter.clearSelection();
	}

	void toggleSelection(int adapterPosition) {

		Log.d(TAG, "toggleSelection() called with: " + "adapterPosition = [" + adapterPosition + "]");

		// note: We're not supporting selection of entire section because - while it can be useful
		// in some circumstances, it's confusing here. We only allow toggling of items/footers

		int sectionIndex = adapter.getSectionForAdapterPosition(adapterPosition);
		int footerAdapterPosition = adapter.getAdapterPositionForSectionFooter(sectionIndex);

		if (footerAdapterPosition == adapterPosition) {
			Log.d(TAG, "toggleSelection: toggling selection for footer @ section " + sectionIndex);
			adapter.toggleSectionFooterSelection(sectionIndex);
		} else {
			int itemIndex = adapter.getPositionOfItemInSection(sectionIndex, adapterPosition);
			if (itemIndex >= 0) {
				Log.d(TAG, "toggleSelection: toggling selection of item @ section: " + sectionIndex + " itemIndex: " + itemIndex);
				adapter.toggleSectionItemSelected(sectionIndex, itemIndex);
			}
		}

		// update selected item count in CAB
		int selectedItemCount = adapter.getSelectedItemCount();
		String title = getString(R.string.cab_selected_count, selectedItemCount);
		actionMode.setTitle(title);
	}

	void onItemTapped(int sectionIndex, int itemIndex) {
		Log.d(TAG, "onItemTapped() called with: " + "sectionIndex = [" + sectionIndex + "], itemIndex = [" + itemIndex + "]");
	}

	void onSectionTapped(int sectionIndex) {
		Log.d(TAG, "onSectionTapped() called with: " + "sectionIndex = [" + sectionIndex + "]");
	}

	void onFooterTapped(int sectionIndex) {
		Log.d(TAG, "onFooterTapped() called with: " + "sectionIndex = [" + sectionIndex + "]");
	}

}
