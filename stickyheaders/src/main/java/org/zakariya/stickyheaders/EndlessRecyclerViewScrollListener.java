package org.zakariya.stickyheaders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Adapted from https://gist.github.com/nesquena/d09dc68ff07e845cc622
 */
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
	// The minimum amount of items to have below your current scroll position before loading more.
	private int visibleThreshold = 5;
	// The current offset index of data you have loaded
	private int currentPage = 0;
	// The total number of items in the dataset after the last load
	private int previousTotalItemCount = 0;
	// True if we are still waiting for the last set of data to load.
	private boolean loading = true;
	// Sets the starting page index
	private int startingPageIndex = 0;

	StickyHeaderLayoutManager layoutManager;

	public EndlessRecyclerViewScrollListener(StickyHeaderLayoutManager layoutManager, int visibleThreshold) {
		this.layoutManager = layoutManager;
		this.visibleThreshold = visibleThreshold;
	}

	public EndlessRecyclerViewScrollListener(StickyHeaderLayoutManager layoutManager) {
		this(layoutManager, 5);
	}

	// This happens many times a second during a scroll, so be wary of the code you place here.
	// We are given a few useful parameters to help us work out if we need to load some more data,
	// but first we check if we are waiting for the previous load to finish.
	@Override
	public void onScrolled(RecyclerView view, int dx, int dy) {
		int totalItemCount = layoutManager.getItemCount();
		View lastVisibleItem = layoutManager.getBottommostChildView();
		int lastVisibleItemAdapterPosition = layoutManager.getViewAdapterPosition(lastVisibleItem);

		// If the total item count is zero and the previous isn't, assume the
		// list is invalidated and should be reset back to initial state
		if (totalItemCount < previousTotalItemCount) {
			this.currentPage = this.startingPageIndex;
			this.previousTotalItemCount = totalItemCount;
			if (totalItemCount == 0) {
				this.loading = true;
			}
		}
		// If it’s still loading, we check to see if the dataset count has
		// changed, if so we conclude it has finished loading and update the current page
		// number and total item count.
		if (loading && (totalItemCount > previousTotalItemCount)) {
			loading = false;
			previousTotalItemCount = totalItemCount;
		}

		// If it isn’t currently loading, we check to see if we have breached
		// the visibleThreshold and need to reload more data.
		// If we do need to reload some more data, we execute onLoadMore to fetch the data.
		// threshold should reflect how many total columns there are too
		if (!loading && (lastVisibleItemAdapterPosition + visibleThreshold) > totalItemCount) {
			currentPage++;
			onLoadMore(currentPage, totalItemCount);
			loading = true;
		}
	}

	// Defines the process for actually loading more data based on page
	public abstract void onLoadMore(int page, int totalItemsCount);

}
