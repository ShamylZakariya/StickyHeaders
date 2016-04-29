package org.zakariya.stickyheadersapp.api;

import android.text.TextUtils;
import android.util.Log;

import org.zakariya.stickyheadersapp.model.Person;
import org.zakariya.stickyheadersapp.model.RandomUserResults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton for accessing the http://api.randomuser.me API
 * Only performs the fetch once. Vends cached data subsequently.
 */
public class RandomUserLoader {

	private static final String TAG = RandomUserLoader.class.getSimpleName();

	private RandomUsersService service;
	private List<Person> randomUsers = new ArrayList<>();
	private ArrayList<OnLoadCallback> onLoadCallbacks = new ArrayList<>();
	private boolean loading;

	public interface OnLoadCallback {
		void onRandomUsersDidLoad(List<Person> randomUsers);
		void onRandomUserLoadFailure(Throwable t);
	}

	public RandomUserLoader() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://api.randomuser.me")
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		service = retrofit.create(RandomUsersService.class);
	}

	public void load(final OnLoadCallback onLoadCallback) {
		if (!randomUsers.isEmpty()) {
			onLoadCallback.onRandomUsersDidLoad(randomUsers);
			return;
		}

		onLoadCallbacks.add(onLoadCallback);
		if (loading) {
			return;
		}

		loading = true;
		int count = 50;
		String nationalities = "us,dk,fr,gb"; // stick with "western" names to keep sorting simple
		String seed = "qux";
		Call<RandomUserResults> call = service.randomUsers(count, nationalities, seed);
		call.enqueue(new Callback<RandomUserResults>() {
			@Override
			public void onResponse(Call<RandomUserResults> call, Response<RandomUserResults> response) {
				RandomUserResults results = response.body();

				if (!TextUtils.isEmpty(results.error)) {

					Log.e(TAG, "onResponse: error message: " + results.error);
					for (OnLoadCallback c : onLoadCallbacks) {
						c.onRandomUserLoadFailure(new Throwable(results.error));
					}

				} else if (results.results != null && results.results.length > 0) {

					randomUsers = sortUsers(Arrays.asList(results.results));
					for (OnLoadCallback c : onLoadCallbacks) {
						c.onRandomUsersDidLoad(randomUsers);
					}

				} else {

					Log.e(TAG, "onResponse: got empty list, and no error message from API");
					for (OnLoadCallback c : onLoadCallbacks) {
						c.onRandomUserLoadFailure(new Throwable("No data received"));
					}

				}
				onLoadCallbacks.clear();
				loading = false;
			}

			@Override
			public void onFailure(Call<RandomUserResults> call, Throwable t) {
				Log.e(TAG, "onRandomUserLoadFailure: error: " + t.toString() );
				for (OnLoadCallback c : onLoadCallbacks) {
					c.onRandomUserLoadFailure(t);
				}
				onLoadCallbacks.clear();
				loading = false;
			}
		});

	}

	private List<Person> sortUsers(List<Person> users) {
		Collections.sort(users, new Comparator<Person>() {
			@Override
			public int compare(Person lhs, Person rhs) {
				if (lhs.name.last.equalsIgnoreCase(rhs.name.last)) {
					return lhs.name.first.compareToIgnoreCase(rhs.name.first);
				}

				return lhs.name.last.compareToIgnoreCase(rhs.name.last);
			}
		});

		return users;
	}
}
