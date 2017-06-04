import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Callable;

public class CalculatorThread implements Callable<BigDecimal> {
	private int counter;
	private int tasksNumber;
	private int threads;
	private int totalNumberOfTasks;
	
	public CalculatorThread(int counter, int tasksNumber, int threads, int totalNumberOfTasks) {
		this.counter = counter;
		this.tasksNumber = tasksNumber;
		this.threads = threads;
		this.totalNumberOfTasks = totalNumberOfTasks;
	}
	
	private BigDecimal CalculateFactorial(int n) {
		BigDecimal result = BigDecimal.ONE;
		
		for (int i = 1; i <= n; i++) {
			BigDecimal multiplicant = new BigDecimal(i);
			result = result.multiply(multiplicant);
		}
		
		return result;
	}

	@Override
	public BigDecimal call() {
		BigDecimal threadResult = BigDecimal.ZERO;
		
		for (int i = 0; i < tasksNumber; i++) {
			int k = counter + i * threads;
			
			if (k > totalNumberOfTasks) {
				break;
			}
			
			BigDecimal numerator = new BigDecimal(2 * k + 1);
			BigDecimal denominator = CalculateFactorial(2 * k);
			BigDecimal currentNumber = numerator.divide(denominator, totalNumberOfTasks, RoundingMode.HALF_UP);
			threadResult = threadResult.add(currentNumber);
		}
		
		return threadResult;
	}
}
