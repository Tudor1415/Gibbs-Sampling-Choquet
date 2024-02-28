package rules.mining.rules;

public interface IRule {
	int[] getX();
	int[] getY();
	int getFreqX();
	int getFreqY();
	int getFreqZ();
	
	String toString();
}
