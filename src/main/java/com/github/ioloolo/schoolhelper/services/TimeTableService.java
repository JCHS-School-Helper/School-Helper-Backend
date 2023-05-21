package com.github.ioloolo.schoolhelper.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.ioloolo.schoolhelper.api.ComciganApi;
import com.github.ioloolo.schoolhelper.api.RiroSchoolApi;
import com.github.ioloolo.schoolhelper.cache.TimeTableCache;
import com.github.ioloolo.schoolhelper.data.Student;
import com.github.ioloolo.schoolhelper.data.TimeTable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class TimeTableService {

	private final TimeTableCache cache;

	public List<TimeTable> getTimeTable(Student student) {
		return getTimeTable(LocalDate.now(), student);
	}

	public List<TimeTable> getTimeTable(LocalDate date, Student student) {
		if (cache.get(date, student).isEmpty()) {
			if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
				cache.put(date, student, Collections.emptyList());
			} else {
				List<TimeTable> comcigan = ComciganApi.getTimeTable(date, student.getGrade(), student.getClazz());
				List<TimeTable> riroschool = RiroSchoolApi.getTimeTable(date, student);

				cache.put(date, student, new ArrayList<>() {{
					for (int period = 1; period <= 7; period++) {
						int finalPeriod = period;

						riroschool.stream()
								.filter(timeTable -> timeTable.getPeriod() == finalPeriod)
								.findFirst().ifPresentOrElse(
										this::add,
										() -> comcigan.stream()
												.filter(timeTable -> timeTable.getPeriod() == finalPeriod)
												.findFirst().ifPresent(this::add));
					}
				}});
			}
		}

		return cache.get(date, student).orElse(Collections.emptyList());
	}
}
