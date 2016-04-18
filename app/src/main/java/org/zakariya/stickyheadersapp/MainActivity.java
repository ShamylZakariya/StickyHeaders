package org.zakariya.stickyheadersapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";
	private static final boolean useStickyHeadersLayoutManager = true;
	private static final boolean useDebugColoration = false;

	RecyclerView recyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

		if (useStickyHeadersLayoutManager) {
			StickyHeaderLayoutManager stickyHeaderLayoutManager = new StickyHeaderLayoutManager(this);
			recyclerView.setLayoutManager(stickyHeaderLayoutManager);

			// set a header position callback to set elevation on sticky headers, because why not
			stickyHeaderLayoutManager.setHeaderPositionChangedCallback(new StickyHeaderLayoutManager.HeaderPositionChangedCallback() {
				@Override
				public void onHeaderPositionChanged(int sectionIndex, View header, StickyHeaderLayoutManager.HeaderPosition oldPosition, StickyHeaderLayoutManager.HeaderPosition newPosition) {
					Log.i(TAG, "onHeaderPositionChanged: section: " + sectionIndex + " -> old: " + oldPosition.name() + " new: " + newPosition.name());
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						boolean elevated = newPosition == StickyHeaderLayoutManager.HeaderPosition.STICKY;
						header.setElevation(elevated ? 8 : 0);
					}
				}
			});

		} else {
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
		}

		recyclerView.setAdapter(new DemoAdapter(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.reloadMenuItem:
				((SectioningAdapter) recyclerView.getAdapter()).notifyAllSectionsDataSetChanged();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private static final class Section {
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

	public class DemoAdapter extends SectioningAdapter {

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
			}

			@Override
			public void onClick(View v) {
				int adapterPosition = getAdapterPosition();
				final int section = DemoAdapter.this.getSectionForAdapterPosition(adapterPosition);
				final int item = DemoAdapter.this.getPositionOfItemInSection(section, adapterPosition);
				if (v == cloneButton) {
					DemoAdapter.this.onCloneItem(section, item);
				} else if (v == deleteButton) {
					DemoAdapter.this.onDeleteItem(section, item);
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
			}

			@Override
			public void onClick(View v) {
				int position = getAdapterPosition();
				final int section = DemoAdapter.this.getSectionForAdapterPosition(position);
				if (v == cloneButton) {
					DemoAdapter.this.onCloneSection(section);
				} else if (v == deleteButton) {
					DemoAdapter.this.onDeleteSection(section);
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
			}
		}


		Context context;
		ArrayList<Section> sections = new ArrayList<>();

		public DemoAdapter(Context context) {
			this.context = context;

			for (int i = 0; i < 25; i++) {
				appendSection(i, 25);
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
			View v = inflater.inflate(R.layout.list_item, parent, false);
			return new ItemViewHolder(v);
		}

		@Override
		public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			View v = inflater.inflate(R.layout.list_item_header, parent, false);
			return new HeaderViewHolder(v);
		}

		@Override
		public FooterViewHolder onCreateFooterViewHolder(ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			View v = inflater.inflate(R.layout.list_item_footer, parent, false);
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
			hvh.textView.setText(s.header);
			hvh.adapterPositionTextView.setText(Integer.toString(getAdapterPositionForSectionHeader(sectionIndex)));

			if (useDebugColoration) {
				viewHolder.itemView.setBackgroundColor(0x55FF9999);
			}
		}

		@Override
		public void onBindGhostHeaderViewHolder(SectioningAdapter.GhostHeaderViewHolder viewHolder, int sectionIndex) {
			if (useDebugColoration) {
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

	}
}
