package org.zakariya.stickyheadersapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import org.zakariya.stickyheadersapp.adapters.AddressBookAdapter;
import org.zakariya.stickyheadersapp.adapters.SimpleDemoAdapter;
import org.zakariya.stickyheadersapp.api.RandomUserLoader;
import org.zakariya.stickyheadersapp.model.Person;

import java.util.List;

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
		viewPager.setAdapter(new RecyclerViewsPager(getSupportFragmentManager()));
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
			case R.id.aboutMenuItem:
				Intent intent = new Intent(this, AboutActivity.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private static class RecyclerViewsPager extends FragmentPagerAdapter {

		public RecyclerViewsPager(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch(position) {
				case 0: return new AddressBookDemo();
				case 1: return new StickySectionHeadersDemo();
				case 2: return new SectionsDemo();
				default: return null;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0: return AddressBookDemo.demoName();
				case 1: return StickySectionHeadersDemo.demoName();
				case 2: return SectionsDemo.demoName();
				default: return null;
			}
		}

		@Override
		public int getCount() {
			return 3;
		}
	}

	public static class RecyclerViewFragment extends Fragment {

		RecyclerView recyclerView;
		ProgressBar progressBar;

		public RecyclerViewFragment() {
			setHasOptionsMenu(true);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch(item.getItemId()) {
				case R.id.reloadMenuItem:
					Log.i(TAG, "onOptionsItemSelected: RELOAD");
					RecyclerView.Adapter adapter = recyclerView.getAdapter();
					if (adapter instanceof SectioningAdapter) {
						((SectioningAdapter)adapter).notifyAllSectionsDataSetChanged();
					} else {
						adapter.notifyDataSetChanged();
					}
					return true;

				default:
					return super.onOptionsItemSelected(item);
			}
		}

		@Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_recycler, container, false);
			recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
			progressBar = (ProgressBar) view.findViewById(R.id.progress);
			return view;
		}
	}

	public static class SectionsDemo extends RecyclerViewFragment {
		public SectionsDemo() {
		}

		public static String demoName() {
			return "Sections";
		}

		@Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			View v = super.onCreateView(inflater, container, savedInstanceState);

			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
			recyclerView.setAdapter(new SimpleDemoAdapter(5, 5));

			return v;
		}
	}

	public static class StickySectionHeadersDemo extends RecyclerViewFragment {
		public StickySectionHeadersDemo() {
		}

		public static String demoName() {
			return "Sticky Section Headers";
		}

		@Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			View v = super.onCreateView(inflater, container, savedInstanceState);

			StickyHeaderLayoutManager stickyHeaderLayoutManager = new StickyHeaderLayoutManager();
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

			recyclerView.setLayoutManager(stickyHeaderLayoutManager);
			recyclerView.setAdapter(new SimpleDemoAdapter(5, 5));

			return v;
		}
	}

	public static class AddressBookDemo extends RecyclerViewFragment {


		public AddressBookDemo() {
		}

		public static String demoName() {
			return "Address Book";
		}

		RandomUserLoader getRandomUserLoader() {
			return ((StickyHeadersDemoApp) getContext().getApplicationContext()).getRandomUserLoader();
		}

		@Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			final View v = super.onCreateView(inflater, container, savedInstanceState);

			recyclerView.setLayoutManager(new StickyHeaderLayoutManager());

			final AddressBookAdapter adapter = new AddressBookAdapter();
			recyclerView.setAdapter(adapter);

			progressBar.setVisibility(View.VISIBLE);
			recyclerView.setVisibility(View.GONE);

			getRandomUserLoader().load(new RandomUserLoader.OnLoadCallback() {
				@Override
				public void onRandomUsersDidLoad(List<Person> randomUsers) {
					progressBar.setVisibility(View.GONE);
					recyclerView.setVisibility(View.VISIBLE);
					adapter.setPeople(randomUsers);
				}

				@Override
				public void onFailure(Throwable t) {
					progressBar.setVisibility(View.GONE);
					recyclerView.setVisibility(View.GONE);
					if (v != null) {
						Snackbar.make(v, "Unable to load fake people", Snackbar.LENGTH_LONG).show();
					}
				}
			});

			return v;
		}
	}

}
