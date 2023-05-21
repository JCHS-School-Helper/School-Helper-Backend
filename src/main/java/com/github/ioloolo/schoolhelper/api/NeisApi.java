package com.github.ioloolo.schoolhelper.api;

import com.github.ioloolo.schoolhelper.util.QueryString;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;

@Builder
@RequiredArgsConstructor
public final class NeisApi {

	private static final String NEIS_HOST = "https://open.neis.go.kr/hub";
	private static final String NEIS_API_KEY = "7904357bf8b649afb3b252212499411e";
	private static final String NEIS_APTP_CODE = "M10";
	private static final String NEIS_SCHUL_CODE = "8000057";

	private final String url;
	private final QueryString query;

	public static QueryString.QueryStringBuilder getDefaultParams() {
		return QueryString.builder()
				.param("KEY", NEIS_API_KEY)
				.param("Type", "json")
				.param("ATPT_OFCDC_SC_CODE", NEIS_APTP_CODE)
				.param("SD_SCHUL_CODE", NEIS_SCHUL_CODE);
	}

	public Request toOkHttpRequest() {
		return new Request.Builder()
				.url(NEIS_HOST + url + "?" + query.toString())
				.build();
	}
}
