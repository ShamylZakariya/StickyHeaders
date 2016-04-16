package org.zakariya.stickyheaders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * SectioningAdapter
 * Represents a list of sections, each containing a list of items and optionally a header and or footer item.
 */
@SuppressWarnings("unused")
public class SectioningAdapter extends RecyclerView.Adapter<SectioningAdapter.ViewHolder> {

	private static final String TAG = "SectioningAdapter";

	public static final int TYPE_ITEM = 0;
	public static final int TYPE_HEADER = 1;
	public static final int TYPE_FOOTER = 2;
	static final int TYPE_GHOST_HEADER = 3; // not necessary to be visible externally

	public enum HeaderPosition {
		NONE,
		NATURAL,
		STICKY,
		TRAILING
	}

	private class Section {
		int adapterPosition;    // adapterPosition of first item (the header) of this sections
		int numberOfItems;      // number of items (not including header or footer)
		int length;             // total number of items in sections including header and footer
		boolean hasHeader;      // if true, sections has a header
		boolean hasFooter;      // if true, sections has a footer
	}

	private ArrayList<Section> sections;
	private int totalNumberOfItems;

	private HashMap<Integer, HeaderPosition> headerPositionsBySection = new HashMap<>();

	public class ViewHolder extends RecyclerView.ViewHolder {
		private int section;
		private int numberOfItemsInSection;

		public ViewHolder(View itemView) {
			super(itemView);
		}

		public boolean isHeader() {
			return false;
		}

		public boolean isGhostHeader() {
			return false;
		}

		public boolean isFooter() {
			return false;
		}

		public int getSection() {
			return section;
		}

		private void setSection(int section) {
			this.section = section;
		}

		public int getNumberOfItemsInSection() {
			return numberOfItemsInSection;
		}

		void setNumberOfItemsInSection(int numberOfItemsInSection) {
			this.numberOfItemsInSection = numberOfItemsInSection;
		}
	}

	public class ItemViewHolder extends ViewHolder {
		private int positionInSection;

		public ItemViewHolder(View itemView) {
			super(itemView);
		}

		public int getPositionInSection() {
			return positionInSection;
		}

		private void setPositionInSection(int positionInSection) {
			this.positionInSection = positionInSection;
		}
	}

	public class HeaderViewHolder extends ViewHolder {

		public HeaderViewHolder(View itemView) {
			super(itemView);
		}

		@Override
		public boolean isHeader() {
			return true;
		}
	}

	public class GhostHeaderViewHolder extends ViewHolder {
		public GhostHeaderViewHolder(View itemView) {
			super(itemView);
		}

		@Override
		public boolean isGhostHeader() {
			return true;
		}
	}


	public class FooterViewHolder extends ViewHolder {
		public FooterViewHolder(View itemView) {
			super(itemView);
		}

		@Override
		public boolean isFooter() {
			return true;
		}
	}


	/**
	 * @return Number of sections
	 */
	public int getNumberOfSections() {
		return 0;
	}

	/**
	 * @param sectionIndex index of the section in question
	 * @return the number of items in the specified section
	 */
	public int getNumberOfItemsInSection(int sectionIndex) {
		return 0;
	}

	/**
	 * @param sectionIndex index of the section in question
	 * @return true if this section has a header
	 */
	public boolean doesSectionHaveHeader(int sectionIndex) {
		return false;
	}

	/**
	 * @param sectionIndex index of the section in question
	 * @return true if this section has a footer
	 */
	public boolean doesSectionHaveFooter(int sectionIndex) {
		return false;
	}

	/**
	 * Called when a ViewHolder is needed for a section item view
	 *
	 * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
	 * @return A new ItemViewHolder holding an item view
	 */
	public ItemViewHolder onCreateItemViewHolder(ViewGroup parent) {
		return null;
	}

