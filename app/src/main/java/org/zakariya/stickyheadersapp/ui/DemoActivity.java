package org.zakariya.stickyheadersapp.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheadersapp.R;

/**
 * Base activity for StickyHeadersApp demos
 */
public class DemoActivity extends AppCompatActivity {

	private static final String TAG = DemoActivity.class.getSimpleName();

	RecyclerView recyclerView;
	ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_demo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
