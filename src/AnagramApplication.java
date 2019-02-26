import static java.lang.Character.toLowerCase;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

public class AnagramApplication {

	private static final ArrayList<String> RESULT = new ArrayList<>();
	private static final StringBuilder STRING_BUILDER = new StringBuilder();

	public static void main(String[] args) throws Exception {
		long startTime = System.nanoTime();
		findAnograms("C:\\Users\\elvis.napritson\\Desktop\\lemmad.txt", "automaks");
		long endTime = System.nanoTime();
		System.out.println(MICROSECONDS.convert(endTime - startTime, NANOSECONDS) + "," + String.join(",", RESULT));
	}

	public static String sortString(String word) {
		char[] chars = word.toCharArray();
		Arrays.sort(chars);
		return new String(chars); // do we need new String
	}

	public static boolean isAnagram(String source, String target) {
		return source.equals(sortString(target));
	}

	public static void findAnograms(String path, String word) throws Exception {
		String sortedString = sortString(word.toLowerCase());
		char lastChar = sortedString.charAt(sortedString.length() - 1);

		FileChannel fileChannel = (FileChannel) Files.newByteChannel(Paths.get(path), EnumSet.of(READ));

		MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
		boolean beggingOfNewLine = false;
		boolean readTillNewLine = false;
		for (int i = 0; i < mappedByteBuffer.limit(); i++) {
			char character = (char) (mappedByteBuffer.get() & 0xFF);

			if (beggingOfNewLine && character > lastChar) {
				break;
			} else {
				beggingOfNewLine = false;
			}

			if (character == '\n' || character == '\r') {
				beggingOfNewLine = true;
				if (STRING_BUILDER.length() > 0) {
					String target = STRING_BUILDER.toString();
					if (STRING_BUILDER.length() == sortedString.length() && isAnagram(sortedString, target) && !word.equalsIgnoreCase(target)) {
						RESULT.add(target);
					}
					STRING_BUILDER.setLength(0);

				}
				readTillNewLine = false;
				continue;
			}
			if (readTillNewLine) {
				continue;
			}
			if (sortedString.indexOf(toLowerCase(character)) == -1 || STRING_BUILDER.length() + 1 > sortedString.length()) {
				readTillNewLine = true;
				STRING_BUILDER.setLength(0);
			} else {
				STRING_BUILDER.append(character);
			}

		}
	}

}
