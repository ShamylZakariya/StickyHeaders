package org.zakariya.stickyheaders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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

	private static class SectionItem {
		View view;
		int adapterPosition;
		int positionInSection;

		public SectionItem(View view, int adapterPosition, int positionInSection) {
			this.view = view;
			this.adapterPosition = adapterPosition;
			this.positionInSection = positionInSection;
		}
	}

	private static class Section {
		int sectionIndex;
		int headerAdapterPosition;
		int ghostHeaderAdapterPosition;
		View header;
		View ghostHeader;
		int numberOfItemsInSection;
		ArrayList<SectionItem> items = new ArrayList<>();
		View footer;
		int footerAdapterPosition;
	}

	private static class SectionItemSortComparator implements Comparator<SectionItem> {

		@Override
		public int compare(SectionItem lhs, SectionItem rhs) {
			return lhs.adapterPosition - rhs.adapterPosition;
		}

	}

	Context context;
	SectioningAdapter adapter;
	SparseArray<Section> sections = new SparseArray<>();
	SectionItemSortComparator sectionItemSortComparator = new SectionItemSortComparator();

	// holds the adapter position of each visible view
	HashMap<View, Integer> adapterPositionsByView = new HashMap<>();
	HashSet<View> headerViews = new HashSet<>();
	HashMap<Integer, HeaderPosition> headerPositionsBySection = new HashMap<>();

	// adapter position of first (lowest-y-value) visible item.
	int firstAdapterPosition;

	HeaderPositionChangedCallback headerPositionChangedCallback;


	public StickyHeaderLayoutManager(Context context) {
		this.context = context;
	}

	public HeaderPositionChangedCallback getHeaderPositionChangedCallback() {
		return headerPositionChangedCallback;
	}

	public void setHeaderPositionChangedCallback(HeaderPositionChangedCallback headerPositionChangedCallback) {
		this.headerPositionChangedCallback = headerPositionChangedCallback;
	}

	@Override
	public void onAttachedToWindow(RecyclerView view) {
		super.onAttachedToWindow(view);
		try {
			this.adapter = (SectioningAdapter) view.getAdapter();
		} catch (ClassCastException e) {
			Log.e(TAG, "onAttachedToWindow: StickyHeaderLayoutManager must be used with a RecyclerView who's adapter is a kind of SectioningAdapter");
		}
	}

	@Override
	public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
		super.onDetachedFromWindow(view, recycler);
		this.adapter = null;
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

		int top = updateFirstAdapterPosition();

		// RESET
		sections.clear();
		adapterPositionsByView.clear();
		headerViews.clear();
		detachAndScrapAttachedViews(recycler);

		int height;
		int left = getPaddingLeft();
		int right = getWidth() - getPaddingRight();
		int parentBottom = getHeight() - getPaddingBottom();

		// walk through adapter starting at firstAdapterPosition stacking each vended item
		for (int adapterPosition = firstAdapterPosition; adapterPosition < state.getItemCount(); adapterPosition++) {

			View v = recycler.getViewForPosition(adapterPosition);
			registerView(v, adapterPosition);
			addView(v);
			measureChildWithMargins(v, 0, 0);

			int itemViewType = adapter.getItemViewType(adapterPosition);

			// skip headers - they're lazily created on demand
			if (itemViewType == SectioningAdapter.TYPE_HEADER) {
				continue;
			}

			if (itemViewType == SectioningAdapter.TYPE_GHOST_HEADER) {

				// ghost header is sized to same height as the actual header
				// but to do so, we need to ensure actual header has been created
				int sectionIndex = adapter.getSectionForAdapterPosition(adapterPosition);
				View header = getSectionHeader(recycler, sectionIndex);

				measureChildWithMargins(header, 0, 0);
				height = getDecoratedMeasuredHeight(header);
			} else {
				height = getDecoratedMeasuredHeight(v);
			}

			layoutDecorated(v, left, top, right, top + height);
			top += height;

			// if the item we just laid out falls off the bottom of the view, we're done
			if (v.getBottom() >= parentBottom) {
				break;
			}
		}

		// put headers in sticky positions if necessary
		updateHeaderPositions(recycler);
	}

	View getSectionHeader(RecyclerView.Recycler recycler, int sectionIndex) {
		Section section = getSection(sectionIndex);
		return createSectionHeaderIfNeeded(recycler, section);
	}

	View createSectionHeaderIfNeeded(RecyclerView.Recycler recycler, Section section) {
		if (adapter.doesSectionHaveHeader(section.sectionIndex) && section.header == null) {
			int headerAdapterPosition = adapter.getAdapterPositionForSectionHeader(section.sectionIndex);
			View header = recycler.getViewForPosition(headerAdapterPosition);
			registerView(header, headerAdapterPosition);
			addView(header);
			measureChildWithMargins(header, 0, 0);
		}

		return section.header;
	}

	Section getSection(int sectionIndex) {
		Section section = sections.get(sectionIndex);
		if (section == null) {
			section = new Section();
			section.sectionIndex = sectionIndex;
			section.numberOfItemsInSection = adapter.getNumberOfItemsInSection(sectionIndex);
			sections.put(sectionIndex, section);
		}

		return section;
	}

	void registerView(View v, int adapterPosition) {

		// determine which section this item lives in,
		// and then mark it as the header footer, item etc accordingly

		int sectionIndex = adapter.getSectionForAdapterPosition(adapterPosition);
		Section section = getSection(sectionIndex);

		adapterPositionsByView.put(v, adapterPosition);

		switch (adapter.getItemViewType(adapterPosition)) {
			case SectioningAdapter.TYPE_HEADER:
				section.header = v;
				section.headerAdapterPosition = adapterPosition;
				headerViews.add(section.header);
				break;

			case SectioningAdapter.TYPE_GHOST_HEADER:
				section.ghostHeader = v;
				section.ghostHeaderAdapterPosition = adapterPosition;
				break;

			case SectioningAdapter.TYPE_ITEM:
				int positionInSection = adapter.getPositionOfItemInSection(sectionIndex, adapterPosition);
				section.items.add(new SectionItem(v, adapterPosition, positionInSection));
				// sort so that items are in order of adapterPosition
				Collections.sort(section.items, sectionItemSortComparator);
				break;

			case SectioningAdapter.TYPE_FOOTER:
				section.footer = v;
				section.footerAdapterPosition = adapterPosition;
				break;
		}


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

					if (isHeader) {
						firstAdapterPosition--;
						if (firstAdapterPosition < 0) {
							break;
						}
					}

					View v = recycler.getViewForPosition(firstAdapterPosition);
					registerView(v, firstAdapterPosition);
					addView(v, 0);

					int bottom = getDecoratedTop(topView);
					int top;
					boolean isGhostHeader = itemViewType == SectioningAdapter.TYPE_GHOST_HEADER;
					if (isGhostHeader) {
						View header = getSectionHeader(recycler, adapter.getSectionForAdapterPosition(firstAdapterPosition));
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

				int nextAdapterPosition = adapterPositionsByView.get(bottomView) + 1;

				if (scrolled < dy && nextAdapterPosition < state.getItemCount()) {

					// we're skipping headers. they should already be vended, but if we're vending a ghostHeader
					// here an actual header will be vended if needed for measurement

					int top = getDecoratedBottom(bottomView);

					int itemViewType = adapter.getItemViewType(nextAdapterPosition);
					if (itemViewType == SectioningAdapter.TYPE_HEADER) {

						View headerView = getSectionHeader(recycler, adapter.getSectionForAdapterPosition(nextAdapterPosition));
						int height = getDecoratedMeasuredHeight(headerView);
						layoutDecorated(headerView, left, 0, right, height);

						// but we need to vend the followup ghost header too
						nextAdapterPosition++;
						View ghostHeader = recycler.getViewForPosition(nextAdapterPosition);
						registerView(ghostHeader, nextAdapterPosition);
						addView(ghostHeader);
						layoutDecorated(ghostHeader, left, top, right, top + height);
						bottomView = ghostHeader;
					} else {

						if (itemViewType == SectioningAdapter.TYPE_GHOST_HEADER) {
							throw new IllegalStateException("Should never be vending a GHOST_HEADER here");
						}

						View v = recycler.getViewForPosition(nextAdapterPosition);
						registerView(v, nextAdapterPosition);
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

		List<Integer> sectionIndicesToRemove = new ArrayList<>();
		int height = getHeight();

		for (int i = 0, n = sections.size(); i < n; i++) {
			int sectionIndex = sections.keyAt(i);
			Section section = sections.get(sectionIndex);

			//Log.i(TAG, "recycleViewsOutOfBounds: section: " + sectionIndex + " header: " + section.header + " ghostHeader: " + section.ghostHeader + " items: " + section.items + " footer: " + section.footer);

			// remove any item which is offscreen. note we keep section.items in sync hence use of iterator
			Iterator<SectionItem> it = section.items.iterator();
			while (it.hasNext()) {
				SectionItem item = it.next();
				if (getDecoratedBottom(item.view) < 0 || getDecoratedTop(item.view) > height) {
					removeAndRecycleView(item.view, recycler);
					adapterPositionsByView.remove(item.view);
					it.remove();
				}
			}

			// recycle footer
			if (section.footer != null) {
				if (getDecoratedBottom(section.footer) < 0 || getDecoratedTop(section.footer) > height) {
					removeAndRecycleView(section.footer, recycler);
					adapterPositionsByView.remove(section.footer);
					section.footer = null;
					section.footerAdapterPosition = -1;
				}
			}

			// recycle ghost header
			if (section.ghostHeader != null) {
				if (getDecoratedBottom(section.ghostHeader) < 0 || getDecoratedTop(section.ghostHeader) > height) {
					removeAndRecycleView(section.ghostHeader, recycler);
					adapterPositionsByView.remove(section.ghostHeader);
					section.ghostHeader = null;
					section.ghostHeaderAdapterPosition = -1;
				}
			}

			// recycle headers iff offscreen && all items and footers are recycled
			if (section.header != null && section.footer == null && section.items.isEmpty()) {
				float translationY = section.header.getTranslationY();
				if ((getDecoratedBottom(section.header) + translationY) <= 0 || (getDecoratedTop(section.header) + translationY) >= height) {
					removeAndRecycleView(section.header, recycler);
					adapterPositionsByView.remove(section.header);
					headerViews.remove(section.header);
					section.header = null;
					section.headerAdapterPosition = -1;

					// reset the header position state
					headerPositionsBySection.remove(sectionIndex);

					// if the actual header is offscreen, that means this section is entirely offscreen
					// and we need to clean it up
					sectionIndicesToRemove.add(sectionIndex);
				}
			}
		}

		// destroy any sections which are now empty
		for (int sectionIndex : sectionIndicesToRemove) {
			//Log.i(TAG, "recycleViewsOutOfBounds: deleting section: " + sectionIndex);
			sections.remove(sectionIndex);
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
			if (headerViews.contains(v)) {
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
			if (headerViews.contains(v)) {
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
			if (adapterPositionsByView.containsKey(topmostView)) {
				firstAdapterPosition = adapterPositionsByView.get(topmostView);
				return topmostView.getTop();
			}
		}

		firstAdapterPosition = 0;
		return getPaddingTop();
	}

	View getFirstViewAfterSection(Section section) {

		int sectionIndex = section.sectionIndex + 1;
		Section nextSection = sections.get(sectionIndex);
		while( nextSection != null) {

			if (nextSection.ghostHeader != null) {
				return nextSection.ghostHeader;
			}

			if (!nextSection.items.isEmpty()) {
				return nextSection.items.get(0).view;
			}

			if (nextSection.footer != null) {
				return nextSection.footer;
			}

			sectionIndex++;
			nextSection = sections.get(sectionIndex);
		}

		return null;
	}

	void updateHeaderPositions(RecyclerView.Recycler recycler) {
		for (int i = 0, n = sections.size(); i < n; i++) {

			Section section = sections.valueAt(i);
			createSectionHeaderIfNeeded(recycler, section);

			// if the adapter says this section has a header, we'll have it now
			if (section.header != null) {

				// header is always positioned at top
				int left = getPaddingLeft();
				int right = getWidth() - getPaddingRight();
				int height = getDecoratedMeasuredHeight(section.header);
				int top = getPaddingTop();
				layoutDecorated(section.header, left, top, right, top + height);

				HeaderPosition headerPosition = HeaderPosition.STICKY;

				if (section.ghostHeader != null) {
					int ghostHeaderTop = getDecoratedTop(section.ghostHeader);
					if (ghostHeaderTop >= top) {
						top = ghostHeaderTop;
						headerPosition = HeaderPosition.NATURAL;
					}
				}

				View nextView = getFirstViewAfterSection(section);
				if (nextView != null) {
					int nextViewTop = getDecoratedTop(nextView);
					if (nextViewTop - height < top) {
						top = nextViewTop - height;
						headerPosition = HeaderPosition.TRAILING;
					}
				}

				// now bring header to front of stack for overlap, and offset y for sticky positioning
				section.header.bringToFront();
				section.header.setTranslationY(top);

				// notify adapter of positioning for this header
				setHeaderPosition(section.sectionIndex, section.header, headerPosition);
			}
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

}
