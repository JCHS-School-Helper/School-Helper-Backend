package com.github.ioloolo.schoolhelper.services;

import com.github.ioloolo.schoolhelper.api.NeisApi;
import com.github.ioloolo.schoolhelper.cache.MealCache;
import com.github.ioloolo.schoolhelper.data.Meal;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public final class MealService {

	private final MealCache cache;

	public List<Meal> getMeal(LocalDate date) {
		if (cache.get(date).isEmpty()) {
			if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
				cache.put(date, List.of());
			} else {
				OkHttpClient client = new OkHttpClient();

				Request request = NeisApi.builder()
						.url("/mealServiceDietInfo")
						.query(
								NeisApi.getDefaultParams()
										.param("MLSV_YMD", date.toString().replaceAll("-", ""))
										.build()
						)
						.build()
						.toOkHttpRequest();

				try (Response response = client.newCall(request).execute()) {
					assert response.body() != null;
					String responseBody = response.body().string();

					Gson gson = new Gson();
					JsonObject json = gson.fromJson(responseBody, JsonObject.class);

					if (!json.has("mealServiceDietInfo")) {
						cache.put(date, List.of());
					} else {
						List<Meal> meals = new ArrayList<>();

						JsonArray mealArray = json
								.get("mealServiceDietInfo").getAsJsonArray()
								.get(1).getAsJsonObject()
								.get("row").getAsJsonArray();

						mealArray.forEach(jsonElement -> {
							JsonObject meal = jsonElement.getAsJsonObject();

							Meal.MealType type = Meal.MealType.of(meal.get("MMEAL_SC_NM").getAsString());
							List<String> menu = new ArrayList<>() {{
								String cleaned = meal.get("DDISH_NM").getAsString()
										.replaceAll("\\(.*?\\)", "");

								Arrays.stream(cleaned.split("<br/>"))
										.map(String::trim)
										.forEach(this::add);
							}};

							meals.add(Meal.of(type, menu));
						});

						cache.put(date, meals);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return cache.get(date).get();
	}
}
