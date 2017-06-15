package org.zakariya.stickyheadersapp.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import org.zakariya.stickyheadersapp.R;

import java.util.ArrayList;

/**
 * Base activity for StickyHeadersApp demos
 */
public class DemoActivity extends AppCompatActivity {

	private static final String TAG = DemoActivity.class.getSimpleName();
	private static final String STATE_SCROLL_POSITION = "DemoActivity.STATE_SCROLL_POSITION";

	public static final boolean SHOW_ADAPTER_POSITIONS = true;

	AppBarLayout appBarLayout;
	CollapsingToolbarLayout collapsingToolbarLayout;
	RecyclerView recyclerView;
	ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewLayout());

		appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
		collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		progressBar = (ProgressBar) findViewById(R.id.progress);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if (toolbar != null) {
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
		}
	}

	@LayoutRes
	protected int getContentViewLayout(){
		return R.layout.activity_demo;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
		Parcelable scrollState = lm.onSaveInstanceState();
		outState.putParcelable(STATE_SCROLL_POSITION, scrollState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(STATE_SCROLL_POSITION));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_demo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.menuItemInspect:
				showInspectDialog();
				break;

			case R.id.scrollToBottom:
				showScrollToPositionDialog();
				break;

			case R.id.reloadMenuItem:
				RecyclerView.Adapter adapter = recyclerView.getAdapter();
				if (adapter instanceof SectioningAdapter) {
					((SectioningAdapter) adapter).notifyAllSectionsDataSetChanged();
				} else {
					adapter.notifyDataSetChanged();
				}
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class ScrollDialogSingleChoiceItemSelection {
		int which = -1;
	}

	private void showInspectDialog() {

		if (!(recyclerView.getLayoutManager() instanceof StickyHeaderLayoutManager)) {
			return;
		}

		boolean fullVisibleOnly = true;
		StickyHeaderLayoutManager layoutManager = (StickyHeaderLayoutManager) recyclerView.getLayoutManager();
		SectioningAdapter.HeaderViewHolder headerViewHolder = layoutManager.getFirstVisibleHeaderViewHolder(fullVisibleOnly);
		SectioningAdapter.ItemViewHolder itemViewHolder = layoutManager.getFirstVisibleItemViewHolder(fullVisibleOnly);
		SectioningAdapter.FooterViewHolder footerViewHolder = layoutManager.getFirstVisibleFooterViewHolder(fullVisibleOnly);

		ArrayList<String> inspections = new ArrayList<>();

		if (headerViewHolder != null) {
			Log.i(TAG, "showInspectDialog: first header adapter position: " + headerViewHolder.getAdapterPosition());
			inspections.add(getString(R.string.inspect_header_adapter_position, headerViewHolder.getAdapterPosition()));
		}

		if (itemViewHolder != null) {
			Log.i(TAG, "showInspectDialog: first item adapter position: " + itemViewHolder.getAdapterPosition());
			inspections.add(getString(R.string.inspect_item_adapter_position, itemViewHolder.getAdapterPosition()));
		}

		if (footerViewHolder != null) {
			Log.i(TAG, "showInspectDialog: first footer adapter position: " + footerViewHolder.getAdapterPosition());
			inspections.add(getString(R.string.inspect_footer_adapter_position, footerViewHolder.getAdapterPosition()));
		}

		String message = getString(R.string.inspect_empty);;
		if (!inspections.isEmpty()) {
			message = TextUtils.join("\n", inspections);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.inspect_title)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, null)
				.show();
	}

	private void showScrollToPositionDialog() {
		final ScrollDialogSingleChoiceItemSelection selection = new ScrollDialogSingleChoiceItemSelection();
		selection.which = 0;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert_dialog_scroll_title)
				.setSingleChoiceItems(R.array.scroll_positions, selection.which, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						selection.which = which;
					}
				})
				.setPositiveButton(R.string.alert_dialog_button_title_jump, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						RecyclerView.Adapter adapter = recyclerView.getAdapter();
						switch (selection.which) {
							case 0:
								recyclerView.scrollToPosition(0);
								break;
							case 1:
								recyclerView.scrollToPosition(adapter.getItemCount() / 2);
								break;
							case 2:
								recyclerView.scrollToPosition(adapter.getItemCount() - 1);
								break;
						}
					}
				})
				.setNeutralButton(R.string.alert_dialog_button_title_smooth, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						RecyclerView.Adapter adapter = recyclerView.getAdapter();
						switch (selection.which) {
							case 0:
								recyclerView.smoothScrollToPosition(0);
								break;
							case 1:
								recyclerView.smoothScrollToPosition(adapter.getItemCount() / 2);
								break;
							case 2:
								recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
								break;
						}

					}
				})
				.show();
	}
}
