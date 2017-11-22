package io.lemontree.utils;

public class PositionRange {

	private int start;
	private int end;
	
	public PositionRange(){}
	public PositionRange(int start, int end){
		this.start = start;
		this.end = end;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + start;
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
		PositionRange other = (PositionRange) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		return true;
	}
	
	
}
