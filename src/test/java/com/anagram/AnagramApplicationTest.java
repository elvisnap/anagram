package com.anagram;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintStream;
import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AnagramApplicationTest {
	private String filePath = "./src/test/resources/lemmad.txt";

	@Mock
	private PrintStream out;

	@Test
	public void shouldPrintTime() throws Exception {
		System.setOut(out);
		String[] args = { filePath, "word" };
		AnagramApplication.main(args);

		Mockito.verify(out).println(Matchers.matches(".*\\d+.*"));
	}

	@Test
	public void shouldFindAnagramWithSpecialChar() throws Exception {
		AnagramApplication app = new AnagramApplication();

		ArrayList<String> result = app.exec(filePath, "abijudõ");

		Assertions.assertThat(result).contains("abijõud");
	}

	@Test
	public void shouldNotIncludeSearchWordInResult() throws Exception {
		AnagramApplication app = new AnagramApplication();

		ArrayList<String> result = app.exec(filePath, "abijõud");

		assertThat(result).doesNotContain("abijõud");
	}

	@Test
	public void shouldFindAnagramInAnyCase() throws Exception {
		AnagramApplication app = new AnagramApplication();

		ArrayList<String> result = app.exec(filePath, "aDSi");

		Assertions.assertThat(result).contains("AIDS");
	}

	@Test
	public void shouldFindAnagramInUpperCase() throws Exception {
		AnagramApplication app = new AnagramApplication();

		ArrayList<String> result = app.exec(filePath, "VPI");

		Assertions.assertThat(result).contains("VIP");
	}

	@Test
	public void shouldFindAnagrams() throws Exception {
		AnagramApplication app = new AnagramApplication();

		ArrayList<String> result = app.exec(filePath, "maja");

		Assertions.assertThat(result).contains("ajam");
	}

	@Test
	public void shouldFindAnagramsWithSpace() throws Exception {
		AnagramApplication app = new AnagramApplication();

		ArrayList<String> result = app.exec(filePath, "a cppellaa");

		Assertions.assertThat(result).contains("a cappella");
	}

	@Test
	public void shouldFindAnagramsWithHypen() throws Exception {
		AnagramApplication app = new AnagramApplication();

		ArrayList<String> result = app.exec(filePath, "a-vtamiini");

		Assertions.assertThat(result).contains("a-vitamiin");
	}

	@Test
	public void shouldFindAnagramsWithŽ() throws Exception {
		AnagramApplication app = new AnagramApplication();

		ArrayList<String> result = app.exec(filePath, "žarn");

		Assertions.assertThat(result).contains("žanr");
	}

}
