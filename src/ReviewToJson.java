import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ReviewToJson {

	public static void main(String args[]) {

		class Item {

			public Item() {
				this.word_count = new HashMap<>();
			}

			public String sentiment;
			public Map<String, Long> word_count;

		}

		if (args.length < 2) {
			System.out
					.print("Two or more parameters.\n1: The desired output file name\n2-n: file names of review files");
			return;
		}

		String filename = args[0];

		BufferedReader br = null;

		// for each array: index at 0 : the word, index at 1 : the count, index
		// at 2 : the class
		List<Item> resultList = new LinkedList<Item>();

		try {

			for (int i = 1; i < args.length; i++) {
				String currentLine;
				br = new BufferedReader(new FileReader(args[i]));
				System.out.println("File loaded: " + args[i]);
				System.out.println("Extracting information");
				while ((currentLine = br.readLine()) != null) {
					String sentiment = currentLine.substring(currentLine.length() - 8, currentLine.length())
							.equals("positive") ? "1" : "0";
					currentLine = currentLine.substring(0, currentLine.length() - 17);
					String[] substr = currentLine.split("\\s+");
					Item item = new Item();
					item.sentiment = sentiment;
					for (String s : substr) {
						String[] ss = s.split(":");
						if (item.word_count.containsKey(ss[0]))
							item.word_count.put(ss[0], item.word_count.get(ss[0]) + Integer.valueOf(ss[1]));
						else
							item.word_count.put(ss[0], Long.valueOf(ss[1]));
					}
					resultList.add(item);
				}
				br.close();
			}

			System.out.println("Extraction done");
			System.out.println("Saving in file " + args[0] + ".json");

			ObjectMapper mapper = new ObjectMapper();

			mapper.writeValue(new File(filename + ".json"), resultList);

			System.out.println("Saved " + resultList.size() + " sentiments.");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
