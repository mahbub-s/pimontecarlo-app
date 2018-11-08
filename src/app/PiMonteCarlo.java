package app;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

/**
 * implement the Monte Carlo algorithm as described in class or see link below.
 * this class implements the {@link Runnable} interface
 */
public class PiMonteCarlo implements Runnable {

	Consumer<double[]> callback = e -> {}; 
	
	/**
	 * width of the square.
	 */
	private static final double WIDTH = 1;

	/**
	 * Radius of the circle which is always half of squares {@link PiMonteCarloSkeleton#WIDTH}.
	 */
	private static final double RADIUS = WIDTH / 2;

	/**
	 * Convenience variable to be used in {@link PiMonteCarloSkeleton#containedInCircle()}.
	 */
	private static final double RADIUS_SQUARE = RADIUS * RADIUS;

	/**
	 * random generator. by placing a seed in {@link Random#Random(long)}
	 * we can repeat the same numbers which can be very helpful for debugging.
	 */
	private final Random RAND;

	/**
	 * maximum number of points to be generated.
	 */
	private long threshold;

	/**
	 * <p>
	 * create an {@link AtomicBoolean} as condition of keeping the thread alive, named alive</br>
	 * {@link AtomicBoolean} is thread safe, allowing multiple threads to change its value without race condition.</br>
	 * {@link AtomicBoolean} can be manipulated using {@link AtomicBoolean#get()}, {@link AtomicBoolean#set()}
	 * and other similar methods.</br>
	 * </p>
	 */
	private AtomicBoolean alive = new AtomicBoolean();

	/**
	 * <p>
	 * create two {@link LongAdder} as counters to keep track of dots in each square and circle.</br>
	 * {@link LongAdder} is thread safe, allowing multiple threads to change its value without race condition.</br>
	 * {@link LongAdder} can be manipulated using {@link LongAdder#longValue()}, {@link LongAdder#increment()}
	 * and other similar methods.</br>
	 * </p>
	 */
	LongAdder squareCtr = new LongAdder();
	LongAdder circleCtr = new LongAdder();

	/**
	 * <p>
	 * create a default constructor. you are to initialize {@link PiMonteCarloSkeleton#RAND}.</br>
	 * </p>
	 */
	public PiMonteCarlo (){
		super();
		RAND = new Random();
	}

	/**
	 * <p>
	 * complete {@link Runnable#run()} which is inherited from {@link Runnable}.</br>
	 * generate {@link PiMonteCarloSkeleton#threshold} points in a loop as long as {@link PiMonteCarloSkeleton#alive} is true</br>
	 * generate each point using {@link PiMonteCarloSkeleton#RAND} and {@link Random#nextDouble()}.</br>
	 * for each point generated call {@link LongAdder#increment()} for square counter and if
	 * this point is true for {@link PiMonteCarloSkeleton#containedInCircle()},
	 * call {@link LongAdder#increment()} for circle counter as well.</br>
	 * finally print your result in following format, you can use System.out.printf():</br>
	 * 
	 * <pre>
	 * 	S:1000, C:781, C/S:0.7810000000, PI:3.1240000000, Real PI:3.1415926536
	 * </pre>
	 * 
	 * in order: points in square, points in circle, {@link PiMonteCarloSkeleton#getRatio()},
	 * {@link PiMonteCarloSkeleton#getPI()}, real pi ({@link Math#PI}).</br>
	 * </p>
	 */
	@Override
	public void run() {
		// Auto-generated method stub
		while(alive.get() == true) {
			double x = RAND.nextDouble();
			double y = RAND.nextDouble();
			int circleOrSquare = 0;
			
			squareCtr.increment();
			
			if(containedInCircle(x, y) == true) {
				circleCtr.increment();
				circleOrSquare = 1;
			}
			
			if(squareCtr.sum() == threshold) {
				stop();
			}
			
			double[] dArray = new double[] {x, y, circleOrSquare};
			
			callback.accept(dArray);
		}
		
		
		System.out.printf("S:%d, C:%d, C/S: %.10f, PI: %.10f, Real PI: %.10f \n", squareCtr.intValue(), circleCtr.intValue(), getRatio(), getPI(), Math.PI);
		
	}
	
