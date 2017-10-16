package net.sepulcre.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sepulcre.util.RuneConverter;

public class Sentence {

	private List<String> runes = new ArrayList<>();
	private final EnumState power;
	private final String suffixe;
	private int id;
	private EnumState nextState;
	private int coolDown;
	private boolean validated = true;

	public Sentence(List<String> runes, EnumState power) {
		for(String rune : runes) {
			this.runes.add(RuneConverter.fromFrenchToVesten(rune));
		}
		StringBuilder sb = new StringBuilder();
		for (String rune : this.runes) {
			sb.append(StringUtils.capitalize(rune));
		}
		this.suffixe = sb.toString();
		this.power = power;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isSameRunes(Sentence other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (runes == null) {
			if (other.runes != null) {
				return false;
			}
		} else if (!runes.equals(other.runes)) {
			return false;
		}
		return true;
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

	public EnumState getPower() {
		return power;
	}
	
	public String getSuffixe() {
		return suffixe;
	}

	public List<String> getRunes() {
		return runes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((power == null) ? 0 : power.hashCode());
		result = prime * result + ((runes == null) ? 0 : runes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Sentence other = (Sentence) obj;
		if (power != other.power) {
			return false;
		}
		if (runes == null) {
			if (other.runes != null) {
				return false;
			}
		} else if (!runes.equals(other.runes)) {
			return false;
		}
		return true;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

}
