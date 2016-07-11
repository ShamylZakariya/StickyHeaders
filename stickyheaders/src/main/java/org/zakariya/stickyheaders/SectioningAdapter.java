package org.zakariya.stickyheaders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * SectioningAdapter
 * Represents a list of sections, each containing a list of items and optionally a header and or footer item.
 * SectioningAdapter may be used with a normal RecyclerView.LinearLayoutManager but is meant for use with
 * StickyHeaderLayoutManager to allow for sticky positioning of header items.
 * <p/>
 * When invalidating the adapter's contents NEVER use RecyclerView.Adapter.notify* methods. These methods
 * aren't aware of the section information and internal state of SectioningAdapter. As such, please
 * use the SectioningAdapter.notify* methods.
 * <p/>
 * SectioningAdapter manages four types of items: TYPE_HEADER, TYPE_ITEM, TYPE_FOOTER and TYPE_GHOST_HEADER.
 * Headers are the optional first item in a section. A section then has some number of items in it,
 * and an optional footer. The ghost header is a special item used for layout mechanics. It can
 * be ignored by SectioningAdapter subclasses - but it is made externally accessible just in case.
 */
@SuppressWarnings("unused")
public class SectioningAdapter extends RecyclerView.Adapter<SectioningAdapter.ViewHolder> {

	private static final String TAG = "SectioningAdapter";

	public static final int TYPE_HEADER = 0;
	public static final int TYPE_GHOST_HEADER = 1;
	public static final int TYPE_ITEM = 2;
	public static final int TYPE_FOOTER = 3;

	private static class Section {
		int adapterPosition;    // adapterPosition of first item (the header) of this sections
		int numberOfItems;      // number of items (not including header or footer)
		int length;             // total number of items in sections including header and footer
		boolean hasHeader;      // if true, sections has a header
		boolean hasFooter;      // if true, sections has a footer
	}

	private ArrayList<Section> sections;
	private HashMap<Integer,Boolean> collapsedSections = new HashMap<>();
	private int[] sectionIndicesByAdapterPosition;
	private int totalNumberOfItems;


	public static class ViewHolder extends RecyclerView.ViewHolder {
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

	public static class ItemViewHolder extends ViewHolder {
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

	public static class HeaderViewHolder extends ViewHolder {

		public HeaderViewHolder(View itemView) {
			super(itemView);
		}

		@Override
		public boolean isHeader() {
			return true;
		}
	}

	public static class GhostHeaderViewHolder extends ViewHolder {
		public GhostHeaderViewHolder(View itemView) {
			super(itemView);
		}

		@Override
		public boolean isGhostHeader() {
			return true;
		}
	}


