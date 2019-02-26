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
	private static MappedByteBuffer BYTE_BUFFER;
	private static String searchWord;
	private static byte[] searchBytes;

	public static void main(String[] args) throws Exception {
		long startTime = System.nanoTime();
		searchWord = "maja";
		searchBytes = searchWord.getBytes();
		Arrays.sort(searchBytes);
		loadDictionary("C:\\Users\\elvis.napritson\\Desktop\\lemmad.txt");
		findAnograms();
		long endTime = System.nanoTime();
		System.out.println(MICROSECONDS.convert(endTime - startTime, NANOSECONDS) + "," + String.join(",", RESULT));
	}

	private static boolean isAnagram(byte[] target) {
		Arrays.sort(target);
		return Arrays.equals(searchBytes, target);
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
			} else {
				readTillEndOfTheLine();
			}
		}
	}

	private static boolean isLineEndReached() {
		byte b = BYTE_BUFFER.get();
		if (b == '\r') {
			BYTE_BUFFER.get();
			return true;
		}
		if (b == '\n') {
			return true;
		}
		return false;
	}

	private static void readTillEndOfTheLine() {
		while (!isLineEndReached()) {
		}
	}

	private static void findAnograms() throws Exception {
		// String sortedString = sort(searchWord.toLowerCase());
		// char lastChar = sortedString.charAt(sortedString.length() - 1);

		try {

			while (BYTE_BUFFER.hasRemaining()) {
				byte[] nextWord = loadNextWord(searchWord.length());
				if (isAnagram(nextWord.clone())) {
					RESULT.add(new String(nextWord));
				}
			}
		} catch (IndexOutOfBoundsException | BufferUnderflowException e) {
			// nothing to do
		}
	}

}
