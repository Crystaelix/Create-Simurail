package com.crystaelix.simurail.api.bogey.menu;

import java.util.List;
import java.util.Optional;

public record BogeyMenuSelection(List<BogeyCategory<?>> path, Optional<BogeyEntry> entry, List<BogeyDataOptionValue<?>> optionValues) {

	public static final BogeyMenuSelection EMPTY = new BogeyMenuSelection(List.of(), Optional.empty(), List.of());
}
