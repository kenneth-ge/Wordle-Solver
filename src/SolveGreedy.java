import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolveGreedy {
	
	public static final int NUM_LINES = 5,
							NUM_GUESSES = 1;

	public static String[] words, validAnswers;
	public static Line[] lines;
	public static FixedSizePriorityQueue<Line> nextLines;
	
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("words.txt"));
		
		String line;
		List<String> words = new ArrayList<>();
		while((line = br.readLine()) != null) {
			words.add(line);
		}
		br.close();
		
		SolveGreedy.words = words.toArray(new String[0]);
		
		br = new BufferedReader(new FileReader("validanswers.txt"));
		List<String> validAnswers = new ArrayList<>();
		while((line = br.readLine()) != null) {
			/*if(getPattern("crate", line) == 0)*/
				if(getPattern("bekah", line) == 22000)
					if(getPattern("rynds", line) == 20)
						if(getPattern("olate", line) == 200)
							validAnswers.add(line);
		}
		
		if(validAnswers.size() < 50)
			System.out.println(validAnswers);
		
		if(validAnswers.size() == 1) {
			System.out.println("Answer is: " + validAnswers.get(0));
			return;
		}
				
		SolveGreedy.validAnswers = validAnswers.toArray(new String[0]);
		
		br.close();
		
		//change to consider more greedy lines
		nextLines = new FixedSizePriorityQueue<>(NUM_LINES);
		lines = new Line[NUM_LINES];
		
		for(int i = 0; i < NUM_LINES; i++) {
			lines[i] = new Line(SolveGreedy.validAnswers);
		}
		
		for(int i = 0; i < NUM_GUESSES; i++) {
			for(int j = 0; j < NUM_LINES; j++) {
				process(lines[j], i);
			}
			
			for(int j = 0; j < NUM_LINES; j++) {
				lines[j] = (Line) nextLines.data[j];
			}
			
			System.out.println("DID GUESS " + i + "!");
		}
		
		for(int i = 0; i < NUM_LINES; i++) {
			System.out.print("Line (" + lines[i].mean + "): ");
			for(String s: lines[i].guesses) {
				System.out.print(s + " ");
			}
			System.out.println();
		}
	}
	
	public static void process(Line input, int guessNum) {
		int wordsProcessed = 0;
		
		for(int wordIdx = 0/*input.searchStartIdx*/; wordIdx < words.length; wordIdx++) {
			String nextWord = words[wordIdx];
			
			if(nextLines.contains(new Line(input.guesses, nextWord)))
				continue;
			
			Line l = new Line(input);
			
			List<Integer> newIndices = new ArrayList<>();
			
			//process each section
			for(int i = 0; i < l.indices.size() - 1; i++) {
				int start = l.indices.get(i);
				int end = l.indices.get(i + 1);
				
				int pattern = getPattern(nextWord, l.words[start].word);
				l.words[start].pattern = pattern;
				
				for(int j = start + 1; j < end; j++) {
					int nextPattern = getPattern(nextWord, l.words[j].word);
					l.words[j].pattern = nextPattern;
					if(nextPattern != pattern) {
						pattern = nextPattern;
					}
				}
				
				Arrays.sort(l.words, start, end);
				
				pattern = l.words[start].pattern;
				newIndices.add(start);
				for(int j = start; j < end; j++) {
					int nextPattern = l.words[j].pattern;
					if(nextPattern != pattern) {
						newIndices.add(j);
						pattern = nextPattern;
					}
				}
				newIndices.add(end);
			}
			
			double mean = 0;
			double numElems = 0;
			
			double max = 0;
			
			for(int i = 0; i < newIndices.size() - 1; i++) {
				mean += newIndices.get(i + 1) - newIndices.get(i);
				numElems++;
				max = Math.max(max, newIndices.get(i + 1) - newIndices.get(i));
			}
			
			mean /= numElems;
			
			l.mean = max;
			l.guesses[guessNum] = nextWord;
			l.indices = newIndices;
			l.searchStartIdx = wordIdx + 1;
			
			nextLines.add(l);
			
			wordsProcessed++;
			
			if(wordsProcessed % 1000 == 0) {
				System.out.println("Processed " + wordsProcessed + " words");
			}
		}
	}
	
	final static int[] NULLS = new int[128];
	static int[] letterDiff = new int[128];
	static int[] patternHolder = new int[5];
	
	public static int getPattern(String guess, String word) {
		System.arraycopy(NULLS, 97, letterDiff, 97, 26);
	    for(int i = 0; i < 5; i++)
	    	patternHolder[i] = 0;

	    for (int i = 0; i < 5; i++) {
	        if (word.charAt(i) == guess.charAt(i)) {
	        	patternHolder[i] = 1;
	        }
	        else {
	            letterDiff[word.charAt(i)]++;
	        }
	    }

	    for (int i = 0; i < 5; i++) {
	        if (patternHolder[i] != 1) {
	            if (letterDiff[guess.charAt(i)] > 0) {
	            	patternHolder[i] = 2;
	                letterDiff[guess.charAt(i)]--;
	            }
	        }
	    }

	    int ret = 0;
	    int mult = 1;

	    for (int i = 0; i < 5; i++) {
	        ret += patternHolder[i] * mult;
	        mult *= 10;
	    }

	    return ret;
	}
	
	static class Line implements Comparable<Line> {
		public double mean;
		public Word[] words;
		public List<Integer> indices;
		public String[] guesses;
		public int searchStartIdx;
		
		public Line(double mean) {this.mean = mean;}
		
		/** 
		 * Copy words, guesses, indices
		 * */
		public Line(Line other) {
			this.words = new Word[other.words.length];
			System.arraycopy(other.words, 0, this.words, 0, other.words.length);
			this.guesses = new String[NUM_GUESSES];
			System.arraycopy(other.guesses, 0, this.guesses, 0, NUM_GUESSES);
			this.indices = new ArrayList<>(other.indices);
		}
		
		public Line(String[] inWords) {
			this.mean = Double.POSITIVE_INFINITY;
			this.words = new Word[inWords.length];
			for(int i = 0; i < inWords.length; i++)
				this.words[i] = new Word(inWords[i], 0);
			this.indices = new ArrayList<>(); indices.add(0); indices.add(this.words.length);
			this.guesses = new String[NUM_GUESSES];
		}
		
		public Line(String[] guesses2, String nextWord) {
			this.guesses = new String[guesses2.length];
			for(int i = 0; i < guesses2.length; i++) {
				if(guesses[i] == null) {
					guesses[i] = nextWord;
					break;
				}
			}
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof Line) {
				Line l = (Line) o;
				return Arrays.equals(l.guesses, this.guesses);
			}
			return false;
		}
		
		@Override
		public int compareTo(Line o) {
			return (int) Math.signum(mean - o.mean);
		}
		
		@Override
		public String toString() {
			return "mean: " + mean;
		}
	}
	
	static class Word implements Comparable<Word> {
		public String word;
		public int pattern;
		
		public Word(String word, int pattern) {
			this.word = word;
			this.pattern = pattern;
		}
		
		@Override
		public int compareTo(Word o) {
			return pattern - o.pattern;
		}
		
	}

}
