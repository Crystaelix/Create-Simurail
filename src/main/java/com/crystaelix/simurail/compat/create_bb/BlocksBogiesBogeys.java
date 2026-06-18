package com.crystaelix.simurail.compat.create_bb;

import java.util.List;

import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.api.bogey.menu.BogeyDataSelectionOption;
import com.crystaelix.simurail.api.bogey.menu.BogeyEntry;
import com.crystaelix.simurail.api.bogey.menu.BogeyEntryCategory;
import com.crystaelix.simurail.api.bogey.menu.BogeyMenuManager;
import com.crystaelix.simurail.api.bogey.menu.BogeyParentCategory;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.weido.create_bb.BlocksBogies;
import com.weido.create_bb.registry.BogieStyles;

import net.minecraft.network.chat.Component;

public class BlocksBogiesBogeys {

	public static final BogeyDataSelectionOption ROT = new BogeyDataSelectionOption(Component.translatable("simurail_bogey_option.create_bb.rot")).
			options(List.of(
					Component.translatable("simurail_bogey_option.create_bb.rot.normal"),
					Component.translatable("simurail_bogey_option.create_bb.rot.rotated"))).
			codec((extra, i) -> extra.putBoolean("IsForwards", i != 0),
					extra -> (extra.getBoolean("IsForwards") ? 1 : 0));

	public static final BogeyEntry
	STANDARD_1 = new BogeyEntry(
			BlocksBogies.asResource("standard/1"),
			small(BogieStyles.SINGLE_AXLE_BOGIE)),
	STANDARD_1_OFFSET = new BogeyEntry(
			BlocksBogies.asResource("standard/1_offset"),
			small(BogieStyles.SINGLE_AXLE_OFFSET),
			ROT),
	STANDARD_2 = new BogeyEntry(
			BlocksBogies.asResource("standard/2"),
			small(AllBogeyStyles.STANDARD)),
	STANDARD_3 = new BogeyEntry(
			BlocksBogies.asResource("standard/3"),
			small(BogieStyles.TRIPLE_AXLE_BOGIE)),
	STANDARD_4 = new BogeyEntry(
			BlocksBogies.asResource("standard/4"),
			small(BogieStyles.QUADRUPLE_AXLE_BOGIE)),
	STANDARD_5 = new BogeyEntry(
			BlocksBogies.asResource("standard/5"),
			small(BogieStyles.QUINTUPLE_AXLE_BOGIE));

