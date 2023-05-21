package com.github.ioloolo.schoolhelper.util;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Singular;

@Component
@Builder
public final class QueryString {

	@Singular("param")
	private final Map<String, Object> params;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			builder.append(entry.getKey())
					.append("=")
					.append(entry.getValue())
					.append("&");
		}

		return builder.substring(0, builder.length() - 1);
	}
}
