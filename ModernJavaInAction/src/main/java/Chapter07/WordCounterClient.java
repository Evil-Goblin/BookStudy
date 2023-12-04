package Chapter07;

import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class WordCounterClient {

    static final String SENTENCE =
            "Nel      mezzo del  cammin di nostra vita " +
                    "mi ritrovai in una selva oscura " +
                    "ch la dritta via era smarrita ";

    public static void main(String[] args) {
        System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");

        int wordCount = IntStream.range(0, SENTENCE.length())
                .mapToObj(SENTENCE::charAt)
                .reduce(new WordCounter(0, true),
                        WordCounter::accumulate,
                        WordCounter::combine)
                .getCounter();

        System.out.println("Found " + wordCount + " words");

        WordCounterSpliterator spliterator = new WordCounterSpliterator(SENTENCE);
        int spliteratorWordCount = StreamSupport.stream(spliterator, true)
                .reduce(new WordCounter(0, true),
                        WordCounter::accumulate,
                        WordCounter::combine)
                .getCounter();
        System.out.println("Found " + spliteratorWordCount + " words");
    }


    public static int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            } else {
                if (lastSpace) counter++;
                lastSpace = false;
            }
        }
        return counter;
    }
}
