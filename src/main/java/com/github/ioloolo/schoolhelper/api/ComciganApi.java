package com.github.ioloolo.schoolhelper.api;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.ioloolo.schoolhelper.data.TimeTable;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RequiredArgsConstructor
public final class ComciganApi {

    private static final List<String> TEACHER_FULL_NAME = List.of(
            "이복섬", "이용현", "김모아", "김미애", "임영현", "진신영", "윤현주", "이혜지", "정수진", "박종진",
            "김재광", "이기종", "조환희", "송준숙", "이슬아", "서나라", "전영광", "이소영", "손대협", "김재휘",
            "전효나", "송미라", "김보철", "김용태", "유진솔", "이은곤", "정상현", "정호영", "신미애", "정하나",
            "김은혜", "원선미", "윤지수", "박지형", "이용현", "조승현", "김종성", "이성민", "연채원", "박승환",
            "박병주", "김상정", "최선희");

    private static Optional<JsonObject> getComciganTable() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://comci.net:4082/36179?NzM2MjlfMzM3MjhfMF8x")
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();

            responseBody = responseBody.replaceAll("\0", "");

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);

            return Optional.of(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private static Optional<TimeTable> getPeriodTimeTable(JsonObject json, LocalDate date, int grade, int clazz, int period) {
        JsonArray dataArray = json.get("자료481").getAsJsonArray()
                .get(grade).getAsJsonArray()
                .get(clazz).getAsJsonArray()
                .get(date.getDayOfWeek().getValue()).getAsJsonArray();

        if (period >= dataArray.size())
            return Optional.empty();

        int data = dataArray.get(period).getAsInt();

        int th = data / 100;
        int sb = data - (th * 100);

        String lecture = json.get("자료492").getAsJsonArray()
                .get(sb).getAsString();

        String teacher = json.get("자료446").getAsJsonArray()
                .get(th).getAsString()
                .substring(0, 2);

        String finalTeacher = teacher;
        teacher = TEACHER_FULL_NAME.stream()
                .filter(s -> s.startsWith(finalTeacher))
                .findFirst()
                .orElse(teacher);

        return Optional.of(TimeTable.builder()
                .period(period)
                .lecture(lecture)
                .teacher(teacher)
                .location("%d-%d".formatted(grade, clazz))
                .build());
    }

    public static List<TimeTable> getTimeTable(LocalDate date, int grade, int clazz) {
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return Collections.emptyList();
        }

        Optional<JsonObject> jsonObject = getComciganTable();

        return new ArrayList<>() {{
            jsonObject.ifPresent(json -> {
                for (int period = 1; period <= 7; period++) {
                    Optional<TimeTable> timeTable = getPeriodTimeTable(json, date, grade, clazz, period);

                    timeTable.ifPresent(this::add);
                }
            });
        }};
    }

    public static List<TimeTable> getTimeTable(int grade, int clazz) {
        return getTimeTable(LocalDate.now(), grade, clazz);
    }
}
