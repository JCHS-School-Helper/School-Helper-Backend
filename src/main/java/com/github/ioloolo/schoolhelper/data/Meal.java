package com.github.ioloolo.schoolhelper.data;

import java.util.EnumSet;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public final class Meal {

	private final MealType type;
	private final List<String> menu;

	@RequiredArgsConstructor
	public enum MealType {
		BREAKFAST("조식"),
		LUNCH("중식"),
		DINNER("석식"),
		;

		@Getter
		private final String value;

		public static MealType of(String s) {
			return EnumSet.allOf(MealType.class).stream()
					.filter(type -> type.value.equals(s))
					.findFirst()
					.orElse(MealType.LUNCH);
		}
	}
}