	public static class FooterViewHolder extends ViewHolder {
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
	 * Given a "global" adapter adapterPosition, determine which sections contains that item
	 *
	 * @param adapterPosition an adapter adapterPosition from 0 to getItemCount()-1
	 * @return the index of the sections containing that item
	 */
	public int getSectionForAdapterPosition(int adapterPosition) {
		if (sections == null) {
			buildSectionIndex();
		}

		if (getItemCount() == 0) {
			return -1;
		}

		if (adapterPosition < 0 || adapterPosition >= getItemCount()) {
			throw new IndexOutOfBoundsException("adapterPosition " + adapterPosition + " is not in range of items represented by adapter");
		}

		return sectionIndicesByAdapterPosition[adapterPosition];
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
	 * Mark that a section is collapsed or not. By default sections are not collapsed and draw
	 * all their child items. By "collapsing" a section, the child items are hidden.
	 * @param sectionIndex index of section
	 * @param collapsed if true, section is collapsed, false, it's open
	 */
	public void setSectionIsCollapsed(int sectionIndex, boolean collapsed) {
		boolean notify = isSectionCollapsed(sectionIndex) != collapsed;

		collapsedSections.put(sectionIndex, collapsed);

		if(notify) {
			if(sections == null)
				buildSectionIndex();

			Section section = sections.get(sectionIndex);
			int number = section.numberOfItems;

			if(collapsed)
				notifySectionItemRangeRemoved(sectionIndex, 0, number);
			else
				notifySectionItemRangeInserted(sectionIndex, 0, number);
		}
	}

	public boolean isSectionCollapsed(int sectionIndex) {
		if(collapsedSections.containsKey(sectionIndex)) {
			return collapsedSections.get(sectionIndex);
		}

		return false;
	}

	/**
	 * Notify that all data in the list is invalid and the entire list should be reloaded.
	 * Equivalent to RecyclerView.Adapter.notifyDataSetChanged.
	 * Never directly call notifyDataSetChanged.
	 */
	public void notifyAllSectionsDataSetChanged() {
		buildSectionIndex();
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
	 * Notify that a range of items in a section has been inserted
	 * @param sectionIndex index of the section
	 * @param fromPosition index to start adding
	 * @param number amount of items inserted
     */
	public void notifySectionItemRangeInserted(int sectionIndex, int fromPosition, int number) {
		if (sections == null) {
			buildSectionIndex();
			notifyAllSectionsDataSetChanged();
		} else {
			buildSectionIndex();
			Section section = this.sections.get(sectionIndex);

			// 0 is a valid position to insert from
			if (fromPosition > section.numberOfItems) {
				throw new IndexOutOfBoundsException("itemIndex adapterPosition: " + fromPosition + " exceeds sectionIndex numberOfItems: " + section.numberOfItems);
			}

			if(section.hasHeader) {
				fromPosition += 2;
			}

			notifyItemRangeInserted(section.adapterPosition + fromPosition, number);
		}
	}

	/**
	 * Notify that a range of items in a section has been removed
	 * @param sectionIndex index of the section
	 * @param fromPosition index to start removing from
	 * @param number amount of items removed
	 */
	public void notifySectionItemRangeRemoved(int sectionIndex, int fromPosition, int number) {
		if (sections == null) {
			buildSectionIndex();
			notifyAllSectionsDataSetChanged();
		} else {
			buildSectionIndex();
			Section section = this.sections.get(sectionIndex);

			// 0 is a valid position to remove from
			if (fromPosition > section.numberOfItems) {
				throw new IndexOutOfBoundsException("itemIndex adapterPosition: " + fromPosition + " exceeds sectionIndex numberOfItems: " + section.numberOfItems);
			}

			// Verify we don't run off the end of the section
			if (fromPosition + number > section.numberOfItems) {
				throw new IndexOutOfBoundsException("itemIndex adapterPosition: " + fromPosition + number + " exceeds sectionIndex numberOfItems: " + section.numberOfItems);
			}

			if(section.hasHeader) {
				fromPosition += 2;
			}

			notifyItemRangeRemoved(section.adapterPosition + fromPosition, number);
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

			if (isSectionCollapsed(s)){
				section.length = 0;
				section.numberOfItems = getNumberOfItemsInSection(s);
			} else {
				section.length = section.numberOfItems = getNumberOfItemsInSection(s);
			}

			if (section.hasHeader) {
				section.length += 2; // room for header and ghostHeader
			}
			if (section.hasFooter) {
				section.length++;
			}

			this.sections.add(section);

			i += section.length;
		}

		totalNumberOfItems = i;

		i = 0;
		sectionIndicesByAdapterPosition = new int[totalNumberOfItems];
		for (int s = 0, ns = getNumberOfSections(); s < ns; s++) {
			Section section = sections.get(s);
			for (int p = 0; p < section.length; p++) {
				sectionIndicesByAdapterPosition[i+p] = s;
			}

			i += section.length;
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

		// tag the viewHolder's item so as to make it possible to track in layout manager
		tagViewHolderItemView(holder, section, adapterPosition);

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
				throw new IllegalArgumentException("unrecognized viewType: " + holder.getItemViewType() + " does not correspond to TYPE_ITEM, TYPE_HEADER, TYPE_GHOST_HEADER or TYPE_FOOTER");
		}
	}

	/**
	 * Tag the itemView of the view holder with information needed for the layout to do its sticky positioning.
	 * Specifically, it tags R.id.sectioning_adapter_tag_key_view_type to the item type, R.id.sectioning_adapter_tag_key_view_section
	 * to the item's section, and R.id.sectioning_adapter_tag_key_view_adapter_position which is the adapter position of the view
	 *
	 * @param holder          the view holder containing the itemView to tag
	 * @param section         the section index
	 * @param adapterPosition the adapter position of the view holder
	 */
	void tagViewHolderItemView(ViewHolder holder, int section, int adapterPosition) {
		View view = holder.itemView;
		view.setTag(R.id.sectioning_adapter_tag_key_view_viewholder, holder);
	}

}
