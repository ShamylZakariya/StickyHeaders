package org.zakariya.stickyheadersapp.adapters;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheadersapp.R;

import java.util.ArrayList;

/**
 * Created by shamyl on 7/26/16.
 */
public class SelectionDemoAdapter extends SectioningAdapter {

	private static final String TAG = "SelectionDemoAdapter";

	private class Section {
		int index;
		String header;
		String footer;
		ArrayList<String> items = new ArrayList<>();
	}

	public class ItemViewHolder extends SectioningAdapter.ItemViewHolder {
		TextView textView;
		TextView adapterPositionTextView;

		public ItemViewHolder(View itemView, boolean showAdapterPosition) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
			adapterPositionTextView = (TextView) itemView.findViewById(R.id.adapterPositionTextView);

			if (showAdapterPosition) {
				adapterPositionTextView.setVisibility(View.GONE);
			}
		}
	}

	public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
		TextView textView;
		TextView adapterPositionTextView;

		public HeaderViewHolder(View itemView, boolean showAdapterPosition) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
			adapterPositionTextView = (TextView) itemView.findViewById(R.id.adapterPositionTextView);

			if (showAdapterPosition) {
				adapterPositionTextView.setVisibility(View.INVISIBLE);
			}
		}
	}

	public class FooterViewHolder extends SectioningAdapter.FooterViewHolder {
		TextView textView;
		TextView adapterPositionTextView;

		public FooterViewHolder(View itemView, boolean showAdapterPosition) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
			adapterPositionTextView = (TextView) itemView.findViewById(R.id.adapterPositionTextView);

			if (showAdapterPosition) {
				adapterPositionTextView.setVisibility(View.GONE);
			}
		}
	}

	ArrayList<Section> sections = new ArrayList<>();
	boolean showAdapterPositions;


	public SelectionDemoAdapter(int numSections, int numItemsPerSection, boolean showAdapterPositions) {
		this.showAdapterPositions = showAdapterPositions;

		for (int i = 0; i < numSections; i++) {
			appendSection(i, numItemsPerSection);
		}
	}

	void appendSection(int index, int itemCount) {
		Section section = new Section();
		section.index = index;
		section.header = Integer.toString(index);
		section.footer = "End of section " + index;

		for (int j = 0; j < itemCount; j++) {
			section.items.add(index + "/" + j);
		}

		sections.add(section);
	}

	public void deleteSelection() {

		traverseSelection(new SelectionVisitor() {
			@Override
			public void onVisitSelectedSection(int sectionIndex) {
				Log.d(TAG, "onVisitSelectedSection() called with: " + "sectionIndex = [" + sectionIndex + "]");
			}

			@Override
			public void onVisitSelectedSectionItem(int sectionIndex, int itemIndex) {
				Log.d(TAG, "onVisitSelectedSectionItem() called with: " + "sectionIndex = [" + sectionIndex + "], itemIndex = [" + itemIndex + "]");
			}

			@Override
			public void onVisitSelectedFooter(int sectionIndex) {
				Log.d(TAG, "onVisitSelectedFooter() called with: " + "sectionIndex = [" + sectionIndex + "]");
			}
		});

	}

	public void duplicateSelection() {

	}

	@Override
	public int getNumberOfSections() {
		return sections.size();
	}

	@Override
	public int getNumberOfItemsInSection(int sectionIndex) {
		return sections.get(sectionIndex).items.size();
	}

	@Override
	public boolean doesSectionHaveHeader(int sectionIndex) {
		return !TextUtils.isEmpty(sections.get(sectionIndex).header);
	}

	@Override
	public boolean doesSectionHaveFooter(int sectionIndex) {
		return !TextUtils.isEmpty(sections.get(sectionIndex).footer);
	}

	@Override
	public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View v = inflater.inflate(R.layout.list_item_selectable_item, parent, false);
		return new ItemViewHolder(v, showAdapterPositions);
	}

	@Override
	public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View v = inflater.inflate(R.layout.list_item_selectable_header, parent, false);
		return new HeaderViewHolder(v, showAdapterPositions);
	}

	@Override
	public FooterViewHolder onCreateFooterViewHolder(ViewGroup parent, int footerType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View v = inflater.inflate(R.layout.list_item_selectable_footer, parent, false);
		return new FooterViewHolder(v, showAdapterPositions);
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
		Section s = sections.get(sectionIndex);
		ItemViewHolder ivh = (ItemViewHolder) viewHolder;
		ivh.textView.setText(s.items.get(itemIndex));
		ivh.adapterPositionTextView.setText(Integer.toString(getAdapterPositionForSectionItem(sectionIndex, itemIndex)));

		ivh.itemView.setActivated(isSectionItemSelected(sectionIndex, itemIndex));

	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
		Section s = sections.get(sectionIndex);
		HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;

		hvh.textView.setText(s.header);
		hvh.adapterPositionTextView.setText(Integer.toString(getAdapterPositionForSectionHeader(sectionIndex)));

		hvh.itemView.setActivated(isSectionSelected(sectionIndex));

	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindFooterViewHolder(SectioningAdapter.FooterViewHolder viewHolder, int sectionIndex, int footerType) {
		Section s = sections.get(sectionIndex);
		FooterViewHolder fvh = (FooterViewHolder) viewHolder;
		fvh.textView.setText(s.footer);
		fvh.adapterPositionTextView.setText(Integer.toString(getAdapterPositionForSectionFooter(sectionIndex)));

		fvh.itemView.setActivated(isSectionFooterSelected(sectionIndex));
	}
}
