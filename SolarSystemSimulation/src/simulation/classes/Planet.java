package simulation.classes;

import javafx.scene.paint.Color;


public class Planet {
	
	public static final double G = 6.674e-11;					// gravitational constant
	public static final double SCALED_G = 39.4767;				// scaled gravitational constant using: AU^3 / (SUN_MASS * ONE_DAY^2)
	public static final double A_U = 1;							// average distance between the Earth and the Sun in meters
	public static final double ONE_YEAR = 1;					// one Earth year in seconds
	public static final double ONE_DAY = 0.00274;				// one day in seconds
	public static final double SUN_MASS = 1;					// mass of Sun in kilograms
	public static final double EARTH_MASS = 3.003e-6;			// mass of Earth in kilograms (0.000003003)
	public static final double MERCURY_MASS = 1.652e-7;			// mass of Mercury in kilograms
	public static final double VENUS_MASS = 2.447e-6;			// mass of Venus in kilograms (0.000002447)
	public static final double MARS_MASS = 3.213e-7;			// mass of Mars in kilograms
	
	private String name;		// planet's name
	private double posX;		// x-coordinate
	private double posY;		// y-coordinate
	private double oriPosX;		// planet's original x-coordinate
	private double oriPosY;		// planet's original y-coordinate
	private double prevPosX;	// planet's previous x-coordinate
	private double prevPosY;	// planet's previous y-coordinate
	private double velX;		// x-axis velocity component
	private double velY;		// y-axis velocity component
	private double oriVelX;		// planet's original x-velocity
	private double oriVelY;		// planet's original y-velocity
	private double forceX;		// x-axis force component
	private double forceY;		// y-axis force component
	private double mass;		// planet's mass
	private Color color;		// colour filling
	private Color oriColor;		// planet's original colour
	private double size;		// displayed planet's size
	
	/**
	 * Empty constructor
	 */
	public Planet(){
		name 	= "";
		posX 	= 0.0;
		posY 	= 0.0;
		oriPosX = 0.0;
		oriPosY = 0.0;
		prevPosX= 0.0;
		prevPosY= 0.0;
		velX 	= 0.0;
		velY 	= 0.0;
		oriVelX = 0.0;
		oriVelY = 0.0;
		forceX 	= 0.0;
		forceY 	= 0.0;
		mass 	= 0.0;
		color 	= null;
		oriColor= null;
		size	= 0.0;
	}
	
	/**
	 * @param name Planet's name
	 * @param posX Planet's x-coordinate
	 * @param posY Planet's y-coordinate
	 * @param velX Planet's velocity along x-axis
	 * @param velY Planet's velocity along y-axis
	 * @param forceX Planet's force's x-component
	 * @param forceY Planet's force's y-component
	 * @param mass Planet's mass
	 * @param color Planet's colour filling
	 */
	public Planet(String name, double posX, double posY, double velX, double velY, double forceX, double forceY, double mass, Color color, double size){
		this.name 		= name;
		this.posX 		= posX;
		this.posY 		= posY;
		this.oriPosX	= posX;
		this.oriPosY	= posY;
		this.prevPosX	= posX;
		this.prevPosY	= posY;
		this.velX 		= velX;
		this.velY 		= velY;
		this.oriVelX	= velX;
		this.oriVelY	= velY;
		this.forceX 	= forceX;
		this.forceY 	= forceY;
		this.mass 		= mass;
		this.color 		= color;
		this.oriColor	= color;
		this.size		= size;
	}
	
	/**
	 * @return Name of the planet
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name Planet's new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Planet's X-coordinate
	 */
	public double getPosX() {
		return posX;
	}

	/**
	 * @param posX Planet's new X-coordinate
	 */
	public void setPosX(double posX) {
		this.posX = posX;
	}

	/**
	 * @return Planet's Y-coordinate
	 */
	public double getPosY() {
		return posY;
	}

	/**
	 * @param posY Planet's new Y-coordinate
	 */
	public void setPosY(double posY) {
		this.posY = posY;
	}
	
	/**
	 * @return Planet's original X-coordinate
	 */
	public double getOriPosX(){
		return oriPosX;
	}
	
	/**
	 * @return Planet's original Y-coordinate
	 */
	public double getOriPosY(){
		return oriPosY;
	}
	
