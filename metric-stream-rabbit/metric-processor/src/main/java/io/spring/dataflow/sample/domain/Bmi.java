package io.spring.dataflow.sample.domain;

public class Bmi {
	private final float value;

	public Bmi(float value) {
		this.value = value;
	}

	public static Bmi fromMetric(float height, float weight) {
		return new Bmi(weight * 703 / (height * height));
	}

	public float getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "Bmi [value=" + value + "]";
	}

}
