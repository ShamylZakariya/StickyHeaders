package org.zakariya.stickyheaders;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * StickyHeaderLayoutManager
 * Provides equivalent behavior to a simple LinearLayoutManager, but where section header items
 * are positioned in a "sticky" manner like the section headers in iOS's UITableView.
 * StickyHeaderLayoutManager MUST be used in conjunction with SectioningAdapter.
 *
 * @see SectioningAdapter
 */
public class StickyHeaderLayoutManager extends RecyclerView.LayoutManager {

	public enum HeaderPosition {
		NONE,
		NATURAL,
		STICKY,
		TRAILING
	}

	/**
	 * Callback interface for monitoring when header positions change between members of HeaderPosition enum values.
	 * This can be useful if client code wants to change appearance for headers in HeaderPosition.STICKY vs normal positioning.
	 *
	 * @see HeaderPosition
	 */
	public interface HeaderPositionChangedCallback {
		/**
		 * Called when a sections header positioning approach changes. The position can be HeaderPosition.NONE, HeaderPosition.NATURAL, HeaderPosition.STICKY or HeaderPosition.TRAILING
		 *
		 * @param sectionIndex the sections [0...n)
		 * @param header       the header view
		 * @param oldPosition  the previous positioning of the header (NONE, NATURAL, STICKY or TRAILING)
		 * @param newPosition  the new positioning of the header (NATURAL, STICKY or TRAILING)
		 */
		void onHeaderPositionChanged(int sectionIndex, View header, HeaderPosition oldPosition, HeaderPosition newPosition);
	}

	private static final String TAG = StickyHeaderLayoutManager.class.getSimpleName();

	SectioningAdapter adapter;
	HashSet<View> headerViews = new HashSet<>(); // holds all the visible section headers
	HashMap<Integer, HeaderPosition> headerPositionsBySection = new HashMap<>(); // holds the HeaderPosition for each header
	HeaderPositionChangedCallback headerPositionChangedCallback;

	// adapter position of first (lowest-y-value) visible item.
	int firstAdapterPosition;


	public StickyHeaderLayoutManager() {
	}

	public HeaderPositionChangedCallback getHeaderPositionChangedCallback() {
		return headerPositionChangedCallback;
	}

	/**
	 * Assign callback object to be notified when a header view position changes between states of the HeaderPosition enum
	 *
	 * @param headerPositionChangedCallback the callback
	 * @see HeaderPosition
	 */
	public void setHeaderPositionChangedCallback(HeaderPositionChangedCallback headerPositionChangedCallback) {
		this.headerPositionChangedCallback = headerPositionChangedCallback;
	}

	@Override
	public void onAttachedToWindow(RecyclerView view) {
		super.onAttachedToWindow(view);
		try {
			adapter = (SectioningAdapter) view.getAdapter();
		} catch (ClassCastException e) {
			Log.e(TAG, "onAttachedToWindow: StickyHeaderLayoutManager must be used with a RecyclerView who's adapter is a kind of SectioningAdapter");
		}
	}

	@Override
	public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
		super.onDetachedFromWindow(view, recycler);
		adapter = null;
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

		int top = updateFirstAdapterPosition();

		// RESET
		headerViews.clear();
		headerPositionsBySection.clear();
		detachAndScrapAttachedViews(recycler);

		int height;
		int left = getPaddingLeft();
		int right = getWidth() - getPaddingRight();
		int parentBottom = getHeight() - getPaddingBottom();

