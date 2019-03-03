package com.anagram;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnagramSearch {

	private static final String ENCODING = "windows-1257";
	private static final Charset CHARSET = Charset.forName(ENCODING);
	private static final int CR = 13;
	private static final int LF = 10;
	private byte[] fileData;
	private byte[] potentialTargetWord;
	private byte[] sortedSourceWord;
	private byte sourceChar;
	private byte currentChar;
	private int currentPosition = 0;
	private Map<Integer, Integer> foundCharPositions = new HashMap<>();
	private Set<Integer> uniqueSourceChars = new HashSet<Integer>();
	private Set<String> result = new HashSet<String>();

	public static void main(String[] args) throws Exception {
		long startTime = nanoTime();
		Set<String> result = new AnagramSearch().exec(args[0], args[1]);
		long stopTime = nanoTime();
		System.out.println(MICROSECONDS.convert(stopTime - startTime, NANOSECONDS) + "," + String.join(",", result));
	}

	public Set<String> exec(String path, String wordToSearch) throws Exception {
		if (wordToSearch.length() > 32 || wordToSearch.length() < 2) {
			return result;
		}
		loadDictionary(path);
		if (wordToSearch.length() > fileData.length) {
			return result;
		}
		init(wordToSearch);
		findAnagrams();
		result.remove(wordToSearch);
		return result;
	}

	private void init(String wordToSearch) {
		sortedSourceWord = wordToSearch.toLowerCase().getBytes();
		Arrays.sort(sortedSourceWord);
		potentialTargetWord = new byte[sortedSourceWord.length];
		for (int i = 0; i < sortedSourceWord.length; i++) {
			int c = sortedSourceWord[i] & 0xff;
			if (Character.isLetter(c)) {
				uniqueSourceChars.add(c);
			}
		}
	}

	private void findAnagrams() {
		if (uniqueSourceChars.size() > 20) {
			readForward(0, fileData.length - 1);
			return;
		}
		for (int c : uniqueSourceChars) {
			find(c);
			find(Character.toUpperCase(c));
		}
	}

	private void find(int c) {
		sourceChar = (byte) c;
		Integer foundPosition = foundCharPositions.get(c);
		if (foundPosition == null) {
			binarySearch(0, fileData.length - 1, new HashSet<Integer>());
		} else {
			readBackwards(foundPosition);
			readForward(foundPosition);
		}
	}

	private void binarySearch(int startIndex, int endIndex, Set<Integer> foundPositions) {
		if (endIndex - startIndex < 256) {
			readForward(startIndex, endIndex);
			return;
		}
		currentPosition = (startIndex + endIndex) >> 1;

		if (!isPositionSuitable() || !foundPositions.add(currentPosition)) {
			return;
		}

		foundCharPositions.put(currentChar & 0xff, currentPosition);

		if (currentChar == sourceChar) {
			int position = currentPosition;
			readBackwards(position);
			readForward(position);
		} else if ((currentChar & 0xff) > (sourceChar & 0xff)) {
			binarySearch(startIndex, currentPosition, foundPositions);
		} else {
			binarySearch(currentPosition, endIndex, foundPositions);
		}
	}

	private void readForward(int startIndex, int endIndex) {
		currentPosition = startIndex - 1;
		while (true) {
			for (int i = 0; i < potentialTargetWord.length; i++) {
				if (currentPosition + 1 > endIndex) {
					return;
				}
				readNextByte();
				if (currentChar == CR || currentChar == LF) {
					i = -1;
					continue;
				}
				potentialTargetWord[i] = currentChar;
			}
			if (isNextByteEndOfLine()) {
				checkForAnagram();
			} else {
				readTillEndOfTheLine();
			}
		}
	}

	private boolean isPositionSuitable() {
		try {
			currentChar = fileData[currentPosition];
			if (!isCurrentPositionLineEnd()) {
				readTillEndOfTheLine();
			}
			readNextByte();
			return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	private void readForward(int fromIndex) {
		currentPosition = fromIndex - 1;
		try {
			while (isNextPotentialWordLoaded()) {
				if (isNextByteEndOfLine()) {
					checkForAnagram();
				} else {
					readTillEndOfTheLine();
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// nothing to do file end reached
		}
	}

	private boolean isNextPotentialWordLoaded() {
		for (int i = 0; i < potentialTargetWord.length; i++) {
			readNextByte();
			if (currentChar == CR || currentChar == LF) {
				i = -1;
				continue;
			}
			if (i == 0 && currentChar != sourceChar) {
				return false;
			}
			potentialTargetWord[i] = currentChar;
		}
		return true;
	}

	private void readNextByte() {
		currentPosition++;
		currentChar = fileData[currentPosition];
	}

	private void readBackwards(int fromIndex) {
		potentialTargetWord[0] = sourceChar;
		currentPosition = fromIndex;
		try {
			while (loadNextWordBackwards()) {
				if (isLineBeginningReached()) {
					if (potentialTargetWord[0] != sourceChar) {
						break;
					}
					checkForAnagram();
				} else {
					readTillBeginningOfTheLine();
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// nothing to do
		}
	}

	private boolean loadNextWordBackwards() {
		for (int i = potentialTargetWord.length - 1; i >= 0; i--) {
			readPreviousByte();
			if (currentChar == CR || currentChar == LF) {
				i = potentialTargetWord.length;
			} else {
				potentialTargetWord[i] = currentChar;
				if (i != 0) {

					byte prevChar = fileData[currentPosition - 1];
					if ((prevChar == CR || prevChar == LF) && currentChar != sourceChar) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private void readPreviousByte() {
		currentPosition--;
		currentChar = fileData[currentPosition];
	}

	private void checkForAnagram() {
		byte[] target = potentialTargetWord.clone();
		toLowerCase(target);
		Arrays.sort(target);
		for (int i = 0; i < sortedSourceWord.length; i++) {
			if (sortedSourceWord[i] != target[i]) {
				return;
			}
		}

		result.add(new String(potentialTargetWord, CHARSET));
	}

	private void toLowerCase(byte[] target) {
		for (int i = 0; i < target.length; i++) {
			if (Character.isUpperCase(target[i] & 0xff)) {
				target[i] = (byte) Character.toLowerCase(target[i] & 0xff);
			}
		}
	}

	private void loadDictionary(String path) throws Exception {
		File file = new File(path);
		fileData = new byte[(int) file.length()];
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		dis.readFully(fileData);
		dis.close();
	}

	private boolean isNextByteEndOfLine() {
		byte b = fileData[currentPosition + 1];
		if (b == CR || b == LF) {
			return true;
		}
		return false;
	}

	private boolean isCurrentPositionLineEnd() {
		if (currentChar == CR) {
			readNextByte();
			return true;
		}
		if (currentChar == LF) {
			return true;
		}
		return false;
	}

	private boolean isLineBeginningReached() {
		if (currentPosition == 0) {
			return true;
		}
		readPreviousByte();
		if (currentChar == LF) {
			if (fileData[currentPosition - 1] == CR) {
				currentPosition--;
			}
			return true;
		}
		if (currentChar == CR) {
			return true;
		}
		return false;
	}

	private void readTillEndOfTheLine() {
		while (!isCurrentPositionLineEnd()) {
			readNextByte();
		}
	}

	private void readTillBeginningOfTheLine() {
		while (!isLineBeginningReached()) {
		}
	}
}
