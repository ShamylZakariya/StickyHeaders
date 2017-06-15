package org.zakariya.stickyheadersapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheadersapp.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by shamyl on 7/11/16.
 */
public class MultiTypeDemoAdapter extends SectioningAdapter {

	static final String TAG = MultiTypeDemoAdapter.class.getSimpleName();

	static final int USER_HEADER_TYPE_0 = 0;
	static final int USER_HEADER_TYPE_1 = 1;

	static final int USER_ITEM_TYPE_0 = 0;
	static final int USER_ITEM_TYPE_1 = 1;

	static final int USER_FOOTER_TYPE_0 = 0;
	static final int USER_FOOTER_TYPE_1 = 1;

	class Item {
		int type;
		String title;

		public Item(int type, String title) {
			this.type = type;
			this.title = title;
		}

		public int getType() {
			return type;
		}

		public String getTitle() {
			return title;
		}
	}

	class Footer {
		int type;
		String title;

		public Footer(int type, String title) {
			this.type = type;
			this.title = title;
		}

		public int getType() {
			return type;
		}

		public String getTitle() {
			return title;
		}
	}

	class Section {
		int type;
		String title;
		ArrayList<Item> items = new ArrayList<>();
		Footer footer;

		public Section(int type, String title) {
			this.type = type;
			this.title = title;
		}

		public int getType() {
			return type;
		}

		public String getTitle() {
			return title;
		}

		public ArrayList<Item> getItems() {
			return items;
		}

		public Footer getFooter() {
			return footer;
		}
	}

	///////////////

	public class ItemViewHolder0 extends SectioningAdapter.ItemViewHolder {
		TextView textView;

		public ItemViewHolder0(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
		}
	}

	public class HeaderViewHolder0 extends SectioningAdapter.HeaderViewHolder {
		TextView textView;

		public HeaderViewHolder0(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
		}
	}

	public class FooterViewHolder0 extends SectioningAdapter.FooterViewHolder {
		TextView textView;

		public FooterViewHolder0(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
		}
	}

	///////////////

	public class ItemViewHolder1 extends SectioningAdapter.ItemViewHolder {
		TextView textView;

		public ItemViewHolder1(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
		}
	}

	public class HeaderViewHolder1 extends SectioningAdapter.HeaderViewHolder {
		TextView textView;

		public HeaderViewHolder1(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
		}
	}

	public class FooterViewHolder1 extends SectioningAdapter.FooterViewHolder {
		TextView textView;

		public FooterViewHolder1(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.textView);
		}
	}


	Random rng;
	ArrayList<Section> sections;

	public MultiTypeDemoAdapter(int numSections, int numItemsPerSection) {

		rng = new Random();
		sections = new ArrayList<>();
		for (int s = 0; s < numSections; s++) {
			int sectionType = s % 2;
			Section section = new Section(sectionType, "Section: " + Integer.toString(s));
			for (int i = 0; i < numItemsPerSection; i++) {
				section.items.add(new Item(sectionType, "Item: " + Integer.toString(i)));
			}
			section.footer = new Footer(sectionType, "Footer for section: " + s);
			sections.add(section);
		}
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
		return true;
	}

	@Override
	public boolean doesSectionHaveFooter(int sectionIndex) {
		return true;
	}

	@Override
	public int getSectionHeaderUserType(int sectionIndex) {
		return sections.get(sectionIndex).getType();
	}

	@Override
	public int getSectionItemUserType(int sectionIndex, int itemIndex) {
		Section section = sections.get(sectionIndex);
		return section.items.get(itemIndex).getType();
	}

	@Override
	public int getSectionFooterUserType(int sectionIndex) {
		return sections.get(sectionIndex).getFooter().getType();
	}

	@Override
	public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		switch (itemType) {
			case USER_ITEM_TYPE_0:
				return new ItemViewHolder0(inflater.inflate(R.layout.list_item_multi_0, parent, false));

			case USER_ITEM_TYPE_1:
				return new ItemViewHolder1(inflater.inflate(R.layout.list_item_multi_1, parent, false));
		}

		throw new IllegalArgumentException("Unrecognized itemType: " + itemType);
	}

	@Override
	public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		switch (headerType) {
			case USER_HEADER_TYPE_0:
				return new HeaderViewHolder0(inflater.inflate(R.layout.list_item_multi_header_0, parent, false));

			case USER_HEADER_TYPE_1:
				return new HeaderViewHolder1(inflater.inflate(R.layout.list_item_multi_header_1, parent, false));
		}

		throw new IllegalArgumentException("Unrecognized headerType: " + headerType);
	}

	@Override
	public FooterViewHolder onCreateFooterViewHolder(ViewGroup parent, int footerType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		switch (footerType) {
			case USER_FOOTER_TYPE_0:
				return new FooterViewHolder0(inflater.inflate(R.layout.list_item_multi_footer_0, parent, false));

			case USER_FOOTER_TYPE_1:
				return new FooterViewHolder1(inflater.inflate(R.layout.list_item_multi_footer_1, parent, false));
		}

		throw new IllegalArgumentException("Unrecognized footerType: " + footerType);
	}

	@Override
	public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
		Section s = sections.get(sectionIndex);

		switch (itemType) {
			case USER_ITEM_TYPE_0: {
				ItemViewHolder0 ivh = (ItemViewHolder0) viewHolder;
				ivh.textView.setText(s.items.get(itemIndex).getTitle());
				break;
			}
			case USER_ITEM_TYPE_1: {
				ItemViewHolder1 ivh = (ItemViewHolder1) viewHolder;
				ivh.textView.setText(s.items.get(itemIndex).getTitle());
				break;
			}

			default:
				throw new IllegalArgumentException("Unrecognized itemType: " + itemType);
		}

	}

	@Override
	public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
		Section s = sections.get(sectionIndex);

		switch (headerType) {
			case USER_HEADER_TYPE_0: {
				HeaderViewHolder0 hvh = (HeaderViewHolder0) viewHolder;
				hvh.textView.setText(s.getTitle());
				break;
			}
			case USER_HEADER_TYPE_1: {
				HeaderViewHolder1 hvh = (HeaderViewHolder1) viewHolder;
				hvh.textView.setText(s.getTitle());
				break;
			}

			default:
				throw new IllegalArgumentException("Unrecognized headerType: " + headerType);
		}

	}

	@Override
	public void onBindFooterViewHolder(SectioningAdapter.FooterViewHolder viewHolder, int sectionIndex, int footerType) {
		Section s = sections.get(sectionIndex);

		switch (footerType) {
			case USER_FOOTER_TYPE_0: {
				FooterViewHolder0 fvh = (FooterViewHolder0) viewHolder;
				fvh.textView.setText(s.footer.getTitle());
				break;
			}
			case USER_FOOTER_TYPE_1: {
				FooterViewHolder1 fvh = (FooterViewHolder1) viewHolder;
				fvh.textView.setText(s.footer.getTitle());
				break;
			}

			default:
				throw new IllegalArgumentException("Unrecognized footerType: " + footerType);
		}

	}

}
