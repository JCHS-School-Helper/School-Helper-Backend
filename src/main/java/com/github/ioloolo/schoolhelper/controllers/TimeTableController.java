package com.github.ioloolo.schoolhelper.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.ioloolo.schoolhelper.data.Student;
import com.github.ioloolo.schoolhelper.data.TimeTable;
import com.github.ioloolo.schoolhelper.services.TimeTableService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/timetable")
@RequiredArgsConstructor
public final class TimeTableController {

	private final TimeTableService timeTableService;

	@GetMapping
	public ResponseEntity<?> getTimeTable(@RequestParam(value = "grade") int grade,
			@RequestParam(value = "class") int clazz,
			@RequestParam(value = "id") int id,
			@RequestParam(value = "year", required = false) Integer year,
			@RequestParam(value = "month", required = false) Integer month,
			@RequestParam(value = "day", required = false) Integer day) {

		Student student = Student.of(grade, clazz, id);
		LocalDate today = LocalDate.now();

		year = year != null ? year : today.getYear();
		month = month != null ? month : today.getMonthValue();
		day = day != null ? day : today.getDayOfMonth();

		List<TimeTable> timeTable = timeTableService.getTimeTable(LocalDate.of(year, month, day), student);

		if (timeTable.isEmpty())
			return ResponseEntity.noContent().build();

		return ResponseEntity.ok(timeTable);
	}
}
