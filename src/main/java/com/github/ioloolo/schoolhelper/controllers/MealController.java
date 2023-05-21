package com.github.ioloolo.schoolhelper.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.ioloolo.schoolhelper.data.Meal;
import com.github.ioloolo.schoolhelper.services.MealService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/meal")
@RequiredArgsConstructor
public final class MealController {

	private final MealService mealService;

	@GetMapping
	public ResponseEntity<?> getMeal(@RequestParam(value = "year", required = false) Integer year,
			@RequestParam(value = "month", required = false) Integer month,
			@RequestParam(value = "day", required = false) Integer day) {

		LocalDate today = LocalDate.now();

		year = year != null ? year : today.getYear();
		month = month != null ? month : today.getMonthValue();
		day = day != null ? day : today.getDayOfMonth();

		List<Meal> meals = mealService.getMeal(LocalDate.of(year, month, day));

		if (meals.isEmpty())
			return ResponseEntity.noContent().build();

		return ResponseEntity.ok(meals);
	}
}
