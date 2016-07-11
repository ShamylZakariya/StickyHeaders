package org.zakariya.stickyheadersapp.adapters;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheadersapp.R;
import org.zakariya.stickyheadersapp.api.EndlessDemoMockLoader;

import java.util.ArrayList;

public class EndlessDemoAdapter extends SectioningAdapter {

	private static final boolean USE_DEBUG_APPEARANCE = false;

	public class ItemViewHolder extends SectioningAdapter.ItemViewHolder {
		TextView textView;
		TextView adapterPositionTextView;
		ImageButton cloneButton;
		ImageButton deleteButton;

		public ItemViewHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
			adapterPositionTextView = (TextView) itemView.findViewById(R.id.adapterPositionTextView);
			cloneButton = (ImageButton) itemView.findViewById(R.id.cloneButton);
			deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);

			cloneButton.setVisibility(View.GONE);
			deleteButton.setVisibility(View.GONE);
			adapterPositionTextView.setVisibility(View.VISIBLE);
		}
	}

	public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
		TextView textView;
		TextView adapterPositionTextView;
		ImageButton cloneButton;
		ImageButton deleteButton;
		ImageButton collapseButton;

		public HeaderViewHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
			adapterPositionTextView = (TextView) itemView.findViewById(R.id.adapterPositionTextView);

			cloneButton = (ImageButton) itemView.findViewById(R.id.cloneButton);
			deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);
			collapseButton = (ImageButton) itemView.findViewById(R.id.collapseButton);

			cloneButton.setVisibility(View.GONE);
			deleteButton.setVisibility(View.GONE);
			collapseButton.setVisibility(View.GONE);
		}
	}

	ArrayList<EndlessDemoMockLoader.SectionModel> sections = new ArrayList<>();

	public EndlessDemoAdapter() {
	}

	public void addSection(EndlessDemoMockLoader.SectionModel section) {
		sections.add(section);
		notifySectionInserted(sections.size()-1);
	}

	@Override
	public int getNumberOfSections() {
		return sections.size();
	}

	@Override
	public int getNumberOfItemsInSection(int sectionIndex) {
		return sections.get(sectionIndex).getItems().size();
	}

	@Override
	public boolean doesSectionHaveHeader(int sectionIndex) {
		return !TextUtils.isEmpty(sections.get(sectionIndex).getTitle());
	}

	@Override
	public boolean doesSectionHaveFooter(int sectionIndex) {
		return false;
	}

	@Override
	public ItemViewHolder onCreateItemViewHolder(ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View v = inflater.inflate(R.layout.list_item_simple_item, parent, false);
		return new ItemViewHolder(v);
	}

	@Override
	public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View v = inflater.inflate(R.layout.list_item_simple_header, parent, false);
		return new HeaderViewHolder(v);
	}

	@Override
	public FooterViewHolder onCreateFooterViewHolder(ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View v = inflater.inflate(R.layout.list_item_simple_footer, parent, false);
		return new FooterViewHolder(v);
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex) {
		EndlessDemoMockLoader.SectionModel s = sections.get(sectionIndex);
		ItemViewHolder ivh = (ItemViewHolder) viewHolder;
		ivh.textView.setText(s.getItems().get(itemIndex).getTitle());
		ivh.adapterPositionTextView.setText(Integer.toString(getAdapterPositionForSectionItem(sectionIndex, itemIndex)));
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex) {
		EndlessDemoMockLoader.SectionModel s = sections.get(sectionIndex);
		HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;
		hvh.adapterPositionTextView.setText(Integer.toString(getAdapterPositionForSectionHeader(sectionIndex)));

		if (USE_DEBUG_APPEARANCE) {
			hvh.textView.setText(pad(sectionIndex * 2) + s.getTitle());
			viewHolder.itemView.setBackgroundColor(0x55FF9999);
		} else {
			hvh.textView.setText(s.getTitle());
		}
	}

	private String pad(int spaces) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < spaces; i++) {
			b.append(' ');
		}
		return b.toString();
	}
}