	/**
	 * <p>
	 * return the ratio of dots in circle over overall dots (dots in square).</br>
	 * divide the value of 2 {@link LongAdder} class variables, circle over square. </br>
	 * remember to use {@link LongAdder#doubleValue()}, dividing using long or int will remove the decimal precision.</br>
	 * </p>
	 * @return ratio of dots in circle over square
	 */
	public double getRatio(){
		return circleCtr.doubleValue()/squareCtr.doubleValue();
	}

	/**
	 * <p>
	 * using the {@link PiMonteCarloSkeleton#getRatio()} estimate the value of pi.</br>
	 * value of Pi is estimated using:
	 * 
	 * <pre>
	 * 	pi_estimate ~= Area of Circle/Area of Square = (pi*r^2)/(2*r)^2 
	 * 		=> (pi*r^2)/(4*r^2) = pi/4 => pi ~= pi_estimate*4
	 * </pre>
	 * 
	 * using pi ~= pi_estimate*4 and by getting pi_estimate from </br>
	 * {@link PiMonteCarloSkeleton#getRatio()} estimate the value of pi
	 * @return PI
	 */
	public double getPI(){
		return getRatio() * 4;
	}

	/**
	 * <p>
	 * check to see if a point is with or on the circle.</br>
	 * we know a circle with center of (j, k) and radius (r)is represented as:</br>
	 * 
	 * <pre>
	 * 	(x-j)^2 + (y-k)^2 = r^2
	 * </pre>
	 * 
	 * we want to shift our circle from center 0,0 to
	 * RADIUS,RADIUS so all of the circle is in positive range.</br>
	 * using the formula above as long as the left side is
	 * smaller and equal to right side (x,y) is in the circle.</br>
	 * </p>
	 * @param x - x coordinate of the point
	 * @param y - y coordinate of the point
	 * @return true of the point is in the circle
	 */
	private boolean containedInCircle( double x, double y){
		boolean inCircle = false;
		
		if(Math.pow(x-RADIUS, 2) + Math.pow(y-RADIUS, 2) <= RADIUS_SQUARE)
			inCircle = true;
		else
			inCircle = false;
				
		return inCircle;
	}
	
	/**
	 * <p>
	 * this method will start the thread which will start the point generation.</br>
	 * store the value of threshold in {@link PiMonteCarloSkeleton#threshold}.</br>
	 * use {@link PiMonteCarloSkeleton#setSeed(long)} to set new seed.</br>
	 * set {@link PiMonteCarloSkeleton#alive} to true to allow number generation condition to pass.</br>
	 * using {@link Thread} or {@link ExecutorService} class start the {@link PiMonteCarloSkeleton} thread. </br>
	 * {@link Thread} constructor takes a {@link Runnable} object and optional thread name as second argument. </br>
	 * upon calling {@link Thread#start()} on the Thread object the {@link Runnable#run()} method is called internally. </br>
	 * </p>
	 * @param threshold - number of points to be generated
	 * @param seed - to be used in {@link PiMonteCarloSkeleton#RAND} using {@link Random#setSeed()}
	 */
	public void simulate( long threshold, long seed) {
		this.threshold = threshold;
		setSeed(seed);
		alive.set(true);
		Thread thread = new Thread(this, "PiMonteCarlo");
		squareCtr.reset();
		circleCtr.reset();
		thread.start();
	}
	
	/**
	 * <p>
	 * chain to {@link PiMonteCarloSkeleton#simulate(long, long)} and use {@link System#currentTimeMillis()}
	 * as seed for second argument of {@link PiMonteCarloSkeleton#simulate(long, long)}.</br>
	 * </p>
	 * @param threshold - number of points to be generated
	 */
	public void simulate(long threshold) {
		simulate(threshold, System.currentTimeMillis());
	}
	
	public void simulate(long threshold, Consumer<double[]> callback) {
		if(callback != null) {
			this.callback = callback;
		}
		
		simulate(threshold);
	}

	/**
	 * <p>
	 * stop can be called to set {@link PiMonteCarloSkeleton#alive} to false.</br>
	 * forcing number generation to stop.
	 * </p>
	 */
	public void stop() {
		alive.set(false);
	}

	/**
	 * <p>
	 * set a new seed for {@link PiMonteCarloSkeleton#RAND} using {@link Random#setSeed()}.</br>
	 * </p>
	 * @param seed - to be used in {@link PiMonteCarloSkeleton#RAND} using {@link Random#setSeed()}
	 */
	public void setSeed(long seed) {
		RAND.setSeed(seed);
	}
}

