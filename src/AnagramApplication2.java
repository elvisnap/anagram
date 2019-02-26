import static java.nio.file.StandardOpenOption.READ;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnagramApplication2 {

	private static String libraryPath = "C:\\Users\\elvis.napritson\\Desktop\\lemmad.txt";
	private static String word = "a cappella";
	private static final ArrayList<String> RESULT = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		// initial();
		long startTime = System.currentTimeMillis();
		// new AnagramApplication().readFile5(libraryPath);
		readFile20();
		// RESULT.remove(word);
		System.out.println(RESULT);
		System.out.println(System.currentTimeMillis() - startTime);
	}

	public static String sortString(String word) {
		char[] chars = word.toCharArray();
		Arrays.sort(chars);
		return new String(chars);
	}

	public static boolean isAnogram(String source, String target) {
		return source.equals(sortString(target));
	}

	public void readFile5(String path) throws Exception {
		ExecutorService executor = Executors.newCachedThreadPool();
		String source = sortString(word);

		try {
			Path pathToRead = Paths.get(path);

			FileChannel fileChannel = (FileChannel) Files.newByteChannel(pathToRead, EnumSet.of(READ));

			MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
			StringBuilder builder = new StringBuilder();

			while (mappedByteBuffer.hasRemaining()) {
				char character = (char) mappedByteBuffer.get();

				if (character == '\n' || character == '\r') {
					if (builder.length() != 0) {
						// executor.execute(new MyRunnable(source, builder.toString()));
						builder.setLength(0);
					}
				} else {
					builder.append(character);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class MyRunnable implements Runnable {
		private String source;
		private String target;

		public MyRunnable(String source, String target) {
			this.source = source;
			this.target = target;
		}

		@Override
		public void run() {
			if (isAnogram(source, target)) {
				RESULT.add(target);
			}
		}
	}

	public static void readFileNew38() throws Exception {
		BufferedReader reader = Files.newBufferedReader(Paths.get(libraryPath), StandardCharsets.ISO_8859_1);
		while (reader.ready()) {
			System.out.println(reader.readLine());
		}
	}

	public static void readFileNew35() throws Exception {
		InputStream in = new BufferedInputStream(new FileInputStream(libraryPath));

		StringBuilder builder = new StringBuilder();
		int bite = 0;
		while (bite >= 0) {
			bite = in.read();
			char character = (char) bite;
			if (character == '\n' || character == '\r') {
				// if (builder.length() > 0 && isAnogram(word1, builder.toString())) {
				// System.out.println(builder.toString());
				// }
				builder.setLength(0);
			} else {
				builder.append(character);
			}
		}
	}

	public static void readFile40() throws Exception {
		String word1 = sortString(word);

		try (BufferedReader br = Files.newBufferedReader(Paths.get(libraryPath), StandardCharsets.ISO_8859_1)) {
			while (br.ready()) {
				br.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readFile20() throws Exception {
		String word1 = sortString(word);
		char lastCharOfSourceString = word1.charAt(word1.length() - 1);
		Path pathToRead = Paths.get(libraryPath);

		FileChannel fileChannel = (FileChannel) Files.newByteChannel(pathToRead, EnumSet.of(READ));

		MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
		StringBuilder builder = new StringBuilder();
		boolean beggingOfNewLine = false;
		boolean readTillNewLine = false;
		for (int i = 0; i < mappedByteBuffer.limit(); i++) {
			char character = (char) (mappedByteBuffer.get() & 0xFF);

			if (character == '\n' || character == '\r') {
				beggingOfNewLine = true;
				String target = builder.toString();
				if (builder.length() == word1.length() && isAnogram(word1, target)) {
					RESULT.add(target);
				}
				builder.setLength(0);
				readTillNewLine = false;
				continue;
			}
			if (readTillNewLine) {
				continue;
			}
			if (beggingOfNewLine) {
				if (character > lastCharOfSourceString) {
					break;
				}
				beggingOfNewLine = false;
			}
			builder.append(character);
			if (builder.length() > word1.length()) {
				readTillNewLine = true;
				builder.setLength(0);
			}
		}
	}

}
