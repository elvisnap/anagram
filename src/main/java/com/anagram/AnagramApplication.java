package com.anagram;

import static java.lang.System.nanoTime;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class AnagramApplication {

	private static final String ENCODING = "windows-1257";
	private static final int CR = 13;
	private static final int LF = 10;
	private MappedByteBuffer byteBuffer;
	private byte[] source;
	private byte[] potentialTarget;
	private boolean checkForUpperCase = true;

	public static void main(String[] args) throws Exception {
		long startTime = nanoTime();
		Set<String> result = new AnagramApplication().exec(args[0], args[1]);
		long stopTime = nanoTime();
		System.out.println(MICROSECONDS.convert(stopTime - startTime, NANOSECONDS) + "," + String.join(",", result));
	}

	public Set<String> exec(String path, String wordToSearch) throws Exception {
		loadDictionary(path);
		init(wordToSearch);
		Set<String> result = findAnograms();
		result.remove(wordToSearch);
		return result;
	}

	private void init(String wordToSearch) throws UnsupportedEncodingException {
		source = wordToSearch.toLowerCase().getBytes();
		Arrays.sort(source);
		potentialTarget = new byte[source.length];
	}

	private boolean isAnagram(byte[] target) {
		Arrays.sort(target);
		for (int i = 0; i < source.length; i++) {
			if (source[i] != target[i]) {
				return false;
			}
		}
		return true;
	}

	private void fixUpperCase(byte[] target) {
		if (checkForUpperCase) {
			boolean allInLowerCase = true;
			for (int i = 0; i < target.length; i++) {
				if (Character.isUpperCase(target[i])) {
					target[i] = (byte) Character.toLowerCase(target[i]);
					allInLowerCase = false;
				}
			}
			if (allInLowerCase) {
				checkForUpperCase = false;
			}
		}
	}

	private void loadDictionary(String path) throws Exception {
		FileChannel fileChannel = (FileChannel) Files.newByteChannel(Paths.get(path), EnumSet.of(READ));
		byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
	}

	private byte[] loadNextWord() {
		while (true) {
			for (int i = 0; i < potentialTarget.length; i++) {
				potentialTarget[i] = byteBuffer.get();
				if (potentialTarget[i] == CR || potentialTarget[i] == LF) {
					i = -1;
				}
			}
			if (isLineEndReached()) {
				return potentialTarget;
			} else {
				readTillEndOfTheLine();
			}
		}
	}

	private boolean isLineEndReached() {
		byte b = byteBuffer.get();
		if (b == CR || b == LF) {
			return true;
		}
		return false;
	}

	private void readTillEndOfTheLine() {
		while (!isLineEndReached()) {
		}
	}

	private Set<String> findAnograms() throws Exception {
		Set<String> result = new HashSet<>();

		try {

			while (byteBuffer.hasRemaining()) {
				byte[] nextWord = loadNextWord();
				byte[] targetClone = nextWord.clone();
				fixUpperCase(targetClone);
				if (isAnagram(targetClone)) {
					result.add(new String(nextWord, ENCODING));
				}
			}
		} catch (IndexOutOfBoundsException | BufferUnderflowException e) {
			// nothing to do
		}
		return result;
	}

}