		// walk through adapter starting at firstAdapterPosition stacking each vended item
		for (int adapterPosition = firstAdapterPosition; adapterPosition < state.getItemCount(); adapterPosition++) {

			View v = recycler.getViewForPosition(adapterPosition);
			addView(v);
			measureChildWithMargins(v, 0, 0);

			int itemViewType = getViewType(v);
			if (itemViewType == SectioningAdapter.TYPE_HEADER) {
				headerViews.add(v);

				// use the header's height
				height = getDecoratedMeasuredHeight(v);
				layoutDecorated(v, left, top, right, top + height);

				// we need to vend the ghost header and position/size it same as the actual header
				adapterPosition++;
				View ghostHeader = recycler.getViewForPosition(adapterPosition);
				addView(ghostHeader);
				layoutDecorated(ghostHeader, left, top, right, top + height);

			} else if (itemViewType == SectioningAdapter.TYPE_GHOST_HEADER) {

				// we need to back up and get the header for this ghostHeader
				View headerView = recycler.getViewForPosition(adapterPosition-1);
				headerViews.add(headerView);
				addView(headerView);
				measureChildWithMargins(headerView, 0, 0);
				height = getDecoratedMeasuredHeight(headerView);

				layoutDecorated(headerView, left, top, right, top + height);
				layoutDecorated(v, left, top, right, top + height);

			} else {
				height = getDecoratedMeasuredHeight(v);
				layoutDecorated(v, left, top, right, top + height);
			}

			top += height;

			// if the item we just laid out falls off the bottom of the view, we're done
			if (v.getBottom() >= parentBottom) {
				break;
			}
		}

