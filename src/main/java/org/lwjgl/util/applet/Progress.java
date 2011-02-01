package org.lwjgl.util.applet;

public class Progress {

	private float total;

	private float current;

	private String status = "";

	public Progress(int total) {
		this.total = total;
		this.current = 0;
	}

	public void increment() {
		increment(1);
	}

	public void increment(float count) {
		current += count;
		if (current > total)
			current = total;
	}

	public void setCurrent(float current) {
		this.current = current;
	}

	public float getPercentage() {
		return 100 * current / total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void update(String status, int current) {
		setStatus(status);
		setCurrent(current);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}