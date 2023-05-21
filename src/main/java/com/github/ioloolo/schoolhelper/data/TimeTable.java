package com.github.ioloolo.schoolhelper.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class TimeTable {

	private int period;
	private String lecture;
	private String teacher;
	private String location;
}
