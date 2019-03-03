package com.anagram;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintStream;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AnagramSearchTest {
	private String normalFile = "./src/test/resources/lemmad.txt";
	private String oneWordFile = "./src/test/resources/oneword.txt";

	@Mock
	private PrintStream out;

	@Test
	public void shouldPrintTime() throws Exception {
		System.setOut(out);
		String[] args = { normalFile, "word" };
		AnagramSearch.main(args);

		Mockito.verify(out).println(Matchers.matches(".*\\d+.*"));
	}

	@Test
	public void shouldFindAnagramWithEstonianChar() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "abi�jud");

		Assertions.assertThat(result).contains("abij�ud");
	}

	@Test
	public void shouldNotIncludeSearchWordInResult() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "abij�ud");

		assertThat(result).doesNotContain("abij�ud");
	}

	@Test
	public void shouldFindFirstWord() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "aDSi");

		Assertions.assertThat(result).contains("AIDS");
	}

	@Test
	public void shouldFindLastWord() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "��iRi");

		Assertions.assertThat(result).contains("��rii");
	}

	@Test
	public void shouldFindAnagramInUpperCase() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "VPI");

		Assertions.assertThat(result).contains("VIP");
	}

	@Test
	public void shouldFindAnagrams() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "maja");

		Assertions.assertThat(result).contains("ajam");
	}

	@Test
	public void shouldFindAnagramsWithSpace() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "a cppellaa");

		Assertions.assertThat(result).contains("a cappella");
	}

	@Test
	public void shouldFindAnagramsWithHypen() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "a-vtamiini");

		Assertions.assertThat(result).contains("a-vitamiin");
	}

	@Test
	public void shouldFindAnagramsWith�() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "�arn");

		Assertions.assertThat(result).contains("�anr");
	}

	@Test
	public void shouldFindLongestWord() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "kergej�ustiku-meistriv�istlusde");

		Assertions.assertThat(result).contains("kergej�ustiku-meistriv�istlused");
	}

	@Test
	public void shouldFindWordWithUpperCharInMiddle() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "anno Domiin");

		Assertions.assertThat(result).contains("anno Domini");
	}

	@Test
	public void shouldFindWordWithUpperSpecialChar() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "�veitis");

		Assertions.assertThat(result).contains("�veitsi");
	}

	@Test
	public void shouldFindWordWithSpecialChar() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(normalFile, "e�!");

		Assertions.assertThat(result).contains("�e!");
	}

	@Test
	public void shouldFindInOneWordFile() throws Exception {
		AnagramSearch app = new AnagramSearch();

		Set<String> result = app.exec(oneWordFile, "aisd");

		Assertions.assertThat(result).contains("AIDS");
	}

	// @Test
	public void benchmarkTest() throws Exception {
		long timesum = 0;
		int timesToRun = 500;
		for (int i = 0; i < timesToRun; i++) {
			long startTime = nanoTime();

			AnagramSearch app = new AnagramSearch();
			// app.exec(filePath, "maja");
			app.exec(normalFile, "kergej�ustiku-meistriv�istlusde");
			long stopTime = nanoTime();
			timesum += stopTime - startTime;
		}
		System.out.println(MICROSECONDS.convert(timesum / timesToRun, NANOSECONDS));
	}
}
