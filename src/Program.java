import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Program {
	
	private static BigDecimal CalculateFactorial(int n) {
		BigDecimal result = BigDecimal.ONE;
		
		for (int i = 1; i <= n; i++) {
			BigDecimal multiplicant = new BigDecimal(i);
			result = result.multiply(multiplicant);
		}
		
		return result;
	}

	public static BigDecimal f1(int tasksNumber, int threads, int counter, int totalNumberOfTasks) {
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
	
	/////////////////////////////
	
	public static void Calculate(int numberOfTasks, int threads) {
		BigDecimal result = BigDecimal.ZERO;
		boolean remainder = (numberOfTasks % threads != 0);
		int tasksNumber = numberOfTasks / threads + (remainder ? 1 : 0);
		
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<Future<BigDecimal>> resultsList = new ArrayList<>();
		for (int i = 0; i < threads; i++) {
			Callable<BigDecimal> callable = new CalculatorThread(i, tasksNumber, threads, numberOfTasks);
			Future<BigDecimal> future = executor.submit(callable);
			resultsList.add(future);
		}
		
		for (Future<BigDecimal> res : resultsList) {
			try {
				result = result.add(res.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		executor.shutdown();
		
		System.out.println(result.toString());
	}

	public static void main(String[] args) {
		int p = 10000;
		int t = 4;
		
		long startTime = System.currentTimeMillis();
		//BigDecimal result = f1(p, t, 0, p);
		Calculate(p, t);
		long endTime = System.currentTimeMillis();

		//System.out.println(result);
		System.out.println("T" + t + " = " + (endTime - startTime));
	}

}
