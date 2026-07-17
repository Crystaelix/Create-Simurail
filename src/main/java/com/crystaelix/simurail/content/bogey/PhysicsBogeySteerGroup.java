package com.crystaelix.simurail.content.bogey;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import net.minecraft.world.level.Level;

public record PhysicsBogeySteerGroup(List<PhysicsBogeyBlockEntity> bogeys, BooleanList orientation) {

	public float getSteerValue(PhysicsBogeyBlockEntity target) {
		float steer = target.getSteerValue();
		if(steer != 0) {
			return steer;
		}
		int index = bogeys.indexOf(target);
		boolean orient = orientation.getBoolean(index);
		for(int i = 1;; ++i) {
			int frontIndex = index - i;
			int backIndex = index + i;
			if(frontIndex < 0 && backIndex >= bogeys.size()) break;

			float front = frontIndex >= 0 ? bogeys.get(frontIndex).getSteerValue() : 0;
			float back = backIndex < bogeys.size() ? bogeys.get(backIndex).getSteerValue() : 0;
			if(front == 0 && back == 0) continue;

			boolean frontOrient = frontIndex >= 0 ? orientation.getBoolean(frontIndex) : orient;
			boolean backOrient = frontIndex >= 0 ? orientation.getBoolean(frontIndex) : orient;
			if(frontOrient != orient) front *= -1;
			if(backOrient != orient) back *= -1;

			if(front == 0) steer = back;
			else if(back == 0) steer = front;
			else steer = (front + back) / 2;

			if(steer != 0) return steer;
		}
		return 0;
	}

	public void invalidate() {
		for(PhysicsBogeyBlockEntity bogey : bogeys) {
			bogey.steerGroup = null;
		}
	}

	public static PhysicsBogeySteerGroup createAndUpdate(PhysicsBogeyBlockEntity source) {
		SequencedSet<PhysicsBogeyBlockEntity> chain = new LinkedHashSet<>();
		BooleanList orientation = new BooleanArrayList();

		chain.add(source);
		orientation.add(false);

		ObjectBooleanPair<PhysicsBogeyBlockEntity> connection = nextBogey(source, true);
		while(connection != null) {
			PhysicsBogeyBlockEntity curr = connection.left();
			if(chain.contains(curr)) {
				break;
			}
			boolean toFront = connection.rightBoolean();
			chain.addFirst(curr);
			orientation.add(0, toFront);
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
			orientation.add(!toFront);
			connection = nextBogey(curr, !toFront);
		}

		PhysicsBogeySteerGroup group = new PhysicsBogeySteerGroup(List.copyOf(chain), BooleanList.of(orientation.toBooleanArray()));
		for(PhysicsBogeyBlockEntity bogey : chain) {
			bogey.steerGroup = group;
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
