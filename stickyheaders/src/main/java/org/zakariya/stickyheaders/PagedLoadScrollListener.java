package org.zakariya.stickyheaders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Adapted from https://gist.github.com/nesquena/d09dc68ff07e845cc622
 */
public abstract class PagedLoadScrollListener extends RecyclerView.OnScrollListener {

	private static final String TAG = PagedLoadScrollListener.class.getSimpleName();
	private static final int DEFAULT_VISIBLE_THRESHOLD = 5;

	public interface LoadCompleteNotifier {
		void notifyLoadComplete();
	}

	private int visibleThreshold;
	private int currentPage = 0;
	private int previousTotalItemCount = 0;
	private boolean loading = false;
	StickyHeaderLayoutManager layoutManager;


	LoadCompleteNotifier loadCompleteNotifier = new LoadCompleteNotifier() {
		@Override
		public void notifyLoadComplete() {
			loading = false;
			previousTotalItemCount = layoutManager.getItemCount();
		}
	};

	public PagedLoadScrollListener(StickyHeaderLayoutManager layoutManager, int visibleThreshold) {
		this.layoutManager = layoutManager;
		this.visibleThreshold = visibleThreshold;
	}

	public PagedLoadScrollListener(StickyHeaderLayoutManager layoutManager) {
		this(layoutManager, DEFAULT_VISIBLE_THRESHOLD);
	}

	@Override
	public void onScrolled(RecyclerView view, int dx, int dy) {

		// no-op if we're loading
		if (this.loading) {
			return;
		}

		// If the total item count is zero and the previous isn't, assume the
		// list is invalidated and should be reset back to initial state
		int totalItemCount = layoutManager.getItemCount();
		if (totalItemCount < previousTotalItemCount) {
			this.currentPage = 0;
			this.previousTotalItemCount = totalItemCount;
		} else if (totalItemCount > 0) {

			View lastVisibleItem = layoutManager.getBottommostChildView();
			int lastVisibleItemAdapterPosition = layoutManager.getViewAdapterPosition(lastVisibleItem);

			if ((lastVisibleItemAdapterPosition + visibleThreshold) > totalItemCount) {
				currentPage++;
				loading = true;
				onLoadMore(currentPage, loadCompleteNotifier);
			}
		}
	}

	// Defines the process for actually loading more data based on page
	public abstract void onLoadMore(int page, LoadCompleteNotifier loadComplete);

}
