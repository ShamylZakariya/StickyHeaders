package org.zakariya.stickyheadersapp.adapters;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheadersapp.R;

import java.util.ArrayList;

/**
 * SimpleDemoAdapter, just shows demo data
 */
public class SimpleDemoAdapter extends SectioningAdapter {

	static final String TAG = SimpleDemoAdapter.class.getSimpleName();
	static final boolean USE_DEBUG_APPEARANCE = false;
	static final boolean SHOW_DEBUG_CONTROLS = false;

	private class Section {
		int index;
		int copyCount;
		String header;
		String footer;
		ArrayList<String> items = new ArrayList<>();

		public Section duplicate() {
			Section c = new Section();
			c.index = this.index;
			c.copyCount = this.copyCount + 1;
			c.header = c.index + " copy " + c.copyCount;
			c.footer = this.footer;
			for (String i : this.items) {
				c.items.add(i + " copy " + c.copyCount);
			}

			return c;
		}

		public void duplicateItem(int item) {
			String itemCopy = items.get(item) + " copy";
			items.add(item + 1, itemCopy);
		}

	}

	public class ItemViewHolder extends SectioningAdapter.ItemViewHolder implements View.OnClickListener {
		TextView textView;
		TextView adapterPositionTextView;
		ImageButton cloneButton;
		ImageButton deleteButton;

		public ItemViewHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
			adapterPositionTextView = (TextView) itemView.findViewById(R.id.adapterPositionTextView);
			cloneButton = (ImageButton) itemView.findViewById(R.id.cloneImageButton);
			cloneButton.setOnClickListener(this);
			deleteButton = (ImageButton) itemView.findViewById(R.id.deleteImageButton);
			deleteButton.setOnClickListener(this);

			if (!SHOW_DEBUG_CONTROLS) {
				cloneButton.setVisibility(View.INVISIBLE);
				deleteButton.setVisibility(View.INVISIBLE);
				adapterPositionTextView.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void onClick(View v) {
			int adapterPosition = getAdapterPosition();
			final int section = SimpleDemoAdapter.this.getSectionForAdapterPosition(adapterPosition);
			final int item = SimpleDemoAdapter.this.getPositionOfItemInSection(section, adapterPosition);
			if (v == cloneButton) {
				SimpleDemoAdapter.this.onCloneItem(section, item);
			} else if (v == deleteButton) {
				SimpleDemoAdapter.this.onDeleteItem(section, item);
			}
		}
	}

	public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder implements View.OnClickListener {
		TextView textView;
		TextView adapterPositionTextView;
		ImageButton cloneButton;
		ImageButton deleteButton;

		public HeaderViewHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
			adapterPositionTextView = (TextView) itemView.findViewById(R.id.adapterPositionTextView);
			cloneButton = (ImageButton) itemView.findViewById(R.id.cloneImageButton);
			cloneButton.setOnClickListener(this);
			deleteButton = (ImageButton) itemView.findViewById(R.id.deleteImageButton);
			deleteButton.setOnClickListener(this);

			if (!SHOW_DEBUG_CONTROLS) {
				cloneButton.setVisibility(View.INVISIBLE);
				deleteButton.setVisibility(View.INVISIBLE);
				adapterPositionTextView.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void onClick(View v) {
			int position = getAdapterPosition();
			final int section = SimpleDemoAdapter.this.getSectionForAdapterPosition(position);
			if (v == cloneButton) {
				SimpleDemoAdapter.this.onCloneSection(section);
			} else if (v == deleteButton) {
				SimpleDemoAdapter.this.onDeleteSection(section);
			}
		}
	}

	public class FooterViewHolder extends SectioningAdapter.FooterViewHolder {
		TextView textView;
		TextView adapterPositionTextView;

		public FooterViewHolder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
			adapterPositionTextView = (TextView) itemView.findViewById(R.id.adapterPositionTextView);

			if (!SHOW_DEBUG_CONTROLS) {
				adapterPositionTextView.setVisibility(View.INVISIBLE);
			}
		}
	}


	ArrayList<Section> sections = new ArrayList<>();

	public SimpleDemoAdapter(int numSections, int numItemsPerSection) {
		for (int i = 0; i < numSections; i++) {
			appendSection(i, numItemsPerSection);
		}
	}

	void appendSection(int index, int itemCount) {
		Section section = new Section();
		section.index = index;
		section.copyCount = 0;
		section.header = Integer.toString(index);
		section.footer = "End of section " + index;

		for (int j = 0; j < itemCount; j++) {
			section.items.add(index + "/" + j);
		}

		sections.add(section);
	}

	void onDeleteSection(int sectionIndex) {
		Log.d(TAG, "onDeleteSection() called with: " + "sectionIndex = [" + sectionIndex + "]");
		sections.remove(sectionIndex);
		notifySectionRemoved(sectionIndex);
	}

	void onCloneSection(int sectionIndex) {
		Log.d(TAG, "onCloneSection() called with: " + "sectionIndex = [" + sectionIndex + "]");

		Section s = sections.get(sectionIndex);
		Section d = s.duplicate();
		sections.add(sectionIndex + 1, d);
		notifySectionInserted(sectionIndex + 1);
	}

	void onDeleteItem(int sectionIndex, int itemIndex) {
		Log.d(TAG, "onDeleteItem() called with: " + "sectionIndex = [" + sectionIndex + "], itemIndex = [" + itemIndex + "]");
		Section s = sections.get(sectionIndex);
		s.items.remove(itemIndex);
		notifySectionItemRemoved(sectionIndex, itemIndex);
	}

	void onCloneItem(int sectionIndex, int itemIndex) {
		Log.d(TAG, "onCloneItem() called with: " + "sectionIndex = [" + sectionIndex + "], itemIndex = [" + itemIndex + "]");
		Section s = sections.get(sectionIndex);
		s.duplicateItem(itemIndex);
		notifySectionItemInserted(sectionIndex, itemIndex + 1);
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
		Section s = sections.get(sectionIndex);
		ItemViewHolder ivh = (ItemViewHolder) viewHolder;
		ivh.textView.setText(s.items.get(itemIndex));
		ivh.adapterPositionTextView.setText(Integer.toString(getAdapterPositionForSectionItem(sectionIndex, itemIndex)));
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex) {
		Section s = sections.get(sectionIndex);
		HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;
		hvh.adapterPositionTextView.setText(Integer.toString(getAdapterPositionForSectionHeader(sectionIndex)));

		if (USE_DEBUG_APPEARANCE) {
			hvh.textView.setText(pad(sectionIndex * 2) + s.header);
			viewHolder.itemView.setBackgroundColor(0x55FF9999);
		} else {
			hvh.textView.setText(s.header);
		}
	}

	@Override
	public void onBindGhostHeaderViewHolder(SectioningAdapter.GhostHeaderViewHolder viewHolder, int sectionIndex) {
		if (USE_DEBUG_APPEARANCE) {
			viewHolder.itemView.setBackgroundColor(0xFF9999FF);
		}
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindFooterViewHolder(SectioningAdapter.FooterViewHolder viewHolder, int sectionIndex) {
		Section s = sections.get(sectionIndex);
		FooterViewHolder fvh = (FooterViewHolder) viewHolder;
		fvh.textView.setText(s.footer);
		fvh.adapterPositionTextView.setText(Integer.toString(getAdapterPositionForSectionFooter(sectionIndex)));
	}

	private String pad(int spaces) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < spaces; i++) {
			b.append(' ');
		}
		return b.toString();
	}

}
