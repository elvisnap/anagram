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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

public class AnagramApplication {

	private static final String ENCODING = "windows-1257";
	private MappedByteBuffer byteBuffer;
	private byte[] searchBytes;
	private boolean checkForUpperCase = true;
	private int cr = 10;
	private int lf = 13;

	public static void main(String[] args) throws Exception {
		long startTime = nanoTime();
		ArrayList<String> result = new AnagramApplication().exec(args[0], args[1]);
//		ArrayList<String> result = new AnagramApplication().exec("C:\\Users\\Nemo\\Desktop\\lemmad.txt", "þrüii");
		System.out.println(MICROSECONDS.convert(nanoTime() - startTime, NANOSECONDS) + "," + String.join(",", result));
	}

	public ArrayList<String> exec(String path, String wordToSearch) throws Exception {
		loadDictionary(path);
		prepareSearchCriteria(wordToSearch);
		ArrayList<String> result = findAnograms();
		result.remove(wordToSearch);
		return result;
	}

	private void prepareSearchCriteria(String word) throws UnsupportedEncodingException {
		searchBytes = word.toLowerCase().getBytes(ENCODING);
		Arrays.sort(searchBytes);
	}

	private boolean isAnagram(byte[] target) {
		Arrays.sort(target);
		for (int i = 0; i < searchBytes.length; i++) {
			if (searchBytes[i] != target[i]) {
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

	private byte[] loadNextWord(int bytesToRead) {
		byte[] dst = new byte[bytesToRead];

		while (true) {
			for (int i = 0; i < dst.length; i++) {
				dst[i] = byteBuffer.get();
				if (dst[i] == lf || dst[i] == cr) {
					i = -1;
				}
			}
			if (isLineEndReached()) {
				return dst;
			} else {
				readTillEndOfTheLine();
			}
		}
	}

	private boolean isLineEndReached() {
		byte b = byteBuffer.get();
		if (b == '\r') {
			byteBuffer.get();
			return true;
		}
		if (b == '\n') {
			return true;
		}
		return false;
	}

	private void readTillEndOfTheLine() {
		while (!isLineEndReached()) {
		}
	}

	private ArrayList<String> findAnograms() throws Exception {
		ArrayList<String> result = new ArrayList<>();
		// char lastChar = sortedString.charAt(sortedString.length() - 1);

		try {

			while (byteBuffer.hasRemaining()) {
				byte[] nextWord = loadNextWord(searchBytes.length);
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
