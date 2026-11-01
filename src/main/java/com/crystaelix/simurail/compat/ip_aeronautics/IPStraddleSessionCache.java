package com.crystaelix.simurail.compat.ip_aeronautics;

import java.util.Set;
import java.util.UUID;

import com.crystaelix.simurail.mixin.compat.ip_aeronautics.IPStraddleSessionAccessor;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class IPStraddleSessionCache {

	private static final SetMultimap<UUID, IPStraddleSessionAccessor> SESSIONS = Multimaps.newSetMultimap(new Object2ObjectOpenHashMap<>(), ReferenceOpenHashSet::new);

	public static void addSession(IPStraddleSessionAccessor session) {
		SESSIONS.put(session.simurail$subLevel().getUniqueId(), session);
	}

	public static void removeSession(IPStraddleSessionAccessor session) {
		SESSIONS.remove(session.simurail$subLevel().getUniqueId(), session);
	}

	public static Set<IPStraddleSessionAccessor> getSessions(SubLevel subLevel) {
		return SESSIONS.get(subLevel.getUniqueId());
	}

	public static void removeSubLevel(UUID subLevelId) {
		SESSIONS.removeAll(subLevelId);
	}

	public static void clear() {
		SESSIONS.clear();
	}
}