	/**
	 * @return the prevPosX
	 */
	public double getPrevPosX() {
		return prevPosX;
	}

	/**
	 * @param prevPosX the prevPosX to set
	 */
	public void setPrevPosX(double prevPosX) {
		this.prevPosX = prevPosX;
	}

	/**
	 * @return the prevPosY
	 */
	public double getPrevPosY() {
		return prevPosY;
	}

	/**
	 * @param prevPosY the prevPosY to set
	 */
	public void setPrevPosY(double prevPosY) {
		this.prevPosY = prevPosY;
	}

	/**
	 * @return Planet's velocity along X-axis
	 */
	public double getVelX() {
		return velX;
	}

	/**
	 * @param velX Planet's new velocity along X-axis
	 */
	public void setVelX(double velX) {
		this.velX = velX;
	}

	/**
	 * @return Planet's velocity along Y-axis
	 */
	public double getVelY() {
		return velY;
	}

	/**
	 * @param velY Planet's new velocity along Y-axis
	 */
	public void setVelY(double velY) {
		this.velY = velY;
	}
	
	/**
	 * @return Planet's original velocity along X-axis
	 */
	public double getOriVelX(){
		return this.oriVelX;
	}
	
	/**
	 * @return Planet's original velocity along Y-axis
	 */
	public double getOriVelY(){
		return this.oriVelY;
	}

	/**
	 * @return Force acting on planet along X-axis
	 */
	public double getForceX() {
		return forceX;
	}

	/**
	 * @param forceX New force acting on planet along X-axis
	 */
	public void setForceX(double forceX) {
		this.forceX = forceX;
	}

	/**
	 * @return Force acting on planet along Y-axis
	 */
	public double getForceY() {
		return forceY;
	}

	/**
	 * @param forceY New force acting on planet along Y-axis
	 */
	public void setForceY(double forceY) {
		this.forceY = forceY;
	}

	/**
	 * @return Planet's mass
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * @param mass Planet's new mass
	 */
	public void setMass(double mass) {
		this.mass = mass;
	}

	/**
	 * @return Planet's colour
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color Planet's new colour
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getOriColor() {
		return oriColor;
	}

	public void setOriColor(Color oriColor) {
		this.oriColor = oriColor;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	/**
	 * @param other Another planet
	 * @return Distance between the two planets
	 */
	
	public double getDistance(Planet other){
		double dx = other.getPosX()/15 - posX/15;
		double dy = other.getPosY()/15 - posY/15;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * @param other Another planet
	 * @return Force acting between the two planets
	 */
	
	public double getPairwiseForce(Planet other){
		return (SCALED_G * mass * other.getMass()) / (getDistance(other)*
														getDistance(other));
	}
	
	/**
	 * @param other Another planet
	 */
	
	public void addNewForces(Planet other){
		forceX += getPairwiseForce(other) * (other.getPosX()/15 - posX/15) / getDistance(other);
		forceY += getPairwiseForce(other) * (other.getPosY()/15 - posY/15) / getDistance(other);
	}
	
	/**
	 * @return Planet's accelerations along x-axis
	 */
	public double newAccelerationX(){
		return forceX / mass;
	}
	
	/**
	 * @return Planet's acceleration along y-axis
	 */
	public double newAccelerationY(){
		return forceY / mass;
	}
	
	/**
	 * @param timestep Time difference between each simulation update
	 */
	public void updateVelAndPos(double timestep){
		velX += timestep * newAccelerationX();
		velY += timestep * newAccelerationY();
		
		posX += 15*timestep * velX;
		posY += 15*timestep * velY;
	}
	
	/**
	 * Reset acting forces before new calculation
	 */
	public void resetForces(){
		forceX = 0.0;
		forceY = 0.0;
	}
	
	public void resetVelocities(){
		velX = 0.0;
		velY = 0.0;
	}
	
	public String toString(){
		return "Planet- " + this.name + "\nPosition- X:" + posX + "  Y:" + posY
				+ "\nVelocity- X:" + this.velX + "  Y:" + this.velY + "\nForce- X:" +
				this.forceX + "  Y:" + this.forceY + "\nMass- " + this.mass + "\nColour- ";
	}
	
}
