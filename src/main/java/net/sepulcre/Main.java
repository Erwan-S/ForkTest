package net.sepulcre;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sepulcre.business.GoogleSheetManager;
import net.sepulcre.model.EnumState;
import net.sepulcre.model.Sentence;
import net.sepulcre.util.RuneConverter;

public class Main {

	private static final boolean GENERATE_NAME = false;
	private static final boolean GENERATE_MP3 = false;
	private static final boolean RESET_ID = false;

	public static void main(String[] args) {
		String spreadsheetId = "1OapKxDh5cetydNjXS7r7a5Uq4H-kOvu76Cdnhe3-j7k";
		String range = "Séquences!A2:I";

		List<List<Object>> content;
		try {
			content = GoogleSheetManager.getValues(spreadsheetId, range);
			List<Sentence> sentences = new ArrayList<>();
			for (int i = 0; i < content.size(); i++) {
				String[] elements = content.get(i).toArray(new String[content.get(i).size()]);

				List<String> runes = Arrays.asList(elements[0].trim().split("\\+"));
				runes.replaceAll(String::trim);
				Collections.sort(runes);

				EnumState power = EnumState.getState(elements[1].trim());

				Sentence sentence;
				try {
					sentence = new Sentence(runes, power);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					continue;
				}

				Sentence screenSentence = checkScreening(sentences, sentence);
				if (screenSentence != null) {
					System.out.println("Sentence " + String.join("/", screenSentence.getRunes()) + "/"
							+ screenSentence.getPower().getLabel() + " screens or is screened by the sentence "
							+ String.join("/", sentence.getRunes()) + "/" + sentence.getPower().getLabel());
				}

				String existingId = elements.length > 8 ? elements[8].trim() : null;
				if (RESET_ID || existingId == null) {
					sentence.setId(makeSentenceId(sentences, sentence));
				} else {
					sentence.setId(Integer.valueOf(existingId.substring(0, 3)));
				}

				String coolDown = elements.length > 6 ? elements[6].trim() : "";
				if (!coolDown.isEmpty()) {
					sentence.setCoolDown(Integer.valueOf(coolDown));
				}

				// Check if this sentence is validated
				if ("ok".equalsIgnoreCase(elements.length > 4 ? elements[4].trim() : "")) {
					sentence.setValidated(true);
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

				if (GENERATE_NAME) {
					System.out.println(makeFilename(sentence));
				}

				if (GENERATE_MP3) {
					createMp3(sentence);
				}

//				if (!sentence.isValidated()) {
//					continue;
//				}
				
				int power = Integer.valueOf(sentence.getPower().getCode());
				availableSentencesByPowerLevel[power - 1].add("&seq" + i);

				writer.println("const sSequence seq" + i + " PROGMEM = { { " + sentence.getNextState().getCode()
						+ ", /* " + sentence.getPower().getLabel() + " */ " + sentence.getId() + " /* "
						+ sentence.getSuffixe() + " */, " + false + ", " + sentence.getCoolDown() + " }, ");
				writer.println("                                   " + sentence.getRunes().size() + ", ");
				writer.print("                                   { ");
				boolean first = true;
				for (String rune : sentence.getRunes()) {
					if (first) {
						first = false;
					} else {
						writer.print(", ");
					}
					writer.print("&" + rune + "Rune");
				}
				writer.println(" } };");
				// + "SEQ_" + i + "_RUNES_SIZE, &seq" + i + "Runes, &seq"
				// + i + "Result };");
				writer.println();

			}

			addSequencesDir(writer, availableSentencesByPowerLevel);

			writer.println("// Table of sequences by directory");
			writer.println(
					"const sDirSequence * const seq_dir[NB_DIRECTORY] PROGMEM = { &sequencesDir1, &sequencesDir2, &sequencesDir3 };");
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void addSequencesDir(PrintWriter writer, List<String>[] availableSentencesByPowerLevel) {
		for (int i = 0; i < 3; i++) {
			List<String> availableSentences = availableSentencesByPowerLevel[i];
			writer.println("const sDirSequence sequencesDir" + (i + 1) + " PROGMEM = {");
			writer.println("\t" + availableSentences.size() + ",");
			writer.println("\t{");
			writer.println("\t\t" + String.join(", ", availableSentences));
			writer.println("\t}");
			writer.println("};");
			writer.println();
		}
	}

	private static int makeSentenceId(List<Sentence> sentences, Sentence sentence) {
		Integer sentenceId = getExistingSentenceId(sentences, sentence);
		if (sentenceId == null) {
			sentenceId = getMaxId(sentences) + 1;
			// sentenceId = String.format("%02d", ++max);
		}
		return sentenceId;
	}

	private static Sentence checkScreening(List<Sentence> sentences, Sentence newSentence) {
		Sentence result = null;
		for (Sentence sentence : sentences) {
			if (sentence.getPower().equals(newSentence.getPower())) {
				result = sentence;
				int nbElement = Math.min(sentence.getRunes().size(), newSentence.getRunes().size());
				for (int i = 0; i < nbElement; ++i) {
					if (!sentence.getRunes().get(i).equals(newSentence.getRunes().get(i))) {
						result = null;
						break;
					}
				}
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	private static String makeFilename(Sentence sentence) {
		return makeFilename(sentence, true);
	}

	private static String makeFilename(Sentence sentence, boolean suffixe) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%03d", sentence.getId()));
		sb.append(sentence.getPower().getLabel().replaceAll("Æ", "A").charAt(0));
		if (suffixe) {
			sb.append("-");
			sb.append(sentence.getSuffixe());
			sb.append(".mp3");
		}
		return sb.toString();
	}

	private static void createMp3(Sentence sentence) {
		String text = String.join(" + ", sentence.getRunes()) + ", niveau " + sentence.getPower().getLabel();
		text = RuneConverter.fromVestenToFrench(text);
		if (sentence.getNextState() != null && !sentence.getNextState().equals(EnumState.NONE)) {
			text = text + ", changement de puissance vers " + sentence.getNextState().getLabel();
		}
		String fileName = makeFilename(sentence, false);
		String dir = String.format("%02d", Integer.valueOf(sentence.getPower().getCode()));
		List<String> cmdBalcon = Arrays
				.asList(new String[] { "balcon\\balcon.exe", "-t", text, "-id", "1036", "-o", "--raw" });
		List<String> cmdLame = Arrays.asList(new String[] { "balcon\\lame.exe", "-r", "-s", "6.2", "-m", "s", "-h",
				"-b", "192", "-", "result\\" + dir + "\\" + fileName + ".mp3\"" });
		try {
			File tmpFile = new File("result\\temp");
			tmpFile.createNewFile();

			Process processBalcon = new ProcessBuilder(cmdBalcon).redirectOutput(tmpFile).start();
			processBalcon.waitFor();

			Process processLame = new ProcessBuilder(cmdLame).redirectInput(tmpFile).start();
			processLame.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void initOutputFile(PrintWriter writer) {
		writer.println("#include \"config_sequence.h\"");
		writer.println();
	}

	private static int getMaxId(List<Sentence> sentences) {
		int result = 0;
		for (Sentence sentence : sentences) {
			result = Math.max(result, sentence.getId());
		}
		return result;
	}

	private static Integer getExistingSentenceId(List<Sentence> sentences, Sentence newSentence) {
		for (Sentence sentence : sentences) {
			if (sentence.isSameRunes(newSentence)) {
				return sentence.getId();
			}
		}
		return null;
	}

}