		// put headers in sticky positions if necessary
		updateHeaderPositions(recycler);
	}

	/**
	 * Get the header item for a given section, creating it if it's not already in the view hierarchy
	 *
	 * @param recycler     the recycler
	 * @param sectionIndex the index of the section for in question
	 * @return the header, or null if the adapter specifies no header for the section
	 */
	View createSectionHeaderIfNeeded(RecyclerView.Recycler recycler, int sectionIndex) {

		if (adapter.doesSectionHaveHeader(sectionIndex)) {

			// first, see if we've already got a header for this section
			for (int i = 0, n = getChildCount(); i < n; i++) {
				View view = getChildAt(i);
				if (getViewType(view) == SectioningAdapter.TYPE_HEADER && getViewSectionIndex(view) == sectionIndex) {
					return view;
				}
			}

			// looks like we need to create one
			int headerAdapterPosition = adapter.getAdapterPositionForSectionHeader(sectionIndex);
			View headerView = recycler.getViewForPosition(headerAdapterPosition);
			headerViews.add(headerView);
			addView(headerView);
			measureChildWithMargins(headerView, 0, 0);

			return headerView;
		}

		return null;
	}


	@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {

		if (getChildCount() == 0) {
			return 0;
		}

		int scrolled = 0;
		int left = getPaddingLeft();
		int right = getWidth() - getPaddingRight();


		if (dy < 0) {

			// content moving downwards, so we're panning to top of list
			//Log.i(TAG, "scrollVerticallyBy: SCROLLING TO TOP");

			View topView = getTopmostChildView();
			while (scrolled > dy) {

				// get the topmost view
				int hangingTop = Math.max(-getDecoratedTop(topView), 0);
				int scrollBy = Math.min(scrolled - dy, hangingTop); // scrollBy is positive, causing content to move downwards

				scrolled -= scrollBy;
				offsetChildrenVertical(scrollBy);

				// vend next view above topView

				if (firstAdapterPosition > 0 && scrolled > dy) {
					firstAdapterPosition--;

					// we're skipping headers. they should already be vended, but if we're vending a ghostHeader
					// here an actual header will be vended if needed for measurement
					int itemViewType = adapter.getItemViewType(firstAdapterPosition);
					boolean isHeader = itemViewType == SectioningAdapter.TYPE_HEADER;

					// skip the header, move to next item above
					if (isHeader) {
						firstAdapterPosition--;
						if (firstAdapterPosition < 0) {
							break;
						}
					}

					View v = recycler.getViewForPosition(firstAdapterPosition);
					addView(v, 0);

					int bottom = getDecoratedTop(topView);
					int top;
					boolean isGhostHeader = itemViewType == SectioningAdapter.TYPE_GHOST_HEADER;
					if (isGhostHeader) {
						View header = createSectionHeaderIfNeeded(recycler, adapter.getSectionForAdapterPosition(firstAdapterPosition));
						top = bottom - getDecoratedMeasuredHeight(header); // header is already measured
					} else {
						measureChildWithMargins(v, 0, 0);
						top = bottom - getDecoratedMeasuredHeight(v);
					}

					layoutDecorated(v, left, top, right, bottom);
					topView = v;

				} else {
					break;
				}

			}

		} else {

			// content moving up, we're headed to bottom of list
			//Log.i(TAG, "scrollVerticallyBy: SCROLLING TO BOTTOM");

			int parentHeight = getHeight();
			View bottomView = getBottommostChildView();

			while (scrolled < dy) {
				int hangingBottom = Math.max(getDecoratedBottom(bottomView) - parentHeight, 0);
				int scrollBy = -Math.min(dy - scrolled, hangingBottom);
				scrolled -= scrollBy;
				offsetChildrenVertical(scrollBy);

				int adapterPosition = getViewAdapterPosition(bottomView);
				int nextAdapterPosition = adapterPosition + 1;

				if (scrolled < dy && nextAdapterPosition < state.getItemCount()) {

					int top = getDecoratedBottom(bottomView);

					int itemViewType = adapter.getItemViewType(nextAdapterPosition);
					if (itemViewType == SectioningAdapter.TYPE_HEADER) {

						// get the header and measure it so we can followup immediately by vending the ghost header
						View headerView = createSectionHeaderIfNeeded(recycler, adapter.getSectionForAdapterPosition(nextAdapterPosition));
						int height = getDecoratedMeasuredHeight(headerView);
						layoutDecorated(headerView, left, 0, right, height);

						// but we need to vend the followup ghost header too
						nextAdapterPosition++;
						View ghostHeader = recycler.getViewForPosition(nextAdapterPosition);
						addView(ghostHeader);
						layoutDecorated(ghostHeader, left, top, right, top + height);
						bottomView = ghostHeader;

					} else if (itemViewType == SectioningAdapter.TYPE_GHOST_HEADER) {

						// get the header and measure it so we can followup immediately by vending the ghost header
						View headerView = createSectionHeaderIfNeeded(recycler, adapter.getSectionForAdapterPosition(nextAdapterPosition));
						int height = getDecoratedMeasuredHeight(headerView);
						layoutDecorated(headerView, left, 0, right, height);

						// but we need to vend the followup ghost header too
						View ghostHeader = recycler.getViewForPosition(nextAdapterPosition);
						addView(ghostHeader);
						layoutDecorated(ghostHeader, left, top, right, top + height);
						bottomView = ghostHeader;

					} else {

						View v = recycler.getViewForPosition(nextAdapterPosition);
						addView(v);

						measureChildWithMargins(v, 0, 0);
						int height = getDecoratedMeasuredHeight(v);
						layoutDecorated(v, left, top, right, top + height);
						bottomView = v;
					}

				} else {
					break;
				}
			}
		}

		updateHeaderPositions(recycler);
		recycleViewsOutOfBounds(recycler);
		return scrolled;
	}

	@Override
	public RecyclerView.LayoutParams generateDefaultLayoutParams() {
		return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	@Override
	public boolean canScrollVertically() {
		return true;
	}

	public void recycleViewsOutOfBounds(RecyclerView.Recycler recycler) {

		int height = getHeight();
		int numChildren = getChildCount();
		Set<Integer> remainingSections = new HashSet<>();
		Set<View> viewsToRecycle = new HashSet<>();

		// we do this in two passes.
		// first, recycle everything but headers
		for (int i = 0; i < numChildren; i++) {
			View view = getChildAt(i);

			if (getViewType(view) != SectioningAdapter.TYPE_HEADER) {
				if (getDecoratedBottom(view) < 0 || getDecoratedTop(view) > height) {
					//Log.i(TAG, "recycleViewsOutOfBounds: recycling view at adapter position" + getViewAdapterPosition(view));
					viewsToRecycle.add(view);
				} else {
					// this view is visible, therefore the section lives
					remainingSections.add(getViewSectionIndex(view));
				}
			}
		}

		// second pass, for each "orphaned" header (a header who's section is completely recycled)
		// we remove it if it's gone offscreen

		for (int i = 0; i < numChildren; i++) {
			View view = getChildAt(i);
			int sectionIndex = getViewSectionIndex(view);
			if (getViewType(view) == SectioningAdapter.TYPE_HEADER && !remainingSections.contains(sectionIndex)) {
				float translationY = view.getTranslationY();
				if ((getDecoratedBottom(view) + translationY) < 0 || (getDecoratedTop(view) + translationY) > height) {
					viewsToRecycle.add(view);
					headerViews.remove(view);
					headerPositionsBySection.remove(sectionIndex);
				}
			}
		}

		for (View view : viewsToRecycle) {
			removeAndRecycleView(view, recycler);
		}


		// determine the adapter adapterPosition of first visible item
		updateFirstAdapterPosition();
	}

	View getTopmostChildView() {
		if (getChildCount() == 0) {
			return null;
		}

		// note: We can't use child view order because we muck with moving things to front
		View topmostView = null;
		int top = Integer.MAX_VALUE;

		for (int i = 0, e = getChildCount(); i < e; i++) {
			View v = getChildAt(i);

			// ignore headers
			if (getViewType(v) == SectioningAdapter.TYPE_HEADER) {
				continue;
			}

			int t = getDecoratedTop(v);
			if (t < top) {
				top = t;
				topmostView = v;
			}
		}

		return topmostView;
	}

	View getBottommostChildView() {
		if (getChildCount() == 0) {
			return null;
		}

		// note: We can't use child view order because we muck with moving things to front
		View bottommostView = null;
		int bottom = Integer.MIN_VALUE;

		for (int i = 0, e = getChildCount(); i < e; i++) {
			View v = getChildAt(i);

			// ignore headers
			if (getViewType(v) == SectioningAdapter.TYPE_HEADER) {
				continue;
			}

			int b = getDecoratedBottom(v);
			if (b > bottom) {
				bottom = b;
				bottommostView = v;
			}
		}

		return bottommostView;
	}

	/**
	 * Updates firstAdapterPosition to the adapter position  of the highest item in the list - e.g., the
	 * adapter position of the item with lowest y value in the list
	 *
	 * @return the y value of the topmost view in the layout, or paddingTop if empty
	 */
	int updateFirstAdapterPosition() {

		View topmostView = getTopmostChildView();
		if (topmostView != null) {
			firstAdapterPosition = getViewAdapterPosition(topmostView);
			return topmostView.getTop();
		}

		firstAdapterPosition = 0;
		return getPaddingTop();
	}

	View getFirstViewAfterSection(int sectionIndex) {

		for (int i = 0, n = getChildCount(); i < n; i++) {
			View v = getChildAt(i);
			if (getViewType(v) != SectioningAdapter.TYPE_HEADER) {
				if (getViewSectionIndex(v) == sectionIndex + 1) {
					return v;
				}
			}
		}

		return null;
	}

	/**
	 * Find the ghost header for the items in a given section
	 *
	 * @param sectionIndex the index of the section in question
	 * @return the ghostHeader, if it's on-screen and hasn't been recycled
	 */
	@Nullable
	View findSectionGhostHeader(int sectionIndex) {
		for (int i = 0, n = getChildCount(); i < n; i++) {
			View view = getChildAt(i);
			if (getViewType(view) == SectioningAdapter.TYPE_GHOST_HEADER && getViewSectionIndex(view) == sectionIndex) {
				return view;
			}
		}

		return null;
	}

	void updateHeaderPositions(RecyclerView.Recycler recycler) {

		// first, for each section represented by the current list of items,
		// ensure that the header for that section is extant

		Set<Integer> visitedSections = new HashSet<>();
		for (int i = 0, n = getChildCount(); i < n; i++) {
			View view = getChildAt(i);
			int sectionIndex = getViewSectionIndex(view);
			if (visitedSections.add(sectionIndex)) {
				createSectionHeaderIfNeeded(recycler, sectionIndex);
			}
		}

		// header is always positioned at top
		int left = getPaddingLeft();
		int right = getWidth() - getPaddingRight();

		for (View headerView : headerViews) {
			int sectionIndex = getViewSectionIndex(headerView);

			// find first and last non-header views in this section
			View ghostHeader = null;
			View firstViewInNextSection = null;
			for (int i = 0, n = getChildCount(); i < n; i++) {
				View view = getChildAt(i);
				int type = getViewType(view);
				if (type == SectioningAdapter.TYPE_HEADER) {
					continue;
				}

				int viewSectionIndex = getViewSectionIndex(view);
				if (viewSectionIndex == sectionIndex) {
					if (type == SectioningAdapter.TYPE_GHOST_HEADER) {
						ghostHeader = view;
					}
				} else if (viewSectionIndex == sectionIndex + 1) {
					if (firstViewInNextSection == null) {
						firstViewInNextSection = view;
					}
				}
			}

			int height = getDecoratedMeasuredHeight(headerView);
			int top = getPaddingTop();

			// initial position mark
			HeaderPosition headerPosition = HeaderPosition.STICKY;

			if (ghostHeader != null) {
				int ghostHeaderTop = getDecoratedTop(ghostHeader);
				if (ghostHeaderTop >= top) {
					top = ghostHeaderTop;
					headerPosition = HeaderPosition.NATURAL;
				}
			}

			if (firstViewInNextSection != null) {
				int nextViewTop = getDecoratedTop(firstViewInNextSection);
				if (nextViewTop - height < top) {
					top = nextViewTop - height;
					headerPosition = HeaderPosition.TRAILING;
				}
			}

			// now bring header to front of stack for overlap, and position it
			headerView.bringToFront();
			layoutDecorated(headerView, left, top, right, top + height);

			// notify adapter of positioning for this header
			setHeaderPosition(sectionIndex, headerView, headerPosition);
		}
	}

	void setHeaderPosition(int sectionIndex, View headerView, HeaderPosition newHeaderPosition) {
		if (headerPositionsBySection.containsKey(sectionIndex)) {
			HeaderPosition currentHeaderPosition = headerPositionsBySection.get(sectionIndex);
			if (currentHeaderPosition != newHeaderPosition) {
				headerPositionsBySection.put(sectionIndex, newHeaderPosition);
				if (headerPositionChangedCallback != null) {
					headerPositionChangedCallback.onHeaderPositionChanged(sectionIndex, headerView, currentHeaderPosition, newHeaderPosition);
				}

			}
		} else {
			headerPositionsBySection.put(sectionIndex, newHeaderPosition);
			if (headerPositionChangedCallback != null) {
				headerPositionChangedCallback.onHeaderPositionChanged(sectionIndex, headerView, HeaderPosition.NONE, newHeaderPosition);
			}
		}
	}

	int getViewType(View view) {
		return (int) view.getTag(R.id.sectioning_adapter_tag_key_view_type);
	}

	int getViewSectionIndex(View view) {
		return (int) view.getTag(R.id.sectioning_adapter_tag_key_view_section);
	}

	SectioningAdapter.ViewHolder getViewViewHolder(View view) {
		return (SectioningAdapter.ViewHolder) view.getTag(R.id.sectioning_adapter_tag_key_view_viewholder);
	}

	int getViewAdapterPosition(View view) {
		return getViewViewHolder(view).getAdapterPosition();
	}
}
