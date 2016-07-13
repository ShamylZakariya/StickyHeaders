package org.zakariya.stickyheadersapp.adapters;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheadersapp.R;
import org.zakariya.stickyheadersapp.api.PagedMockLoader;

import java.util.ArrayList;

public class PagedDemoAdapter extends SectioningAdapter {

	private static final int USER_ITEM_TYPE_NORMAL = 0;
	private static final int USER_ITEM_TYPE_PROGRESS_INDICATOR = 1;
	private static final int USER_ITEM_TYPE_EXHAUSTED = 2;

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

	public class ExhaustedItemViewHolder extends SectioningAdapter.ItemViewHolder {
		public ExhaustedItemViewHolder(View itemView) {
			super(itemView);
		}
	}

	public class LoadingIndicatorItemViewHolder extends SectioningAdapter.ItemViewHolder {

		private static final int MAX_PROGRESS = 100;

		ProgressBar progressBar;

		public LoadingIndicatorItemViewHolder(View itemView) {
			super(itemView);
			progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
			progressBar.setMax(MAX_PROGRESS);
		}

		void setProgress(float progress) {
			progress = Math.max(Math.min(progress, 1), 0);
			progressBar.setProgress((int)(progress * MAX_PROGRESS));
		}

		float getProgress() {
			return (float)progressBar.getProgress() / (float) MAX_PROGRESS;
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

	ArrayList<PagedMockLoader.SectionModel> sections = new ArrayList<>();
	PagedMockLoader.SectionModel loadingIndicatorSectionModel;
	LoadingIndicatorItemViewHolder currentLoadingIndicatorItemViewHolder;

	public PagedDemoAdapter() {
	}

	public void addSection(PagedMockLoader.SectionModel section) {
		sections.add(section);
		notifySectionInserted(sections.size()-1);
	}

	public void showLoadingIndicator() {
		if (loadingIndicatorSectionModel == null) {
			loadingIndicatorSectionModel = new PagedMockLoader.SectionModel(null);
			loadingIndicatorSectionModel.addItem(PagedMockLoader.ItemModel.createLoadingIndicatorItemModel());

			addSection(loadingIndicatorSectionModel);
		}
	}

	public void updateLoadingIndicatorProgress(float progress) {
		if (currentLoadingIndicatorItemViewHolder != null) {
			currentLoadingIndicatorItemViewHolder.setProgress(progress);
		}
	}

	public void hideLoadingIndicator() {
		if (loadingIndicatorSectionModel != null) {
			int position = sections.indexOf(loadingIndicatorSectionModel);
			if (position >= 0) {

				// TODO: Why does doing this the RIGHT way cause a crash?
				//notifySectionRemoved(position);

				sections.remove(position);
				notifyAllSectionsDataSetChanged();
			}

			loadingIndicatorSectionModel = null;
		}
	}

	public void showLoadExhaustedIndicator() {
		PagedMockLoader.SectionModel section = new PagedMockLoader.SectionModel(null);
		section.addItem(PagedMockLoader.ItemModel.createExhaustedIndicatorItemModel());

		addSection(section);
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
	public int getSectionItemUserType(int sectionIndex, int itemIndex) {
		PagedMockLoader.ItemModel item = sections.get(sectionIndex).getItems().get(itemIndex);

		if (item.isLoadingIndicator()) {
			return USER_ITEM_TYPE_PROGRESS_INDICATOR;
		} else if (item.isExhaustedIndicator()) {
			return USER_ITEM_TYPE_EXHAUSTED;
		} else {
			return USER_ITEM_TYPE_NORMAL;
		}
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
	public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		switch(itemType) {
			case USER_ITEM_TYPE_NORMAL:
				return new ItemViewHolder(inflater.inflate(R.layout.list_item_simple_item, parent, false));

			case USER_ITEM_TYPE_PROGRESS_INDICATOR:
				return new LoadingIndicatorItemViewHolder(inflater.inflate(R.layout.list_item_load_progress, parent, false));

			case USER_ITEM_TYPE_EXHAUSTED:
				return new ExhaustedItemViewHolder(inflater.inflate(R.layout.list_item_load_exhausted, parent, false));
		}

		throw new IllegalArgumentException("Unrecognized itemType: " + itemType);
	}

	@Override
	public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View v = inflater.inflate(R.layout.list_item_simple_header, parent, false);
		return new HeaderViewHolder(v);
	}

	@Override
	public FooterViewHolder onCreateFooterViewHolder(ViewGroup parent, int footerType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View v = inflater.inflate(R.layout.list_item_simple_footer, parent, false);
		return new FooterViewHolder(v);
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
		PagedMockLoader.SectionModel s = sections.get(sectionIndex);

		switch(itemType) {
			case USER_ITEM_TYPE_NORMAL:
				ItemViewHolder ivh = (ItemViewHolder) viewHolder;
				ivh.textView.setText(s.getItems().get(itemIndex).getTitle());
				ivh.adapterPositionTextView.setText(Integer.toString(getAdapterPositionForSectionItem(sectionIndex, itemIndex)));
				break;

			case USER_ITEM_TYPE_PROGRESS_INDICATOR:
				currentLoadingIndicatorItemViewHolder = (LoadingIndicatorItemViewHolder) viewHolder;
				break;

			case USER_ITEM_TYPE_EXHAUSTED:
				break;

			default:
				throw new IllegalArgumentException("Unrecognized item type: " + itemType);
		}
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
		PagedMockLoader.SectionModel s = sections.get(sectionIndex);
		HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;
		hvh.adapterPositionTextView.setText(Integer.toString(getAdapterPositionForSectionHeader(sectionIndex)));
		hvh.textView.setText(s.getTitle());
	}
}
