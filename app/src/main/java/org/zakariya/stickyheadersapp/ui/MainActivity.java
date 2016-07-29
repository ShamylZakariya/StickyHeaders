package org.zakariya.stickyheadersapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import org.zakariya.stickyheadersapp.R;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	TabLayout tabs;
	ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		tabs = (TabLayout) findViewById(R.id.tabs);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(new MainActivityViewsPager(getSupportFragmentManager()));
		tabs.setupWithViewPager(viewPager);
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
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	class MainActivityViewsPager extends FragmentPagerAdapter {

		public MainActivityViewsPager(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return new MainPageFragment();
				case 1:
					return new AboutPageFragment();
				default:
					return null;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return getString(R.string.activity_main_pager_main);
				case 1:
					return getString(R.string.activity_main_pager_about);
				default:
					return null;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	}

	public static class MainPageFragment extends Fragment {

		private static final String TAG = MainPageFragment.class.getSimpleName();
		private static final String SCROLL_STATE = "MainPageFragment.SCROLL_STATE";

		RecyclerView recyclerView;

		@Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_main, container, false);
			recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
			setupDemoRecyclerView();

			if (savedInstanceState != null) {
				recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(SCROLL_STATE));
			}

			return view;
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			outState.putParcelable(SCROLL_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
			super.onSaveInstanceState(outState);
		}

		void setupDemoRecyclerView() {
			DemoModel[] demos = {
					new DemoModel(getString(R.string.demo_list_item_addressbook_title),
							getString(R.string.demo_list_item_addressbook_description),
							AddressBookDemoActivity.class),

					new DemoModel(getString(R.string.demo_list_item_callbacks_title),
							getString(R.string.demo_list_item_callbacks_description),
							HeaderCallbacksDemoActivity.class),

					new DemoModel(getString(R.string.demo_list_item_collapsing_headers_title),
							getString(R.string.demo_list_item_collapsing_headers_description),
							CollapsingSectionsDemoActivity.class),

					new DemoModel(getString(R.string.demo_list_item_stress_test_title),
							getString(R.string.demo_list_item_stress_test_description),
							StressTestDemoActivity.class),

					new DemoModel(getString(R.string.demo_list_item_sections_title),
							getString(R.string.demo_list_item_sections_description),
							SectioningAdapterDemoActivity.class),

					new DemoModel(getString(R.string.demo_list_item_multi_type_title),
							getString(R.string.demo_list_item_multi_type_description),
							MultiTypeItemDemoActivity.class),

					new DemoModel(getString(R.string.demo_list_item_paged_scroll_title),
							getString(R.string.demo_list_item_paged_scroll_description),
							PagedScrollDemoActivity.class),

					new DemoModel(getString(R.string.demo_list_item_selection_title),
							getString(R.string.demo_list_item_selection_description),
							SelectionDemo.class)
			};

			recyclerView.setAdapter(new DemoAdapter(getContext(), demos, new ItemClickListener() {
				@Override
				public void onItemClick(DemoModel demoModel) {
					startActivity(new Intent(getActivity(), demoModel.activityClass));
				}
			}));

			StickyHeaderLayoutManager layoutManager = new StickyHeaderLayoutManager();

			// set a header position callback to set elevation on sticky headers, because why not
			layoutManager.setHeaderPositionChangedCallback(new StickyHeaderLayoutManager.HeaderPositionChangedCallback() {
				@Override
				public void onHeaderPositionChanged(int sectionIndex, View header, StickyHeaderLayoutManager.HeaderPosition oldPosition, StickyHeaderLayoutManager.HeaderPosition newPosition) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						boolean elevated = newPosition == StickyHeaderLayoutManager.HeaderPosition.STICKY;
						header.setElevation(elevated ? 8 : 0);
					}
				}
			});

			recyclerView.setLayoutManager(layoutManager);
		}

		private static class DemoModel {
			String title;
			String description;
			Class activityClass;

			public DemoModel(String title, String description, Class activityClass) {
				this.title = title;
				this.description = description;
				this.activityClass = activityClass;
			}
		}

		private interface ItemClickListener {
			void onItemClick(DemoModel demoModel);
		}

		private static class DemoAdapter extends SectioningAdapter {

			public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
				TextView titleTextView;

				public HeaderViewHolder(View itemView) {
					super(itemView);
					titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
				}
			}

			public class ItemViewHolder extends SectioningAdapter.ItemViewHolder {
				TextView titleTextView;
				TextView descriptionTextView;

				public ItemViewHolder(View itemView) {
					super(itemView);
					titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
					descriptionTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
				}
			}

			Context context;
			DemoModel[] demos;
			ItemClickListener itemClickListener;

			public DemoAdapter(Context context, DemoModel[] demos, ItemClickListener itemClickListener) {
				this.context = context;
				this.demos = demos;
				this.itemClickListener = itemClickListener;
			}

			@Override
			public int getNumberOfSections() {
				return 1;
			}

			@Override
			public int getNumberOfItemsInSection(int sectionIndex) {
				return demos.length;
			}

			@Override
			public boolean doesSectionHaveHeader(int sectionIndex) {
				return true;
			}

			@Override
			public SectioningAdapter.HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				View v = inflater.inflate(R.layout.list_item_demo_header, parent, false);
				return new HeaderViewHolder(v);
			}

			@Override
			public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				View v = inflater.inflate(R.layout.list_item_demo_item, parent, false);
				return new ItemViewHolder(v);
			}

			@Override
			public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
				HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;
				hvh.titleTextView.setText(context.getString(R.string.main_demo_list_title));
			}

			@Override
			public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
				ItemViewHolder ivh = (ItemViewHolder) viewHolder;

				final DemoModel dm = demos[itemIndex];
				ivh.titleTextView.setText(dm.title);
				ivh.descriptionTextView.setText(dm.description);

				ivh.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						itemClickListener.onItemClick(dm);
					}
				});
			}
		}

	}

	public static class AboutPageFragment extends Fragment {
		@Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_about, container, false);
		}
	}
}
