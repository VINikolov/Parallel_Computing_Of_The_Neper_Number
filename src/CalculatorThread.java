import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Callable;

public class CalculatorThread implements Callable<BigDecimal> {
	private int counter;
	private int tasksNumber;
	private int threads;
	private int totalNumberOfTasks;
	private boolean quietMode;
	
	public CalculatorThread(int counter, int tasksNumber, int threads, int totalNumberOfTasks, boolean quietMode) {
		this.counter = counter;
		this.tasksNumber = tasksNumber;
		this.threads = threads;
		this.totalNumberOfTasks = totalNumberOfTasks;
		this.quietMode = quietMode;
	}
	
	private BigDecimal CalculateFactorial(int n, BigDecimal lastFact, int lastCounter) {
		BigDecimal result = lastFact;
		
		for (int i = lastCounter; i <= n; i++) {
			BigDecimal multiplicant = new BigDecimal(i);
			result = result.multiply(multiplicant);
		}
		
		return result;
	}

	@Override
	public BigDecimal call() {
		int threadNumber = counter + 1;
		if (!quietMode) {
			System.out.println("Thread " + threadNumber + " started.");			
		}
		
		long startTime = System.currentTimeMillis();
		BigDecimal threadResult = BigDecimal.ZERO;
		BigDecimal lastFact = BigDecimal.ONE;
		int lastCounter = 1;
		
		for (int i = 0; i < tasksNumber; i++) {
			int k = counter + i * threads;
			
			if (k > totalNumberOfTasks) {
				break;
			}
			
			BigDecimal numerator = new BigDecimal(2 * k + 1);
			BigDecimal denominator = CalculateFactorial(2 * k, lastFact, lastCounter);
			lastFact = denominator;
			lastCounter = 2 * k + 1;
			BigDecimal currentNumber = numerator.divide(denominator, totalNumberOfTasks, RoundingMode.HALF_UP);
			threadResult = threadResult.add(currentNumber);
		}
		
		long endTime = System.currentTimeMillis();
		if (!quietMode) {
			System.out.println("Thread " + threadNumber + " stopped.");
			System.out.println("Thread " + threadNumber + " execution time was: " + (endTime - startTime) + " milliseconds");			
		}
		
		return threadResult;
	}

}
