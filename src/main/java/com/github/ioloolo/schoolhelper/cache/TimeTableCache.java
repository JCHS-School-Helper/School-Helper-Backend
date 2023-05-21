package com.github.ioloolo.schoolhelper.cache;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.ioloolo.schoolhelper.data.Student;
import com.github.ioloolo.schoolhelper.data.TimeTable;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class TimeTableCache {

	private final Map<LocalDate, Map<Student, List<TimeTable>>> cache = new HashMap<>();

	public Optional<List<TimeTable>> get(Student student) {
		LocalDate today = LocalDate.now();

		return Optional.ofNullable(cache.getOrDefault(today, Collections.emptyMap()).get(student));
	}

	public Optional<List<TimeTable>> get(LocalDate date, Student student) {
		return Optional.ofNullable(cache.getOrDefault(date, Collections.emptyMap()).get(student));
	}

	public void put(Student student, List<TimeTable> timeTable) {
		LocalDate today = LocalDate.now();

		if (!cache.containsKey(today)) {
			cache.put(today, new HashMap<>());
		}

		cache.get(today).put(student, timeTable);

		log.info("[TimeTableCache] Cached TimeTable for student({}) at date({})", student, today);
	}

	public void put(LocalDate date, Student student, List<TimeTable> timeTable) {
		if (!cache.containsKey(date)) {
			cache.put(date, new HashMap<>());
		}

		cache.get(date).put(student, timeTable);

		log.info("[TimeTableCache] Cached TimeTable for student({}) at date({})", student, date);
	}
}
