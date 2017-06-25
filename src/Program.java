import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Program {
	
	public static void Calculate(int numberOfTasks, int threads, String outputFileName, boolean quietMode) {
		BigDecimal result = BigDecimal.ZERO;
		boolean remainder = (numberOfTasks % threads != 0);
		int tasksNumber = numberOfTasks / threads + (remainder ? 1 : 0);
		
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		List<Future<BigDecimal>> resultsList = new ArrayList<>();
		for (int i = 0; i < threads; i++) {
			Callable<BigDecimal> callable = new CalculatorThread(i, tasksNumber, threads, numberOfTasks, quietMode);
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
		
		if (outputFileName.isEmpty()) {
			return;
		}
		
		File outputFile = new File(outputFileName);
		
		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		
		String resultStr = "e = " + result.toString();
		
		try {
			Files.write(Paths.get(outputFileName), resultStr.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		int tasks = 10000;
		int threads = 4;
		String outputFileName = "result.txt";
		boolean quietMode = false;
		
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-p":
				tasks = Integer.parseInt(args[++i]);
				break;
			case "-t":
				threads = Integer.parseInt(args[++i]);
				break;
			case "-o":
				outputFileName = args[++i];
				break;
			case "-q":
				quietMode = true;
				break;
			default:
				break;
			}
		}
		
		Calculate(tasks, threads, outputFileName, quietMode);
		long endTime = System.currentTimeMillis();
		
		if (!quietMode) {
			System.out.println("Threads used in current run: " + threads);			
		}
		
		System.out.println("Total execution time: " + (endTime - startTime) + " milliseconds");
	}

}
