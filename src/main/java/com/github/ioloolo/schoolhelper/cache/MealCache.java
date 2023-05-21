package com.github.ioloolo.schoolhelper.cache;

import com.github.ioloolo.schoolhelper.data.Meal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public final class MealCache {
	private final Map<LocalDate, List<Meal>> cache = new HashMap<>();

	public Optional<List<Meal>> get(LocalDate date) {
		return Optional.ofNullable(cache.get(date));
	}

	public void put(LocalDate date, List<Meal> meals) {
		cache.put(date, meals);

		log.info("[MealCache] Cached meals at date({})", date);
	}
}
