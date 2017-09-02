package net.sepulcre;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sepulcre.model.EnumState;
import net.sepulcre.model.Sentence;

public class Main {

	public static void main(String[] args) {
		List<Sentence> sentences = new ArrayList<>();
		try {
			URI uri = Main.class.getResource("/Draft phrases Machine - Séquences.tsv").toURI();
			Object[] content = (Object[]) Files.lines(Paths.get(uri)).toArray();

			for (int i = 0; i < content.length; i++) {
				String line = (String) content[i];

				String[] elements = line.split("\t");

				// Check if this sentence is validated
				if (!"ok".equalsIgnoreCase(elements.length > 3 ? elements[4].trim() : "")) {
					continue;
				}

				List<String> runes = Arrays.asList(elements[0].trim().split("\\+"));
				runes.replaceAll(String::trim);
				Collections.sort(runes);

				int power = Integer.valueOf(EnumState.getState(elements[1].trim()).getCode());

				Sentence sentence = new Sentence(runes, power);

				Integer sentenceId = getExistingSentenceId(sentences, sentence);
				if (sentenceId == null) {
					sentenceId = getMaxId(sentences) + 1;
					// sentenceId = String.format("%02d", ++max);
				}
				sentence.setId(sentenceId);

				String coolDown = elements.length > 6 ? elements[6].trim() : "";
				if (!coolDown.isEmpty()) {
					sentence.setCoolDown(Integer.valueOf(coolDown));
				}

				EnumState nextState = EnumState.getState(elements.length > 7 ? elements[7].trim() : "");
				sentence.setNexState(nextState);

				sentences.add(sentence);
			}

			PrintWriter writer = new PrintWriter("config_sequence.c", "UTF-8");
			initOutputFile(writer);

			@SuppressWarnings("unchecked")
			List<String>[] availableSentencesByPowerLevel = (List<String>[]) new ArrayList[3];
			availableSentencesByPowerLevel[0] = new ArrayList<>();
			availableSentencesByPowerLevel[1] = new ArrayList<>();
			availableSentencesByPowerLevel[2] = new ArrayList<>();

			for (int i = 0; i < sentences.size(); i++) {
				Sentence sentence = sentences.get(i);

				availableSentencesByPowerLevel[sentence.getPower() - 1].add("&seq" + i);

				writer.print("const sItem *seq" + i + "Runes[] = { ");
				writer.print("&" + sentence.getRuneA() + "Rune, ");
				writer.print("&" + sentence.getRuneB() + "Rune");
				writer.println(" };");

				writer.println("#define SEQ_" + i + "_RUNES_SIZE (sizeof(seq" + i + "Runes)/sizeof(sItem*))");

				writer.println("const sResult seq" + i + "Result = { " + sentence.getNextState().getCode() + ", "
						+ (sentence.getPower() * 100 + sentence.getId()) + " /* " + sentence.getSuffixe()
						+ " */, true, " + sentence.getCoolDown() + " };");

				writer.println("const sSequence seq" + i + "[] = { SEQ_" + i + "_RUNES_SIZE, &seq" + i + "Runes, &seq"
						+ i + "Result };");
				writer.println();
			}

			for (int i = 0; i < 3; i++) {
				List<String> availableSentences = availableSentencesByPowerLevel[i];
				writer.println("const sDirSequence sequencesDir" + (i + 1) + " = {");
				writer.println("\t" + availableSentences.size() + ",");
				writer.println("\t{");
				writer.print("\t\t");
				writer.println(String.join(", ", availableSentences));
				writer.println("\t}");
				writer.println("};");
				writer.println();

			}

			writer.println("// Table of sequences by directory");
			writer.println(
					"const sDirSequence *seq_dir[NB_DIRECTORY] = { &sequencesDir1, &sequencesDir2, &sequencesDir3 };");
			writer.close();

		} catch (IOException |

				URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void initOutputFile(PrintWriter writer) {
		writer.println("#include \"config_sequence.h\"");
		writer.println("#include \"config_items.h\"");
		writer.println();
	}

	private static int getMaxId(List<Sentence> sentences) {
		int result = 1;
		for (Sentence sentence : sentences) {
			result = Math.max(result, sentence.getId());
		}
		return result;
	}

	private static Integer getExistingSentenceId(List<Sentence> sentences, Sentence newSentence) {
		for (Sentence sentence : sentences) {
			if (sentence.equals(newSentence)) {
				return sentence.getId();
			}
		}
		return null;
	}

}
