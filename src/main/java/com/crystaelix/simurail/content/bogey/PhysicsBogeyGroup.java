package com.crystaelix.simurail.content.bogey;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;

import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackGraph;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;

public class PhysicsBogeyGroup {

	public final List<PhysicsBogeyBlockEntity> bogeys;
	public final BooleanList reverseStates;
	public final Component displayName;

	protected boolean invalidated = false;

	public PhysicsBogeyGroup(List<PhysicsBogeyBlockEntity> bogeys, BooleanList reverseStates, Component displayName) {
		this.bogeys = bogeys;
		this.reverseStates = reverseStates;
		this.displayName = displayName;
	}

	public PhysicsBogeyBlockEntity getFirst() {
		return bogeys.getFirst();
	}

	public PhysicsBogeyBlockEntity getLast() {
		return bogeys.getLast();
	}

	public boolean isFirstReversed() {
		return reverseStates.getBoolean(0);
	}

	public boolean isLastReversed() {
		return reverseStates.getBoolean(reverseStates.size() - 1);
	}

	public PhysicsBogeyAxle getLeadingAxle() {
		return getFirst().getAxle(!isFirstReversed());
	}

	public PhysicsBogeyAxle getTrailingAxle() {
		return getLast().getAxle(isLastReversed());
	}

	public float getSteerValue(PhysicsBogeyBlockEntity target) {
		float steer = target.getSteerValue();
		if(steer != 0) {
			return steer;
		}
		int index = bogeys.indexOf(target);
		boolean isRevered = reverseStates.getBoolean(index);
		for(int i = 1;; ++i) {
			int frontIndex = index - i;
			int backIndex = index + i;
			if(frontIndex < 0 && backIndex >= bogeys.size()) break;

			float front = frontIndex >= 0 ? bogeys.get(frontIndex).getSteerValue() : 0;
			float back = backIndex < bogeys.size() ? bogeys.get(backIndex).getSteerValue() : 0;
			if(front == 0 && back == 0) continue;

			boolean frontReversed = frontIndex >= 0 ? reverseStates.getBoolean(frontIndex) : isRevered;
			boolean backReversed = frontIndex >= 0 ? reverseStates.getBoolean(frontIndex) : isRevered;
			if(frontReversed != isRevered) front *= -1;
			if(backReversed != isRevered) back *= -1;

			if(front == 0) steer = back;
			else if(back == 0) steer = front;
			else steer = (front + back) / 2;

			if(steer != 0) return steer;
		}
		return 0;
	}

	public float getBrakeStrength(PhysicsBogeyBlockEntity target) {
		float steer = target.getBrakeStrength();
		if(steer > 0) {
			return steer;
		}
		int index = bogeys.indexOf(target);
		for(int i = 1;; ++i) {
			int frontIndex = index - i;
			int backIndex = index + i;
			if(frontIndex < 0 && backIndex >= bogeys.size()) break;

			float front = frontIndex >= 0 ? bogeys.get(frontIndex).getBrakeStrength() : 0;
			float back = backIndex < bogeys.size() ? bogeys.get(backIndex).getBrakeStrength() : 0;
			if(front <= 0 && back <= 0) continue;

			return Math.max(front, back);
		}
		return 0;
	}

	public TrackGraph getTrackGraph() {
		if(invalidated) {
			return null;
		}
		TrackGraph frontGraph = getFirst().getAxle(!isFirstReversed()).getTrackGraph();
		TrackGraph backGraph = getLast().getAxle(isLastReversed()).getTrackGraph();
		if(frontGraph == null || backGraph == null || frontGraph != backGraph) {
			return null;
		}
		return frontGraph;
	}

	public TravellingPoint getLeadingPoint() {
		return getFirst().getAxle(!isFirstReversed()).getTrackPoint();
	}

	public TravellingPoint getTrailingPoint() {
		return getLast().getAxle(isLastReversed()).getTrackPoint();
	}

	public void invalidate() {
		invalidated = true;
		for(PhysicsBogeyBlockEntity bogey : bogeys) {
			bogey.group = null;
		}
	}

	public static PhysicsBogeyGroup createAndAssign(PhysicsBogeyBlockEntity source) {
		SequencedSet<PhysicsBogeyBlockEntity> chain = new LinkedHashSet<>();
		BooleanList reverseStates = new BooleanArrayList();

		chain.add(source);
		reverseStates.add(false);

		ObjectBooleanPair<PhysicsBogeyBlockEntity> connection = nextBogey(source, true);
		while(connection != null) {
			PhysicsBogeyBlockEntity curr = connection.left();
			if(chain.contains(curr)) {
				break;
			}
			boolean toFront = connection.rightBoolean();
			chain.addFirst(curr);
			reverseStates.add(0, toFront);
			connection = nextBogey(curr, !toFront);
		}
		connection = nextBogey(source, false);
		while(connection != null) {
			PhysicsBogeyBlockEntity curr = connection.left();
			if(chain.contains(curr)) {
				break;
			}
			boolean toFront = connection.rightBoolean();
			chain.addLast(curr);
			reverseStates.add(!toFront);
			connection = nextBogey(curr, !toFront);
		}

		int size = reverseStates.size();
		if(chain.getFirst().getBlockPos().compareTo(chain.getLast().getBlockPos()) > 0) {
			chain = chain.reversed();
			for(int i = 0, j = size - 1, mid = size / 2; i < mid; ++i, --j) {
				reverseStates.set(i, reverseStates.set(j, reverseStates.getBoolean(i)));
			}
		}
		if(reverseStates.getBoolean(0) ? chain.getFirst().connectionBack != null : chain.getFirst().connectionFront != null) {
			for(int i = 0; i < size; ++i) {
				reverseStates.set(i, !reverseStates.getBoolean(i));
			}
		}
		Component displayName = chain.stream().
				filter(Nameable::hasCustomName).
				findFirst().map(Nameable::getDisplayName).
				orElseGet(chain.getFirst()::getDisplayName);

		PhysicsBogeyGroup group = new PhysicsBogeyGroup(List.copyOf(chain), BooleanList.of(reverseStates.toBooleanArray()), displayName);
		for(PhysicsBogeyBlockEntity bogey : chain) {
			bogey.group = group;
		}
		return group;
	}

	static ObjectBooleanPair<PhysicsBogeyBlockEntity> nextBogey(PhysicsBogeyBlockEntity bogey, boolean front) {
		Level level = bogey.getLevel();
		if(front) {
			if(bogey.connectionFront != null && level.getBlockEntity(bogey.connectionFront) instanceof PhysicsBogeyBlockEntity next) {
				return ObjectBooleanPair.of(next, bogey.connectionFrontToFront);
			}
		}
		else {
			if(bogey.connectionBack != null && level.getBlockEntity(bogey.connectionBack) instanceof PhysicsBogeyBlockEntity next) {
				return ObjectBooleanPair.of(next, bogey.connectionBackToFront);
			}
		}
		return null;
	}
}
