package com.luanvv.taskdemo.model;

public class Metric {
	private float height;
	private float weight;

	public Metric() {
	}

	public Metric(float height, float weight) {
		this.height = height;
		this.weight = weight;
	}

	public float getHeight() {
		return this.height;
	}

	public float getWeight() {
		return this.weight;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public void setWeight(float weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "Metric [height=" + height + ", weight=" + weight + "]";
	}
	
}
