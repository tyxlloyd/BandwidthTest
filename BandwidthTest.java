import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BandwidthTest {

	public static void main(String[] args) throws IOException {
		if(args.length == 0) {
			System.err.println("An error has occurred, the error was: A URL was not given.");
			System.exit(1);
		}
		
		try {
			HttpURLConnection connection = createURL(args[0]);
			long bandwidth = measureKBPS(connection);
			printBandwidth(bandwidth);
		}

		// If the given url is invalid
		catch (MalformedURLException e) {
			System.err.println("Invalid URL, the error was: " + e.getMessage());
			System.exit(1);
		}

		// For all other exceptions
		catch (Exception e) {
			System.err.println("An error has occurred, the error was: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Measures connection bandwidth in KB/s and displays the result to the user
	 * 
	 * @param bandwidth The speed that will be displayed in kbps
	 */
	public static void printBandwidth(long speed) {
		System.out.println("Bandwidth: " + speed + "kb/s");
	}

	/**
	 * Creates an HttpURLConnection from the url given by the user
	 * 
	 * @param urlARG The url obtained from a command line argument
	 * @return An HttpURLConnection for the given url
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static HttpURLConnection createURL(String urlARG) throws IOException, MalformedURLException {

		// Create a url from args[0]
		URL givenURL = new URL(urlARG);
		HttpURLConnection connection = (HttpURLConnection) givenURL.openConnection();

		return connection;
	}

	/**
	 * Measures runtime in second and content length in kilobytes
	 * to determine bandwidth in kbps
	 * 
	 * @param connection The HttpURLConnection we are using to calculate bandwidth
	 * @return Bandwidth in kbps
	 * @throws Exception
	 */
	public static long measureKBPS(HttpURLConnection connection) throws IOException {

		long startTime = System.nanoTime();
		
		long kilobytes = 0;

		InputStream inputStream = connection.getInputStream();
		int bytes = 0;
		int bytesToSkip = 8192;
		boolean allBytesRead = false;
		
		// Count all bytes of the url
		while(!allBytesRead) {
			long bytesSkipped = inputStream.skip(bytesToSkip);
			bytes += bytesSkipped;
			if(bytesSkipped == 0) {
				allBytesRead = true;
			}
		}
			
		// Calculate the time it takes to fully load the url
		long endTime = System.nanoTime();
		long runTime = endTime - startTime;

		// Convert our runtime from nanoseconds to seconds
		double seconds = (double) runTime / Math.pow(10, 9);
		kilobytes = bytes;

		// Convert from bytes to kilobytes
		kilobytes = kilobytes / 1024;

		// Calculate kilobytes per second
		int kbps = (int) Math.ceil(kilobytes / seconds);
		connection.disconnect();
		return kbps;
	}

}