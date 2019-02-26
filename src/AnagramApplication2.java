import static java.nio.file.StandardOpenOption.READ;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

public class AnagramApplication2 {

	private static final ArrayList<String> RESULT = new ArrayList<>();
	private static final StringBuilder STRING_BUILDER = new StringBuilder();
	private static MappedByteBuffer BYTE_BUFFER;

	public static void main(String[] args) throws Exception {
		long startTime = System.nanoTime();
		loadDictionary("C:\\Users\\elvis.napritson\\Desktop\\lemmad.txt");
		findAnograms("maja");
		long endTime = System.nanoTime();
		System.out.println(MICROSECONDS.convert(endTime - startTime, NANOSECONDS) + "," + String.join(",", RESULT));
	}

	private static String sortString(String word) {
		char[] chars = word.toCharArray();
		Arrays.sort(chars);
		return new String(chars); // do we need new String
	}

	private static boolean isAnagram(String source, String target) {
		return source.equals(sortString(target));
	}

	private static void loadDictionary(String path) throws Exception {
		FileChannel fileChannel = (FileChannel) Files.newByteChannel(Paths.get(path), EnumSet.of(READ));
		BYTE_BUFFER = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
	}

	private static byte[] loadNextWord(int bytesToRead) {
		byte[] dst = new byte[bytesToRead];
		while (true) {
			BYTE_BUFFER.get(dst, 0, bytesToRead);
			if (isLineEndReached()) {
				return dst;
			}
		}
	}

	private static boolean isLineEndReached() {
		return BYTE_BUFFER.get() == '\r';
	}

	private static void readTillEndOfTheLine() {
		byte b = BYTE_BUFFER.get();
		while (!isLineEndReached()) {
			b = BYTE_BUFFER.get();
		}
	}

	private static boolean isLineEnd(byte b) {
		return b == '\n' || b == '\r';
	}

	private static void findAnograms(String word) throws Exception {
		String sortedString = sortString(word.toLowerCase());
		char lastChar = sortedString.charAt(sortedString.length() - 1);

		try {

			while (BYTE_BUFFER.hasRemaining()) {
				byte[] nextWord = loadNextWord(sortedString.length());
				// System.out.println(readNextWord);

			}
		} catch (IndexOutOfBoundsException | BufferUnderflowException e) {
			e.printStackTrace();
		}

		boolean beggingOfNewLine = false;
		boolean readTillNewLine = false;
		// for (int i = 0; i < BYTE_BUFFER.limit(); i++) {
		// char character = (char) (BYTE_BUFFER.get() & 0xFF);
		//
		// if (beggingOfNewLine && character > lastChar) {
		// break;
		// } else {
		// beggingOfNewLine = false;
		// }
		//
		// if (character == '\n' || character == '\r') {
		// beggingOfNewLine = true;
		// String target = STRING_BUILDER.toString();
		// if (STRING_BUILDER.length() == sortedString.length() &&
		// isAnagram(sortedString, target) && !word.equalsIgnoreCase(target)) {
		// RESULT.add(target);
		// }
		// STRING_BUILDER.setLength(0);
		// readTillNewLine = false;
		// continue;
		// }
		// if (readTillNewLine) {
		// continue;
		// }
		// if (sortedString.indexOf(toLowerCase(character)) == -1 ||
		// STRING_BUILDER.length() + 1 > sortedString.length()) {
		// readTillNewLine = true;
		// STRING_BUILDER.setLength(0);
		// } else {
		// STRING_BUILDER.append(character);
		// }
		//
		// }
	}

}
