package com.github.ioloolo.schoolhelper.api;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.github.ioloolo.schoolhelper.data.Student;
import com.github.ioloolo.schoolhelper.data.TimeTable;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Component
@PropertySource("classpath:application.yaml")
public final class RiroSchoolApi {
	private static final Map<Session, String> session = new HashMap<>();

	private static String id;
	private static String pw;

	@Value("${app.riroschool.id}")
	public void setId(String value) {
		id = value;
	}

	@Value("${app.riroschool.pw}")
	public void setPw(String value) {
		pw = value;
	}

	public static List<TimeTable> getTimeTable(LocalDate date, Student student) {
		if (student.getGrade() == 1) {
			return Collections.emptyList();
		}

		OkHttpClient client = new OkHttpClient();

		RequestBody body = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("s1", "student")
				.addFormDataPart("s_num", student.toString())
				.addFormDataPart("key", "_")
				.build();

		Request request = new Request.Builder()
				.url("https://jecheonh.riroschool.kr/lecture.php?club=index&action=list&db=1703&cate=%d".formatted(student.getGrade() == 2 ? 5 : 6))
				.method("POST", body)
				.addHeader("Cookie", "login_chk=%s;".formatted(session.get(Session.LOGIN_CHK)))
				.addHeader("Cookie", "cookie_token=%s;".formatted(session.get(Session.TOKEN)))
				.build();

		try (Response response = client.newCall(request).execute()) {
			assert response.body() != null;
			String responseBody = response.body().string();

			if (responseBody.equals("<META HTTP-EQUIV=Refresh CONTENT=0;URL='user.php?action=signin'>")) {
				log.warn("[RiroSchoolApi] User Session Expired. Trying to login again.");

				RequestBody loginBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
						.addFormDataPart("app","user")
						.addFormDataPart("mode","login")
						.addFormDataPart("userType","1")
						.addFormDataPart("id", id)
						.addFormDataPart("pw", pw)
						.build();

				Request loginRequest = new Request.Builder()
						.url("https://jecheonh.riroschool.kr/ajax.php")
						.method("POST", loginBody)
						.build();

				try (Response loginResponse = client.newCall(loginRequest).execute()) {
					loginResponse.headers("Set-Cookie").forEach(cookie -> {
						AbstractMap.SimpleEntry<String, String> entry = new AbstractMap.SimpleEntry<>(cookie.split("=")[0], cookie.split("=")[1]);

						switch (entry.getKey()) {
							case "login_chk" -> session.put(Session.LOGIN_CHK, entry.getValue());
							case "cookie_token" -> session.put(Session.TOKEN, entry.getValue());
						}
					});

					log.info("[RiroSchoolApi] Login Success.");

					return getTimeTable(date, student);
				} catch (IOException e) {
					e.printStackTrace();
				}

				return Collections.emptyList();
			}

			Document document = Jsoup.parse(responseBody);

			Element table = document.selectFirst("#container > div > table > tbody > tr:nth-child(3) > td > table > tbody");

			if (table != null) {
				List<TimeTable> timeTableList = new ArrayList<>();

				Elements timeTables = table.children();
				for (int i = 1; i < timeTables.size(); i++) {
					Element timeTable = timeTables.get(i);

					Elements details = timeTable.children();

					Element lectureElement = details.get(2);
					Element teacherElement = details.get(3);
					Element locationElement = details.get(6);

					if (lectureElement == null || teacherElement == null || locationElement == null) {
						continue;
					}

					Element timeDescriptionElement = lectureElement.selectFirst("a");
					if (timeDescriptionElement == null) {
						continue;
					}

					String lecture = lectureElement.text();
					lecture = lecture.substring(8);
					lecture = lecture.substring(0, lecture.lastIndexOf("-")-1);
					lecture = lecture.substring(0, lecture.contains("(") ? (lecture.lastIndexOf("(")) : lecture.length());

					String time = timeDescriptionElement.attr("onmouseover");
					time = time.split(":")[1];
					time = time.substring(0, time.length() - 2);
					for (String t : time.split(",")) {
						DayOfWeek dayOfWeek = switch (t.charAt(0)) {
							case '월' -> DayOfWeek.MONDAY;
							case '화' -> DayOfWeek.TUESDAY;
							case '수' -> DayOfWeek.WEDNESDAY;
							case '목' -> DayOfWeek.THURSDAY;
							case '금' -> DayOfWeek.FRIDAY;
							default -> null;
						};

						if (dayOfWeek != null && dayOfWeek == date.getDayOfWeek()) {
							int period = t.charAt(1) - '0';

							timeTableList.add(TimeTable.builder()
									.period(period)
									.lecture(lecture)
									.teacher(teacherElement.text())
									.location(locationElement.text())
									.build());
						}
					}
				}

				return timeTableList;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Collections.emptyList();

	}

	public static List<TimeTable> getTimeTable(Student student) {
		return getTimeTable(LocalDate.now(), student);
	}

	private enum Session {
		LOGIN_CHK, TOKEN
	}

	public static void main(String[] args) {
		System.out.println(getTimeTable(Student.of(3, 7, 8)));
	}
}
