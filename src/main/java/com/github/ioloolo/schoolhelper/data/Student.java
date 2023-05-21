package com.github.ioloolo.schoolhelper.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public final class Student {
	private final int grade;
	private final int clazz;
	private final int id;

	public String toString() {
		return String.format("%d%02d%02d", grade, clazz, id);
	}
}
