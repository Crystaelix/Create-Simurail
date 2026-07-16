package com.crystaelix.simurail.api.control;

import com.crystaelix.simurail.api.math.SimurailMath;

public class PIDController implements FeedbackController {

	private double frequency;
	private double dampingRate;
	private double errorDecayRate;
	private double integralGain;

	private double lastTimeStep = 0;
	private double cachedErrorDecay = -1;
	private double error = 0;
	private double force = 0;

	public PIDController() {
	}

	public PIDController(double frequency, double dampingRate, double errorDecayRate, double integralGain) {
		this.frequency = Math.abs(frequency);
		this.dampingRate = Math.abs(dampingRate);
		this.errorDecayRate = Math.abs(errorDecayRate);
		this.integralGain = Math.abs(integralGain);
	}

	@Override
	public void setFrequency(double frequency) {
		this.frequency = Math.abs(frequency);
	}

	@Override
	public void setDampingRate(double dampingRate) {
		this.dampingRate = Math.abs(dampingRate);
	}

	public void setErrorDecayRate(double errorDecayRate) {
		cachedErrorDecay = -1;
		this.errorDecayRate = Math.abs(errorDecayRate);
	}

	public void setIntegralGain(double integralGain) {
		this.integralGain = Math.abs(integralGain);
	}

	private double getErrorDecay(double timeStep) {
		if(cachedErrorDecay < 0 || Math.abs(lastTimeStep - timeStep) > SimurailMath.EPSILON) {
			lastTimeStep = timeStep;
			cachedErrorDecay = Math.exp(-timeStep * errorDecayRate);
		}
		return cachedErrorDecay;
	}

	@Override
	public double updateForce(double inertia, double offset, double velocity, double maxForce, double timeStep) {
		if(timeStep <= 0 || inertia <= 0) {
			error = 0;
			return force = 0;
		}
		double errorDecay = getErrorDecay(timeStep);
		error = error * errorDecay + offset * (errorDecayRate > SimurailMath.EPSILON ? (1 - errorDecay) / errorDecayRate : timeStep);
		double integralWindup = integralGain * error;
		double maxIntegralWindup = Math.abs(maxForce) / inertia * 0.75;
		if(Math.abs(integralWindup) > maxIntegralWindup) {
			integralWindup = Math.signum(integralWindup) * maxIntegralWindup;
			error = integralGain > SimurailMath.EPSILON ? Math.signum(error) * maxIntegralWindup / integralGain : 0;
		}
		double stiffness = frequency * frequency;
		double damping = 2 * frequency * dampingRate;
		double numerator = stiffness * offset - damping * velocity;
		double denominator = 1 + damping * timeStep + stiffness * timeStep * timeStep;
		force = inertia * (numerator / denominator + integralWindup);
		return force = Math.clamp(force, -Math.abs(maxForce), Math.abs(maxForce));
	}

	@Override
	public double getForce() {
		return force;
	}

	@Override
	public void reset() {
		error = 0;
		force = 0;
	}
}
