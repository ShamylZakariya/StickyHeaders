package org.zakariya.stickyheadersapp.api;

import android.os.Handler;

import java.util.ArrayList;

/**
 * This is a fake service for async loading data for use by PagedScrollDemoActivity
 */
public class PagedMockLoader {

	public static final String TAG = PagedMockLoader.class.getSimpleName();

	public static class ItemModel {
		String title;

		public ItemModel(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}
	}

	public static class SectionModel {
		String title;
		ArrayList<ItemModel> items = new ArrayList<>();

		public SectionModel(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public ArrayList<ItemModel> getItems() {
			return items;
		}

		public void setItems(ArrayList<ItemModel> items) {
			this.items = items;
		}

		public void addItem(ItemModel itemModel) {
			items.add(itemModel);
		}
	}

	public interface Listener {
		void onSectionLoadBegun();
		void onSectionLoadProgress(float progress);
		void onSectionLoaded(SectionModel sectionModel);
		void onSectionsExhausted();
	}

	Handler handler;
	Runnable runnable;
	Listener listener;
	int page;
	int maxPages;
	int tick;
	static final int STEPS = 100;
	static final long DELAY = 20;

	public PagedMockLoader(int maxPages) {
		this.maxPages = maxPages;
	}

	/**
	 * Build a section and vend it synchronously
	 * @return a new populated SectionModel
	 */
	public SectionModel vendSection(int page) {
		PagedMockLoader.SectionModel sectionModel = new PagedMockLoader.SectionModel("Page " + Integer.toString(page));

		for (int j = 0; j < 20; j++) {
			sectionModel.addItem(new PagedMockLoader.ItemModel("Item " + Integer.toString(j)));
		}

		return sectionModel;
	}

	public void load(final int page, final Listener listener) {

		this.listener = listener;
		this.page = page;

		if (handler == null) {
			handler = new Handler();
		}

		if (runnable == null) {
			runnable = new Runnable() {
				@Override
				public void run() {
					tick++;
					if (tick < STEPS) {
						stepLoad();
					} else {
						finishLoad();
					}
				}
			};
		}


		tick = 0;
		listener.onSectionLoadBegun();
		stepLoad();
	}

	private void stepLoad() {
		listener.onSectionLoadProgress((float) tick / (float) STEPS);
		handler.postDelayed(runnable, DELAY);
	}

	private void finishLoad() {
		//Log.i(TAG, "finishLoad: listener? " + listener);
		if (listener != null) {
			if (page < maxPages) {
				listener.onSectionLoaded(vendSection(page));
			} else {
				listener.onSectionsExhausted();
			}
			listener = null;
		}
	}


}
