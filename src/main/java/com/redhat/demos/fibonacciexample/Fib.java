package com.redhat.demos.fibonacciexample;

import java.io.Serializable;

public class Fib implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3227839877551590262L;
	
	private int sequence;
	private long value;
	
	
	
	public Fib(int sequence, long value) {
		super();
		this.sequence = sequence;
		this.value = value;
	}
	
	public Fib(int sequence) {
	    super();
		this.sequence = sequence;
		this.value = -1;
	}
	
	public Fib(){
	    
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	
	public String toString() {
        return "Fibonacci(" + this.sequence + "/" + this.value + ")";
    }

}
