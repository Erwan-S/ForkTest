package net.sepulcre.model;

public enum EnumState {
	
	KRONIEN ("Kronien", "1"), AETHERIC("Æthérique", "2"), DIVINE("Divin", "3"), NONE("", "END_OF_SEQUENCE_RESTART_ID");

	private final String label;
	private final String code;
	
	private EnumState(String label, String code) {
		this.label = label;
		this.code = code;
	}
	
	public static EnumState getState(String label) {
		for (EnumState state : values()) {
			if (state.label.equals(label)) {
				return state;
			}
		}
		throw new IllegalArgumentException(label);
	}
	
	public String getCode() {
		return code;
	}
	
}
