package org.zakariya.stickyheadersapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import org.zakariya.stickyheadersapp.R;

public class MainActivity extends AppCompatActivity {

    TabLayout tabs;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabs = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewPager);
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

        MainActivityViewsPager(FragmentManager fm) {
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

        private static final String SCROLL_STATE = "MainPageFragment.SCROLL_STATE";

        RecyclerView recyclerView;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main, container, false);
            recyclerView = view.findViewById(R.id.recyclerView);
            setupDemoRecyclerView();

            if (savedInstanceState != null) {
                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
                if (lm != null)
                {
                    lm.onRestoreInstanceState(savedInstanceState.getParcelable(SCROLL_STATE));
                }
            }

            return view;
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
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

            recyclerView.setAdapter(
                    new DemoAdapter(getContext(),
                            demos,
                            demoModel -> startActivity(new Intent(getActivity(), demoModel.activityClass))));

            StickyHeaderLayoutManager layoutManager = new StickyHeaderLayoutManager();

            // set a header position callback to set elevation on sticky headers, because why not
            layoutManager.setHeaderPositionChangedCallback((sectionIndex, header, oldPosition, newPosition) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    boolean elevated = newPosition == StickyHeaderLayoutManager.HeaderPosition.STICKY;
                    header.setElevation(elevated ? 8 : 0);
                }
            });

            recyclerView.setLayoutManager(layoutManager);
        }

        private static class DemoModel {
            String title;
            String description;
            Class activityClass;

            DemoModel(String title, String description, Class activityClass) {
                this.title = title;
                this.description = description;
                this.activityClass = activityClass;
            }
        }

        private interface ItemClickListener {
            void onItemClick(DemoModel demoModel);
        }

        private static class DemoAdapter extends SectioningAdapter {

            class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
                TextView titleTextView;

                HeaderViewHolder(View itemView) {
                    super(itemView);
                    titleTextView = itemView.findViewById(R.id.titleTextView);
                }
            }

            class ItemViewHolder extends SectioningAdapter.ItemViewHolder {
                TextView titleTextView;
                TextView descriptionTextView;

                ItemViewHolder(View itemView) {
                    super(itemView);
                    titleTextView = itemView.findViewById(R.id.titleTextView);
                    descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
                }
            }

            Context context;
            DemoModel[] demos;
            ItemClickListener itemClickListener;

            DemoAdapter(Context context, DemoModel[] demos, ItemClickListener itemClickListener) {
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

                ivh.itemView.setOnClickListener(v -> itemClickListener.onItemClick(dm));
            }
        }

    }

    public static class AboutPageFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_about, container, false);
        }
    }
}