	public static final BogeyEntryCategory STANDARD = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.standard"),
			List.of(STANDARD_1, STANDARD_1_OFFSET,
					STANDARD_2,
					STANDARD_3,
					STANDARD_4,
					STANDARD_5));

	public static final BogeyEntry
	TRAILING_1 = new BogeyEntry(
			BlocksBogies.asResource("trailing/1"),
			small(BogieStyles.SINGLE_AXLE_TRAILING)),
	TRAILING_2 = new BogeyEntry(
			BlocksBogies.asResource("trailing/2"),
			small(BogieStyles.DOUBLE_AXLE_TRAILING)),
	TRAILING_3 = new BogeyEntry(
			BlocksBogies.asResource("trailing/3"),
			small(BogieStyles.TRIPLE_AXLE_TRAILING)),
	TRAILING_4 = new BogeyEntry(
			BlocksBogies.asResource("trailing/4"),
			small(BogieStyles.QUADRUPLE_AXLE_TRAILING));

	public static final BogeyEntryCategory TRAILING = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.trailing"),
			List.of(TRAILING_1,
					TRAILING_2,
					TRAILING_3,
					TRAILING_4));

	public static final BogeyEntry
	WALSCHAERTS_LONG_L_1 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/large/1"),
			small(BogieStyles.SINGLE_AXLE_WALSCHAERTS_LONG),
			ROT),
	WALSCHAERTS_LONG_L_2 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/large/2"),
			small(BogieStyles.DOUBLE_AXLE_WALSCHAERTS_LONG),
			ROT),
	WALSCHAERTS_EXTENDED_L_2 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/extended/large/2"),
			small(BogieStyles.DOUBLE_AXLE_WALSCHAERTS_EXTRA_LONG),
			ROT),
	WALSCHAERTS_LONG_L_3 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/large/3"),
			small(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_LONG),
			ROT),
	WALSCHAERTS_LONG_L_3_SPACED = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/large/3_spaced"),
			small(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_EXTENDED_LONG),
			ROT),
	WALSCHAERTS_LONG_L_4 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/large/4"),
			small(BogieStyles.QUADRUPLE_AXLE_WALSCHAERTS_LONG),
			ROT),
	WALSCHAERTS_LONG_L_5 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/large/5"),
			small(BogieStyles.QUINTUPLE_AXLE_WALSCHAERTS_LONG),
			ROT),
	WALSCHAERTS_LONG_L_6 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/large/6"),
			small(BogieStyles.SEXTUPLE_AXLE_WALSCHAERTS_LONG),
			ROT);

	public static final BogeyEntryCategory WALSCHAERTS_LONG_L = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.walschaerts.long.large"),
			List.of(WALSCHAERTS_LONG_L_1,
					WALSCHAERTS_LONG_L_2, WALSCHAERTS_EXTENDED_L_2,
					WALSCHAERTS_LONG_L_3, WALSCHAERTS_LONG_L_3_SPACED,
					WALSCHAERTS_LONG_L_4,
					WALSCHAERTS_LONG_L_5,
					WALSCHAERTS_LONG_L_6));

	public static final BogeyEntry
	WALSCHAERTS_LONG_XL_1 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/extra_large/1"),
			large(BogieStyles.SINGLE_AXLE_WALSCHAERTS_LONG),
			ROT),
	WALSCHAERTS_LONG_XL_2 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/extra_large/2"),
			large(BogieStyles.DOUBLE_AXLE_WALSCHAERTS_LONG),
			ROT),
	WALSCHAERTS_EXTENDED_XL_2 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/extended/extra_large/2"),
			large(BogieStyles.DOUBLE_AXLE_WALSCHAERTS_EXTRA_LONG),
			ROT),
	WALSCHAERTS_LONG_XL_3 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/extra_large/3"),
			large(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_LONG),
			ROT),
	WALSCHAERTS_LONG_XL_3_SPACED = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/extra_large/3_spaced"),
			large(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_EXTENDED_LONG),
			ROT),
	WALSCHAERTS_LONG_XL_4 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/extra_large/4"),
			large(BogieStyles.QUADRUPLE_AXLE_WALSCHAERTS_LONG),
			ROT),
	WALSCHAERTS_LONG_XL_5 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/long/extra_large/5"),
			large(BogieStyles.QUINTUPLE_AXLE_WALSCHAERTS_LONG),
			ROT);

	public static final BogeyEntryCategory WALSCHAERTS_LONG_XL = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.walschaerts.long.extra_large"),
			List.of(WALSCHAERTS_LONG_XL_1,
					WALSCHAERTS_LONG_XL_2, WALSCHAERTS_EXTENDED_XL_2,
					WALSCHAERTS_LONG_XL_3, WALSCHAERTS_LONG_XL_3_SPACED,
					WALSCHAERTS_LONG_XL_4,
					WALSCHAERTS_LONG_XL_5));

	public static final BogeyEntry
	WALSCHAERTS_SHORT_L_2 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/short/large/2"),
			small(BogieStyles.DOUBLE_AXLE_WALSCHAERTS_SHORT),
			ROT),
	WALSCHAERTS_SHORT_L_3 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/short/large/3"),
			small(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_SHORT),
			ROT),
	WALSCHAERTS_SHORT_L_3_SPACED = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/short/large/3_spaced"),
			small(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_EXTENDED_SHORT),
			ROT),
	WALSCHAERTS_SHORT_L_4 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/short/large/4"),
			small(BogieStyles.QUADRUPLE_AXLE_WALSCHAERTS_SHORT),
			ROT),
	WALSCHAERTS_SHORT_L_5 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/short/large/5"),
			small(BogieStyles.QUINTUPLE_AXLE_WALSCHAERTS_SHORT),
			ROT),
	WALSCHAERTS_SHORT_L_6 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/short/large/6"),
			small(BogieStyles.SEXTUPLE_AXLE_WALSCHAERTS_SHORT),
			ROT);

	public static final BogeyEntryCategory WALSCHAERTS_SHORT_L = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.walschaerts.short.large"),
			List.of(WALSCHAERTS_SHORT_L_2,
					WALSCHAERTS_SHORT_L_3, WALSCHAERTS_SHORT_L_3_SPACED,
					WALSCHAERTS_SHORT_L_4,
					WALSCHAERTS_SHORT_L_5,
					WALSCHAERTS_SHORT_L_6));

	public static final BogeyEntry
	WALSCHAERTS_SHORT_XL_2 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/short/extra_large/2"),
			large(BogieStyles.DOUBLE_AXLE_WALSCHAERTS_SHORT),
			ROT),
	WALSCHAERTS_SHORT_XL_3 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/short/extra_large/3"),
			large(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_SHORT),
			ROT),
	WALSCHAERTS_SHORT_XL_3_SPACED = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/short/extra_large/3_spaced"),
			large(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_EXTENDED_SHORT),
			ROT),
	WALSCHAERTS_SHORT_XL_4 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/short/extra_large/4"),
			large(BogieStyles.QUADRUPLE_AXLE_WALSCHAERTS_SHORT),
			ROT),
	WALSCHAERTS_SHORT_XL_5 = new BogeyEntry(
			BlocksBogies.asResource("walschaerts/short/extra_large/5"),
			large(BogieStyles.QUINTUPLE_AXLE_WALSCHAERTS_SHORT),
			ROT);

	public static final BogeyEntryCategory WALSCHAERTS_SHORT_XL = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.walschaerts.short.extra_large"),
			List.of(WALSCHAERTS_SHORT_XL_2,
					WALSCHAERTS_SHORT_XL_3, WALSCHAERTS_SHORT_XL_3_SPACED,
					WALSCHAERTS_SHORT_XL_4,
					WALSCHAERTS_SHORT_XL_5));

	public static final BogeyParentCategory WALSCHAERTS = new BogeyParentCategory(
			Component.translatable("simurail_bogey_category.create_bb.walschaerts"),
			List.of(WALSCHAERTS_LONG_L, WALSCHAERTS_LONG_XL, WALSCHAERTS_SHORT_L, WALSCHAERTS_SHORT_XL));

	public static final BogeyEntry
	GEARLESS_LONG_L_1 = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/large/1"),
			small(BogieStyles.SINGLE_AXLE_LONG),
			ROT),
	GEARLESS_LONG_L_2 = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/large/2"),
			small(BogieStyles.DOUBLE_AXLE_LONG),
			ROT),
	GEARLESS_EXTENDED_L_2 = new BogeyEntry(
			BlocksBogies.asResource("gearless/extended/large/2"),
			small(BogieStyles.DOUBLE_AXLE_EXTRA_LONG),
			ROT),
	GEARLESS_LONG_L_3 = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/large/3"),
			small(BogieStyles.TRIPLE_AXLE_LONG),
			ROT),
	GEARLESS_LONG_L_3_SPACED = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/large/3_spaced"),
			small(BogieStyles.TRIPLE_AXLE_EXTENDED_LONG),
			ROT),
	GEARLESS_LONG_L_4 = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/large/4"),
			small(BogieStyles.QUADRUPLE_AXLE_LONG),
			ROT),
	GEARLESS_LONG_L_5 = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/large/5"),
			small(BogieStyles.QUINTUPLE_AXLE_LONG),
			ROT),
	GEARLESS_LONG_L_6 = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/large/6"),
			small(BogieStyles.SEXTUPLE_AXLE_LONG),
			ROT);

	public static final BogeyEntryCategory GEARLESS_LONG_L = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.gearless.long.large"),
			List.of(GEARLESS_LONG_L_1,
					GEARLESS_LONG_L_2, GEARLESS_EXTENDED_L_2,
					GEARLESS_LONG_L_3, GEARLESS_LONG_L_3_SPACED,
					GEARLESS_LONG_L_4,
					GEARLESS_LONG_L_5,
					GEARLESS_LONG_L_6));

	public static final BogeyEntry
	GEARLESS_LONG_XL_1 = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/extra_large/1"),
			large(BogieStyles.SINGLE_AXLE_LONG),
			ROT),
	GEARLESS_LONG_XL_2 = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/extra_large/2"),
			large(BogieStyles.DOUBLE_AXLE_LONG),
			ROT),
	GEARLESS_EXTENDED_XL_2 = new BogeyEntry(
			BlocksBogies.asResource("gearless/extended/extra_large/2"),
			large(BogieStyles.DOUBLE_AXLE_EXTRA_LONG),
			ROT),
	GEARLESS_LONG_XL_3 = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/extra_large/3"),
			large(BogieStyles.TRIPLE_AXLE_LONG),
			ROT),
	GEARLESS_LONG_XL_3_SPACED = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/extra_large/3_spaced"),
			large(BogieStyles.TRIPLE_AXLE_EXTENDED_LONG),
			ROT),
	GEARLESS_LONG_XL_4 = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/extra_large/4"),
			large(BogieStyles.QUADRUPLE_AXLE_LONG),
			ROT),
	GEARLESS_LONG_XL_5 = new BogeyEntry(
			BlocksBogies.asResource("gearless/long/extra_large/5"),
			large(BogieStyles.QUINTUPLE_AXLE_LONG),
			ROT);

	public static final BogeyEntryCategory GEARLESS_LONG_XL = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.gearless.long.extra_large"),
			List.of(GEARLESS_LONG_XL_1,
					GEARLESS_LONG_XL_2, GEARLESS_EXTENDED_XL_2,
					GEARLESS_LONG_XL_3, GEARLESS_LONG_XL_3_SPACED,
					GEARLESS_LONG_XL_4,
					GEARLESS_LONG_XL_5));

	public static final BogeyEntry
	GEARLESS_SHORT_L_2 = new BogeyEntry(
			BlocksBogies.asResource("gearless/short/large/2"),
			small(BogieStyles.DOUBLE_AXLE_SHORT),
			ROT),
	GEARLESS_SHORT_L_3 = new BogeyEntry(
			BlocksBogies.asResource("gearless/short/large/3"),
			small(BogieStyles.TRIPLE_AXLE_SHORT),
			ROT),
	GEARLESS_SHORT_L_3_SPACED = new BogeyEntry(
			BlocksBogies.asResource("gearless/short/large/3_spaced"),
			small(BogieStyles.TRIPLE_AXLE_EXTENDED_SHORT),
			ROT),
	GEARLESS_SHORT_L_4 = new BogeyEntry(
			BlocksBogies.asResource("gearless/short/large/4"),
			small(BogieStyles.QUADRUPLE_AXLE_SHORT),
			ROT),
	GEARLESS_SHORT_L_5 = new BogeyEntry(
			BlocksBogies.asResource("gearless/short/large/5"),
			small(BogieStyles.QUINTUPLE_AXLE_SHORT),
			ROT),
	GEARLESS_SHORT_L_6 = new BogeyEntry(
			BlocksBogies.asResource("gearless/short/large/6"),
			small(BogieStyles.SEXTUPLE_AXLE_SHORT),
			ROT);

	public static final BogeyEntryCategory GEARLESS_SHORT_L = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.gearless.short.large"),
			List.of(GEARLESS_SHORT_L_2,
					GEARLESS_SHORT_L_3, GEARLESS_SHORT_L_3_SPACED,
					GEARLESS_SHORT_L_4,
					GEARLESS_SHORT_L_5,
					GEARLESS_SHORT_L_6));

	public static final BogeyEntry
	GEARLESS_SHORT_XL_2 = new BogeyEntry(
			BlocksBogies.asResource("gearless/short/extra_large/2"),
			large(BogieStyles.DOUBLE_AXLE_SHORT),
			ROT),
	GEARLESS_SHORT_XL_3 = new BogeyEntry(
			BlocksBogies.asResource("gearless/short/extra_large/3"),
			large(BogieStyles.TRIPLE_AXLE_SHORT),
			ROT),
	GEARLESS_SHORT_XL_3_SPACED = new BogeyEntry(
			BlocksBogies.asResource("gearless/short/extra_large/3_spaced"),
			large(BogieStyles.TRIPLE_AXLE_EXTENDED_SHORT),
			ROT),
	GEARLESS_SHORT_XL_4 = new BogeyEntry(
			BlocksBogies.asResource("gearless/short/extra_large/4"),
			large(BogieStyles.QUADRUPLE_AXLE_SHORT),
			ROT),
	GEARLESS_SHORT_XL_5 = new BogeyEntry(
			BlocksBogies.asResource("gearless/short/extra_large/5"),
			large(BogieStyles.QUINTUPLE_AXLE_SHORT),
			ROT);

	public static final BogeyEntryCategory GEARLESS_SHORT_XL = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.gearless.short.extra_large"),
			List.of(GEARLESS_SHORT_XL_2,
					GEARLESS_SHORT_XL_3, GEARLESS_SHORT_XL_3_SPACED,
					GEARLESS_SHORT_XL_4,
					GEARLESS_SHORT_XL_5));

	public static final BogeyParentCategory GEARLESS = new BogeyParentCategory(
			Component.translatable("simurail_bogey_category.create_bb.gearless"),
			List.of(GEARLESS_LONG_L, GEARLESS_LONG_XL, GEARLESS_SHORT_L, GEARLESS_SHORT_XL));

	public static final BogeyEntry
	PISTONLESS_L_1 = new BogeyEntry(
			BlocksBogies.asResource("pistonless/large/1"),
			small(BogieStyles.SINGLE_AXLE_PISTONLESS)),
	PISTONLESS_L_2 = new BogeyEntry(
			BlocksBogies.asResource("pistonless/large/2"),
			small(BogieStyles.DOUBLE_AXLE_PISTONLESS)),
	PISTONLESS_L_3 = new BogeyEntry(
			BlocksBogies.asResource("pistonless/large/3"),
			small(BogieStyles.TRIPLE_AXLE_PISTONLESS)),
	PISTONLESS_L_3_SPACED = new BogeyEntry(
			BlocksBogies.asResource("pistonless/large/3_spaced"),
			small(BogieStyles.TRIPLE_AXLE_EXTENDED_PISTONLESS),
			ROT),
	PISTONLESS_L_4 = new BogeyEntry(
			BlocksBogies.asResource("pistonless/large/4"),
			small(BogieStyles.QUADRUPLE_AXLE_PISTONLESS)),
	PISTONLESS_L_5 = new BogeyEntry(
			BlocksBogies.asResource("pistonless/large/5"),
			small(BogieStyles.QUINTUPLE_AXLE_PISTONLESS)),
	PISTONLESS_L_6 = new BogeyEntry(
			BlocksBogies.asResource("pistonless/large/6"),
			small(BogieStyles.SEXTUPLE_AXLE_PISTONLESS));

	public static final BogeyEntryCategory PISTONLESS_L = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.pistonless.large"),
			List.of(PISTONLESS_L_1,
					PISTONLESS_L_2,
					PISTONLESS_L_3, PISTONLESS_L_3_SPACED,
					PISTONLESS_L_4,
					PISTONLESS_L_5,
					PISTONLESS_L_6));

	public static final BogeyEntry
	PISTONLESS_XL_1 = new BogeyEntry(
			BlocksBogies.asResource("pistonless/extra_large/1"),
			large(BogieStyles.SINGLE_AXLE_PISTONLESS)),
	PISTONLESS_XL_2 = new BogeyEntry(
			BlocksBogies.asResource("pistonless/extra_large/2"),
			large(BogieStyles.DOUBLE_AXLE_PISTONLESS)),
	PISTONLESS_XL_3 = new BogeyEntry(
			BlocksBogies.asResource("pistonless/extra_large/3"),
			large(BogieStyles.TRIPLE_AXLE_PISTONLESS)),
	PISTONLESS_XL_3_SPACED = new BogeyEntry(
			BlocksBogies.asResource("pistonless/extra_large/3_spaced"),
			large(BogieStyles.TRIPLE_AXLE_EXTENDED_PISTONLESS),
			ROT),
	PISTONLESS_XL_4 = new BogeyEntry(
			BlocksBogies.asResource("pistonless/extra_large/4"),
			large(BogieStyles.QUADRUPLE_AXLE_PISTONLESS)),
	PISTONLESS_XL_5 = new BogeyEntry(
			BlocksBogies.asResource("pistonless/extra_large/5"),
			large(BogieStyles.QUINTUPLE_AXLE_PISTONLESS));

	public static final BogeyEntryCategory PISTONLESS_XL = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.pistonless.extra_large"),
			List.of(PISTONLESS_XL_1,
					PISTONLESS_XL_2,
					PISTONLESS_XL_3, PISTONLESS_XL_3_SPACED,
					PISTONLESS_XL_4,
					PISTONLESS_XL_5));

	public static final BogeyParentCategory PISTONLESS = new BogeyParentCategory(
			Component.translatable("simurail_bogey_category.create_bb.pistonless"),
			List.of(PISTONLESS_L, PISTONLESS_XL));

	public static final BogeyEntry
	RODLESS_L_1 = new BogeyEntry(
			BlocksBogies.asResource("rodless/large/1"),
			small(BogieStyles.SINGLE_AXLE_PISTONLESS)),
	RODLESS_L_2 = new BogeyEntry(
			BlocksBogies.asResource("rodless/large/2"),
			small(BogieStyles.DOUBLE_AXLE_RODLESS)),
	RODLESS_L_3 = new BogeyEntry(
			BlocksBogies.asResource("rodless/large/3"),
			small(BogieStyles.TRIPLE_AXLE_RODLESS)),
	RODLESS_L_3_SPACED = new BogeyEntry(
			BlocksBogies.asResource("rodless/large/3_spaced"),
			small(BogieStyles.TRIPLE_AXLE_EXTENDED_RODLESS),
			ROT),
	RODLESS_L_4 = new BogeyEntry(
			BlocksBogies.asResource("rodless/large/4"),
			small(BogieStyles.QUADRUPLE_AXLE_RODLESS)),
	RODLESS_L_5 = new BogeyEntry(
			BlocksBogies.asResource("rodless/large/5"),
			small(BogieStyles.QUINTUPLE_AXLE_RODLESS)),
	RODLESS_L_6 = new BogeyEntry(
			BlocksBogies.asResource("rodless/large/6"),
			small(BogieStyles.SEXTUPLE_AXLE_RODLESS));

	public static final BogeyEntryCategory RODLESS_L = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.rodless.large"),
			List.of(RODLESS_L_1,
					RODLESS_L_2,
					RODLESS_L_3, RODLESS_L_3_SPACED,
					RODLESS_L_4,
					RODLESS_L_5,
					RODLESS_L_6));

	public static final BogeyEntry
	RODLESS_XL_1 = new BogeyEntry(
			BlocksBogies.asResource("rodless/extra_large/1"),
			large(BogieStyles.SINGLE_AXLE_PISTONLESS)),
	RODLESS_XL_2 = new BogeyEntry(
			BlocksBogies.asResource("rodless/extra_large/2"),
			large(BogieStyles.DOUBLE_AXLE_RODLESS)),
	RODLESS_XL_3 = new BogeyEntry(
			BlocksBogies.asResource("rodless/extra_large/3"),
			large(BogieStyles.TRIPLE_AXLE_RODLESS)),
	RODLESS_XL_3_SPACED = new BogeyEntry(
			BlocksBogies.asResource("rodless/extra_large/3_spaced"),
			large(BogieStyles.TRIPLE_AXLE_EXTENDED_RODLESS),
			ROT),
	RODLESS_XL_4 = new BogeyEntry(
			BlocksBogies.asResource("rodless/extra_large/4"),
			large(BogieStyles.QUADRUPLE_AXLE_RODLESS)),
	RODLESS_XL_5 = new BogeyEntry(
			BlocksBogies.asResource("rodless/extra_large/5"),
			large(BogieStyles.QUINTUPLE_AXLE_RODLESS));

	public static final BogeyEntryCategory RODLESS_XL = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.rodless.extra_large"),
			List.of(RODLESS_XL_1,
					RODLESS_XL_2,
					RODLESS_XL_3, RODLESS_XL_3_SPACED,
					RODLESS_XL_4,
					RODLESS_XL_5));

	public static final BogeyParentCategory RODLESS = new BogeyParentCategory(
			Component.translatable("simurail_bogey_category.create_bb.rodless"),
			List.of(RODLESS_L, RODLESS_XL));

	public static final BogeyEntry
	SCOTCH_YOKE_L_1 = new BogeyEntry(
			BlocksBogies.asResource("scotch_yoke/large/1"),
			small(BogieStyles.SINGLE_AXLE_SCOTCH_YOKE)),
	SCOTCH_YOKE_L_2 = new BogeyEntry(
			BlocksBogies.asResource("scotch_yoke/large/2"),
			small(BogieStyles.DOUBLE_AXLE_SCOTCH_YOKE)),
	SCOTCH_YOKE_L_3 = new BogeyEntry(
			BlocksBogies.asResource("scotch_yoke/large/3"),
			small(BogieStyles.TRIPLE_AXLE_SCOTCH_YOKE)),
	SCOTCH_YOKE_L_4 = new BogeyEntry(
			BlocksBogies.asResource("scotch_yoke/large/4"),
			small(BogieStyles.QUADRUPLE_AXLE_SCOTCH_YOKE)),
	SCOTCH_YOKE_L_5 = new BogeyEntry(
			BlocksBogies.asResource("scotch_yoke/large/5"),
			small(BogieStyles.QUINTUPLE_AXLE_SCOTCH_YOKE)),
	SCOTCH_YOKE_L_6 = new BogeyEntry(
			BlocksBogies.asResource("scotch_yoke/large/6"),
			small(BogieStyles.SEXTUPLE_AXLE_SCOTCH_YOKE));

	public static final BogeyEntryCategory SCOTCH_YOKE_L = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.scotch_yoke.large"),
			List.of(SCOTCH_YOKE_L_1,
					SCOTCH_YOKE_L_2,
					SCOTCH_YOKE_L_3,
					SCOTCH_YOKE_L_4,
					SCOTCH_YOKE_L_5,
					SCOTCH_YOKE_L_6));

	public static final BogeyEntry
	SCOTCH_YOKE_XL_1 = new BogeyEntry(
			BlocksBogies.asResource("scotch_yoke/extra_large/1"),
			large(BogieStyles.SINGLE_AXLE_SCOTCH_YOKE)),
	SCOTCH_YOKE_XL_2 = new BogeyEntry(
			BlocksBogies.asResource("scotch_yoke/extra_large/2"),
			large(BogieStyles.DOUBLE_AXLE_SCOTCH_YOKE)),
	SCOTCH_YOKE_XL_3 = new BogeyEntry(
			BlocksBogies.asResource("scotch_yoke/extra_large/3"),
			large(BogieStyles.TRIPLE_AXLE_SCOTCH_YOKE)),
	SCOTCH_YOKE_XL_4 = new BogeyEntry(
			BlocksBogies.asResource("scotch_yoke/extra_large/4"),
			large(BogieStyles.QUADRUPLE_AXLE_SCOTCH_YOKE)),
	SCOTCH_YOKE_XL_5 = new BogeyEntry(
			BlocksBogies.asResource("scotch_yoke/extra_large/5"),
			large(BogieStyles.QUINTUPLE_AXLE_SCOTCH_YOKE));

	public static final BogeyEntryCategory SCOTCH_YOKE_XL = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.create_bb.scotch_yoke.extra_large"),
			List.of(SCOTCH_YOKE_XL_1,
					SCOTCH_YOKE_XL_2,
					SCOTCH_YOKE_XL_3,
					SCOTCH_YOKE_XL_4,
					SCOTCH_YOKE_XL_5));

	public static final BogeyParentCategory SCOTCH_YOKE = new BogeyParentCategory(
			Component.translatable("simurail_bogey_category.create_bb.scotch_yoke"),
			List.of(SCOTCH_YOKE_L, SCOTCH_YOKE_XL));

	public static final BogeyParentCategory CREATE_BB = new BogeyParentCategory(
			Component.translatable("simurail_bogey_category.create_bb"),
			List.of(STANDARD, TRAILING,
					WALSCHAERTS, GEARLESS,
					PISTONLESS, RODLESS,
					SCOTCH_YOKE));

	public static void register() {
		BogeyMenuManager.addBogeyCategory(CREATE_BB);
	}

	public static BogeyType small(BogeyStyle style) {
		return new BogeyType(style, BogeySizes.SMALL);
	}

	public static BogeyType large(BogeyStyle style) {
		return new BogeyType(style, BogeySizes.LARGE);
	}
}
