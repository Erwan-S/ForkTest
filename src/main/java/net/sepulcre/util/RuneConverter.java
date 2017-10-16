package net.sepulcre.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RuneConverter {

	private static Map<String, String> frenchMap = new HashMap<>();
	
	static {
		frenchMap.put("varsel", "Intuition");
		frenchMap.put("stans", "Mélancolie");
		frenchMap.put("fjell", "Profondeur");
		frenchMap.put("herje", "Destruction");
		frenchMap.put("villskap", "Destin");
		frenchMap.put("ensomhet", "Ensemble");
		frenchMap.put("storsaed", "Orgueil");
		frenchMap.put("grenselos", "Mort");
		frenchMap.put("fornuft", "Changement");
		frenchMap.put("styrke", "Pouvoir");
		frenchMap.put("sterk", "Impunité");
		frenchMap.put("nod", "Passion");
		frenchMap.put("kjolig", "Affrontement");
		frenchMap.put("orga", "Orga");
		
	}
	public static String fromFrenchToVesten(String text) {
		for(Entry<String, String> entry : frenchMap.entrySet()) {
			text = text.replace(entry.getValue(), entry.getKey());
		}
		return text;
	}
	
	public static String fromVestenToFrench(String text) {
		for(Entry<String, String> entry : frenchMap.entrySet()) {
			text = text.replace(entry.getKey(), entry.getValue());
		}
		return text;
	}
	
}
