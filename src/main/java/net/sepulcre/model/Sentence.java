package net.sepulcre.model;

import java.util.List;

public class Sentence {

	private String runeA, runeB;
	private final int power;
	private final String suffixe;
	private String id;
	private EnumState nextState;
	private int coolDown;

	public Sentence(List<String> runes, int power) {
		runeA = fromFrenchToVesten(runes.get(0));
		runeB = fromFrenchToVesten(runes.get(1));
		suffixe = runeA.substring(0, 3) + runeB.substring(0, 3);
		if (power == 0) {
			throw new IllegalArgumentException(runeA + "/" + runeB + "/" + power);
		}
		this.power = power;
	}

//	public boolean isSameRunes(Sentence other) {
//		if ((runeA.equals(other.runeA) && runeB.equals(other.runeB))
//				|| (runeA.equals(other.runeB) && runeB.equalsIgnoreCase(other.runeA))) {
//			return true;
//		}
//		return false;
//	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((runeA == null) ? 0 : runeA.hashCode());
		result = prime * result + ((runeB == null) ? 0 : runeB.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sentence other = (Sentence) obj;
		if (runeA == null) {
			if (other.runeA != null)
				return false;
		} else if (!runeA.equals(other.runeA))
			return false;
		if (runeB == null) {
			if (other.runeB != null)
				return false;
		} else if (!runeB.equals(other.runeB))
			return false;
		return true;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	private static String fromFrenchToVesten(String line) {
		line = line.replaceAll("Intuition", "varsel");
		line = line.replaceAll("Mélancolie", "stans");
		line = line.replaceAll("Profondeur", "fjell");
		line = line.replaceAll("Destruction", "herje");
		line = line.replaceAll("Destin", "villskap");
		line = line.replaceAll("Ensemble", "ensomhet");
		line = line.replaceAll("Orgueil", "storsaed");
		line = line.replaceAll("Mort", "grenselos");
		line = line.replaceAll("Changement", "fornuft");
		line = line.replaceAll("Pouvoir", "styrke");
		line = line.replaceAll("Impunité", "sterk");
		line = line.replaceAll("Passion", "nod");
		line = line.replaceAll("Affrontement", "kjolig");
		line = line.replaceAll("Orga", "orga");

		return line;
	}
	
	public String getRuneA() {
		return runeA;
	}

	public String getRuneB() {
		return runeB;
	}

	public void setNexState(EnumState nextState) {
		this.nextState = nextState;
	}

	public EnumState getNextState() {
		return nextState;
	}
	
	public int getCoolDown() {
		return coolDown;
	}

	public void setCoolDown(int coolDown) {
		this.coolDown = coolDown;
	}

	public int getPower() {
		return power;
	}
	
	public String getSuffixe() {
		return suffixe;
	}

}