	/**
	 * Called when a ViewHolder is needed for a section header view
	 *
	 * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
	 * @return A new HeaderViewHolder holding a header view
	 */
	public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
		return null;
	}

	/**
	 * Called when a ViewHolder is needed for a section footer view
	 *
	 * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
	 * @return A new FooterViewHolder holding a footer view
	 */
	public FooterViewHolder onCreateFooterViewHolder(ViewGroup parent) {
		return null;
	}

	/**
	 * Called when a ViewHolder is needed for a section ghost header view
	 *
	 * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
	 * @return A new GhostHeaderViewHolder holding a ghost header view
	 */
	public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
		View ghostView = new View(parent.getContext());
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		parent.addView(ghostView, layoutParams);
		return new GhostHeaderViewHolder(ghostView);
	}

	/**
	 * Called to display item data at particular position
	 *
	 * @param viewHolder   the view holder to update
	 * @param sectionIndex the index of the section containing the item
	 * @param itemIndex    the index of the item in the section where 0 is the first item
	 */
	public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex) {
	}

	/**
	 * Called to display header data for a particular section
	 *
	 * @param viewHolder   the view holder to update
	 * @param sectionIndex the index of the section containing the header to update
	 */
	public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int sectionIndex) {
	}

	/**
	 * Called to update the ghost header for a particular section. Note, most implementations will not need to ever touch the ghost header.
	 *
	 * @param viewHolder   the view holder to update
	 * @param sectionIndex the index of the section containing the ghost header to update
	 */
	public void onBindGhostHeaderViewHolder(GhostHeaderViewHolder viewHolder, int sectionIndex) {
	}

	/**
	 * Called to display footer data for a particular section
	 *
	 * @param viewHolder   the view holder to update
	 * @param sectionIndex the index of the section containing the footer to update
	 */
	public void onBindFooterViewHolder(FooterViewHolder viewHolder, int sectionIndex) {
	}

	/**
	 * Called when a sections header positioning approach changes. The position can be NATURAL, STICKY or TRAILING
	 *
	 * @param sectionIndex the sections [0...n)
	 * @param header       the header view
	 * @param oldPosition  the previous positioning of the header (NONE, NATURAL, STICKY or TRAILING)
	 * @param newPosition  the new positioning of the header (NATURAL, STICKY or TRAILING)
	 */
	public void onHeaderPositionChanged(int sectionIndex, View header, HeaderPosition oldPosition, HeaderPosition newPosition) {
	}

	/**
	 * Given a "global" adapter adapterPosition, determine which sections contains that item
	 *
	 * @param position an adapter adapterPosition from 0 to getItemCount()-1
	 * @return the index of the sections containing that item
	 */
	public int getSectionForAdapterPosition(int position) {
		if (sections == null) {
			buildSectionIndex();
		}

		// TODO: Speed this up somehow
		int sectionIndex = 0;
		for (Section section : this.sections) {
			if (position >= section.adapterPosition && position < section.adapterPosition + section.length) {
				return sectionIndex;
			}
			sectionIndex++;
		}

		throw new IndexOutOfBoundsException("adapterPosition " + position + " does not correspond to items in adapter");
	}

	/**
	 * Given a sectionIndex and an adapter position get the local position of an item relative to the sectionIndex,
	 * where the first item has position 0
	 *
	 * @param sectionIndex    the sectionIndex index
	 * @param adapterPosition the adapter adapterPosition
	 * @return the position relative to the sectionIndex of an item in that sectionIndex
	 * <p/>
	 * Note, if the adapterPosition corresponds to a sectionIndex header, this will return -1
	 */
	public int getPositionOfItemInSection(int sectionIndex, int adapterPosition) {
		if (sections == null) {
			buildSectionIndex();
		}

		if (sectionIndex < 0) {
			throw new IndexOutOfBoundsException("sectionIndex " + sectionIndex + " < 0");
		}

		if (sectionIndex >= sections.size()) {
			throw new IndexOutOfBoundsException("sectionIndex " + sectionIndex + " >= sections.size (" + sections.size() + ")");
		}

		Section section = this.sections.get(sectionIndex);
		int localPosition = adapterPosition - section.adapterPosition;
		if (localPosition > section.length) {
			throw new IndexOutOfBoundsException("adapterPosition: " + adapterPosition + " is beyond sectionIndex: " + sectionIndex + " length: " + section.length);
		}

		if (section.hasHeader) {
			// adjust for header and ghostHeader
			localPosition -= 2;
		}

		return localPosition;
	}

	/**
	 * Given a sectionIndex index, and an offset into the sectionIndex where 0 is the header, 1 is the ghostHeader, 2 is the first item in the sectionIndex, return the corresponding "global" adapter position
	 *
	 * @param sectionIndex      a sectionIndex index
	 * @param offsetIntoSection offset into sectionIndex where 0 is the header, 1 is the first item, etc
	 * @return the "global" adapter adapterPosition
	 */
	private int getAdapterPosition(int sectionIndex, int offsetIntoSection) {
		if (sections == null) {
			buildSectionIndex();
		}

		if (sectionIndex < 0) {
			throw new IndexOutOfBoundsException("sectionIndex " + sectionIndex + " < 0");
		}

		if (sectionIndex >= sections.size()) {
			throw new IndexOutOfBoundsException("sectionIndex " + sectionIndex + " >= sections.size (" + sections.size() + ")");
		}

		Section section = this.sections.get(sectionIndex);
		int adapterPosition = section.adapterPosition;
		return offsetIntoSection + adapterPosition;
	}

	/**
	 * Return the adapter position corresponding to the header of the provided section
	 *
	 * @param sectionIndex the index of the section
	 * @return adapter position of that section's header
	 */
	public int getAdapterPositionForSectionHeader(int sectionIndex) {
		if (doesSectionHaveHeader(sectionIndex)) {
			return getAdapterPosition(sectionIndex, 0);
		} else {
			throw new InvalidParameterException("Section " + sectionIndex + " has no header");
		}
	}

	/**
	 * Return the adapter position corresponding to the ghost header of the provided section
	 *
	 * @param sectionIndex the index of the section
	 * @return adapter position of that section's ghost header
	 */
	public int getAdapterPositionForSectionGhostHeader(int sectionIndex) {
		if (doesSectionHaveHeader(sectionIndex)) {
			return getAdapterPosition(sectionIndex, 1); // ghost header follows the header
		} else {
			throw new InvalidParameterException("Section " + sectionIndex + " has no header");
		}
	}

	/**
	 * Return the adapter position corresponding to a specific item in the section
	 *
	 * @param sectionIndex      the index of the section
	 * @param offsetIntoSection the offset of the item in the section where 0 would be the first item in the section
	 * @return adapter position of the item in the section
	 */

	public int getAdapterPositionForSectionItem(int sectionIndex, int offsetIntoSection) {
		if (doesSectionHaveHeader(sectionIndex)) {
			return getAdapterPosition(sectionIndex, offsetIntoSection) + 2; // header is at position 0, ghostHeader at position 1
		} else {
			return getAdapterPosition(sectionIndex, offsetIntoSection);
		}
	}

	/**
	 * Return the adapter position corresponding to the footer of the provided section
	 *
	 * @param sectionIndex the index of the section
	 * @return adapter position of that section's footer
	 */
	public int getAdapterPositionForSectionFooter(int sectionIndex) {
		if (doesSectionHaveFooter(sectionIndex)) {
			Section section = this.sections.get(sectionIndex);
			int adapterPosition = section.adapterPosition;
			return adapterPosition + section.length - 1;
		} else {
			throw new InvalidParameterException("Section " + sectionIndex + " has no footer");
		}
	}

	/**
	 * Notify that all data in the list is invalid and the entire list should be reloaded.
	 * Equivalent to RecyclerView.Adapter.notifyDataSetChanged.
	 * Never directly call notifyDataSetChanged.
	 */
	public void notifyAllSectionsDataSetChanged() {
		if (sections == null) {
			buildSectionIndex();
		}
		notifyDataSetChanged();
	}

	/**
	 * Notify that all the items in a particular section are invalid and that section should be reloaded
	 * Never directly call notifyDataSetChanged.
	 *
	 * @param sectionIndex index of the section to reload.
	 */
	public void notifySectionDataSetChanged(int sectionIndex) {
		if (sections == null) {
			buildSectionIndex();
			notifyAllSectionsDataSetChanged();
		} else {
			buildSectionIndex();
			Section section = this.sections.get(sectionIndex);
			notifyItemRangeChanged(section.adapterPosition, section.length);
		}
	}

	/**
	 * Notify that a particular itemIndex in a section has been invalidated and must be reloaded
	 * Never directly call notifyItemChanged
	 *
	 * @param sectionIndex the index of the section containing the itemIndex
	 * @param itemIndex    the index of the item relative to the section (where 0 is the first item in the section)
	 */
	public void notifySectionItemChanged(int sectionIndex, int itemIndex) {
		if (sections == null) {
			buildSectionIndex();
			notifyAllSectionsDataSetChanged();
		} else {
			buildSectionIndex();
			Section section = this.sections.get(sectionIndex);
			if (itemIndex >= section.numberOfItems) {
				throw new IndexOutOfBoundsException("itemIndex adapterPosition: " + itemIndex + " exceeds sectionIndex numberOfItems: " + section.numberOfItems);
			}
			if (section.hasHeader) {
				itemIndex += 2;
			}
			notifyItemChanged(section.adapterPosition + itemIndex);
		}
	}

	/**
	 * Notify that an item has been added to a section
	 * Never directly call notifyItemInserted
	 *
	 * @param sectionIndex index of the section
	 * @param itemIndex    index of the item where 0 is the first position in the section
	 */
	public void notifySectionItemInserted(int sectionIndex, int itemIndex) {
		if (sections == null) {
			buildSectionIndex();
			notifyAllSectionsDataSetChanged();
		} else {
			buildSectionIndex();
			Section section = this.sections.get(sectionIndex);
			if (section.hasHeader) {
				itemIndex += 2;
			}
			notifyItemInserted(section.adapterPosition + itemIndex);
		}
	}

	/**
	 * Notify that an item has been removed from a section
	 * Never directly call notifyItemRemoved
	 *
	 * @param sectionIndex index of the section
	 * @param itemIndex    index of the item in the section where 0 is the first position in the section
	 */
	public void notifySectionItemRemoved(int sectionIndex, int itemIndex) {
		if (sections == null) {
			buildSectionIndex();
			notifyAllSectionsDataSetChanged();
		} else {
			buildSectionIndex();
			Section section = this.sections.get(sectionIndex);
			if (section.hasHeader) {
				itemIndex += 2;
			}
			notifyItemRemoved(section.adapterPosition + itemIndex);
		}
	}

	/**
	 * Notify that a new section has been added
	 *
	 * @param sectionIndex position of the new section
	 */
	public void notifySectionInserted(int sectionIndex) {
		if (sections == null) {
			buildSectionIndex();
			notifyAllSectionsDataSetChanged();
		} else {
			buildSectionIndex();
			Section section = this.sections.get(sectionIndex);
			notifyItemRangeInserted(section.adapterPosition, section.length);
		}
	}

	/**
	 * Notify that a section has been removed
	 *
	 * @param sectionIndex position of the removed section
	 */
	public void notifySectionRemoved(int sectionIndex) {
		if (sections == null) {
			buildSectionIndex();
			notifyAllSectionsDataSetChanged();
		} else {
			Section section = this.sections.get(sectionIndex);
			buildSectionIndex();
			notifyItemRangeRemoved(section.adapterPosition, section.length);
		}
	}

	private void buildSectionIndex() {
		sections = new ArrayList<>();

		int i = 0;
		for (int s = 0, ns = getNumberOfSections(); s < ns; s++) {
			Section section = new Section();
			section.adapterPosition = i;
			section.hasHeader = doesSectionHaveHeader(s);
			section.hasFooter = doesSectionHaveFooter(s);
			section.length = section.numberOfItems = getNumberOfItemsInSection(s);
			if (section.hasHeader) {
				section.length += 2; // room for header and ghostHeader
			}
			if (section.hasFooter) {
				section.length++;
			}
			this.sections.add(section);

			Log.i(TAG, "buildSectionIndex: s:" + s + " adapterPosition: " + section.adapterPosition + " header: " + section.hasHeader + " footer: " + section.hasFooter + " numberOfItems: " + section.numberOfItems + " length: " + section.length);

			i += section.length;
		}

		totalNumberOfItems = i;
		Log.i(TAG, "buildSectionIndex: totalNumberOfItems:" + totalNumberOfItems);
	}

	void setHeaderPosition(int sectionIndex, View headerView, HeaderPosition newHeaderPosition) {
		if (headerPositionsBySection.containsKey(sectionIndex)) {
			HeaderPosition currentHeaderPosition = headerPositionsBySection.get(sectionIndex);
			if (currentHeaderPosition != newHeaderPosition) {
				headerPositionsBySection.put(sectionIndex, newHeaderPosition);
				onHeaderPositionChanged(sectionIndex, headerView, currentHeaderPosition, newHeaderPosition);
			}
		} else {
			headerPositionsBySection.put(sectionIndex, newHeaderPosition);
			onHeaderPositionChanged(sectionIndex, headerView, HeaderPosition.NONE, newHeaderPosition);
		}
	}

	@Override
	public int getItemCount() {
		if (sections == null) {
			buildSectionIndex();
		}
		return totalNumberOfItems;
	}

	@Override
	public int getItemViewType(int adapterPosition) {
		if (sections == null) {
			buildSectionIndex();
		}

		if (adapterPosition < 0) {
			throw new IndexOutOfBoundsException("adapterPosition cannot be < 0");
		} else if (adapterPosition >= getItemCount()) {
			throw new IndexOutOfBoundsException("adapterPosition cannot be > getItemCount() (" + getItemCount() + ")");
		}

		int sectionIndex = getSectionForAdapterPosition(adapterPosition);
		Section section = this.sections.get(sectionIndex);
		int localPosition = adapterPosition - section.adapterPosition;

		if (section.hasHeader && section.hasFooter) {
			if (localPosition == 0) {
				return TYPE_HEADER;
			} else if (localPosition == 1) {
				return TYPE_GHOST_HEADER;
			} else if (localPosition == section.length - 1) {
				return TYPE_FOOTER;
			} else {
				return TYPE_ITEM;
			}
		} else if (section.hasHeader) {
			if (localPosition == 0) {
				return TYPE_HEADER;
			} else if (localPosition == 1) {
				return TYPE_GHOST_HEADER;
			} else {
				return TYPE_ITEM;
			}
		} else if (section.hasFooter) {
			if (localPosition == section.length - 1) {
				return TYPE_FOOTER;
			} else {
				return TYPE_ITEM;
			}
		} else {
			// this sections has no header or footer
			return TYPE_ITEM;
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		switch (viewType) {
			case TYPE_ITEM:
				return onCreateItemViewHolder(parent);
			case TYPE_HEADER:
				return onCreateHeaderViewHolder(parent);
			case TYPE_FOOTER:
				return onCreateFooterViewHolder(parent);
			case TYPE_GHOST_HEADER:
				return onCreateGhostHeaderViewHolder(parent);
		}

		throw new IndexOutOfBoundsException("unrecognized viewType: " + viewType + " does not correspond to TYPE_ITEM, TYPE_HEADER or TYPE_FOOTER");
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int adapterPosition) {
		int section = getSectionForAdapterPosition(adapterPosition);

		// bind the sections to this view holder
		holder.setSection(section);
		holder.setNumberOfItemsInSection(getNumberOfItemsInSection(section));

		switch (holder.getItemViewType()) {
			case TYPE_HEADER:
				onBindHeaderViewHolder((HeaderViewHolder) holder, section);
				break;

			case TYPE_ITEM:
				ItemViewHolder ivh = (ItemViewHolder) holder;
				int positionInSection = getPositionOfItemInSection(section, adapterPosition);
				ivh.setPositionInSection(positionInSection);
				onBindItemViewHolder(ivh, section, positionInSection);
				break;

			case TYPE_FOOTER:
				onBindFooterViewHolder((FooterViewHolder) holder, section);
				break;

			case TYPE_GHOST_HEADER:
				onBindGhostHeaderViewHolder((GhostHeaderViewHolder) holder, section);
				break;

			default:
				throw new IndexOutOfBoundsException("unrecognized viewType: " + holder.getItemViewType() + " does not correspond to TYPE_ITEM, TYPE_HEADER or TYPE_FOOTER");
		}
	}
}
