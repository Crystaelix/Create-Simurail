package com.crystaelix.simurail.compat.railways;

import java.util.List;

import com.crystaelix.simurail.api.bogey.BogeyPropertyOverrides;
import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.api.bogey.menu.BogeyEntry;
import com.crystaelix.simurail.api.bogey.menu.BogeyEntryCategory;
import com.crystaelix.simurail.api.bogey.menu.BogeyMenuManager;
import com.crystaelix.simurail.api.bogey.menu.BogeyParentCategory;
import com.crystaelix.simurail.api.bogey.menu.TrackTypeEntry;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;

import net.minecraft.network.chat.Component;

public class RailwaysBogeys {

	public static final BogeyEntry
	INVISIBLE = new BogeyEntry(
			Railways.asResource("invisible"),
			small(CRBogeyStyles.INVISIBLE_MONOBOGEY)),
	INVISIBLE_ALT = new BogeyEntry(
			Railways.asResource("invisible_alt"),
			small(CRBogeyStyles.INVISIBLE)),
	MONOBOGEY = new BogeyEntry(
			Railways.asResource("monobogey"),
			small(CRBogeyStyles.MONOBOGEY)),
	NARROW_STANDARD_SMALL = new BogeyEntry(
			Railways.asResource("narrow/standard"),
			small(CRBogeyStyles.NARROW_DEFAULT)),
	NARROW_SCOTCH_YOKE_1 = new BogeyEntry(
			Railways.asResource("narrow/scotch_yoke/1"),
			large(CRBogeyStyles.NARROW_DEFAULT)),
	NARROW_SCOTCH_YOKE_2 = new BogeyEntry(
			Railways.asResource("narrow/scotch_yoke/2"),
			large(CRBogeyStyles.NARROW_DOUBLE_SCOTCH)),
	WIDE_STANDARD_SMALL = new BogeyEntry(
			Railways.asResource("wide/standard"),
			small(CRBogeyStyles.WIDE_DEFAULT)),
	WIDE_SCOTCH_YOKE_L = new BogeyEntry(
			Railways.asResource("wide/scotch_yoke/large"),
			large(CRBogeyStyles.WIDE_DEFAULT)),
	WIDE_SCOTCH_YOKE_XXL = new BogeyEntry(
			Railways.asResource("wide/scotch_yoke/comically_large"),
			large(CRBogeyStyles.WIDE_COMICALLY_LARGE));

	public static final BogeyEntryCategory SPECIAL = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.railways.special"),
			List.of(INVISIBLE,
					MONOBOGEY,
					NARROW_STANDARD_SMALL,
					NARROW_SCOTCH_YOKE_1,
					NARROW_SCOTCH_YOKE_2,
					WIDE_STANDARD_SMALL,
					WIDE_SCOTCH_YOKE_L,
					WIDE_SCOTCH_YOKE_XXL));

	public static final BogeyEntry
	COILSPRING = new BogeyEntry(
			Railways.asResource("coilspring"),
			small(CRBogeyStyles.COILSPRING)),
	LEAFSPRING = new BogeyEntry(
			Railways.asResource("leafspring"),
			small(CRBogeyStyles.LEAFSPRING));

	public static final BogeyEntry
	STANDARD_S_1 = new BogeyEntry(
			Railways.asResource("standard/small/1"),
			small(CRBogeyStyles.SINGLEAXLE)),
	STANDARD_M_1 = new BogeyEntry(
			Railways.asResource("standard/medium/1"),
			small(CRBogeyStyles.MEDIUM_STANDARD)),
	TRAILING_M_1 = new BogeyEntry(
			Railways.asResource("trailing/medium/1"),
			small(CRBogeyStyles.MEDIUM_2_0_2_TRAILING)),
	SCOTCH_YOKE_1 = new BogeyEntry(
			Railways.asResource("scotch_yoke/1"),
			large(AllBogeyStyles.STANDARD));

	public static final BogeyEntryCategory $1 = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.railways.1"),
			List.of(COILSPRING,
					LEAFSPRING,
					STANDARD_S_1,
					STANDARD_M_1,
					TRAILING_M_1,
					SCOTCH_YOKE_1));

	public static final BogeyEntry
	MODERN = new BogeyEntry(
			Railways.asResource("modern"),
			small(CRBogeyStyles.MODERN)),
	BLOMBERG = new BogeyEntry(
			Railways.asResource("blomberg"),
			small(CRBogeyStyles.BLOMBERG)),
	Y25 = new BogeyEntry(
			Railways.asResource("y25"),
			small(CRBogeyStyles.Y25)),
	FREIGHT = new BogeyEntry(
			Railways.asResource("freight"),
			small(CRBogeyStyles.FREIGHT)),
	PASSENGER = new BogeyEntry(
			Railways.asResource("passenger"),
			small(CRBogeyStyles.PASSENGER)),
	ARCHBAR = new BogeyEntry(
			Railways.asResource("archbar"),
			small(CRBogeyStyles.ARCHBAR));

	public static final BogeyEntry
	STANDARD_S_2 = new BogeyEntry(
			Railways.asResource("standard/small/2"),
			small(AllBogeyStyles.STANDARD)),
	STANDARD_M_2 = new BogeyEntry(
			Railways.asResource("standard/medium/2"),
			small(CRBogeyStyles.MEDIUM_STANDARD)),
	TRAILING_M_2 = new BogeyEntry(
			Railways.asResource("trailing/medium/2"),
			small(CRBogeyStyles.MEDIUM_4_0_4_TRAILING)),
	SCOTCH_YOKE_2 = new BogeyEntry(
			Railways.asResource("scotch_yoke/2"),
			large(CRBogeyStyles.LARGE_CREATE_STYLED_0_4_0));

	public static final BogeyEntryCategory $2 = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.railways.2"),
			List.of(MODERN,
					BLOMBERG,
					Y25,
					FREIGHT,
					PASSENGER,
					ARCHBAR,
					STANDARD_S_2,
					STANDARD_M_2,
					TRAILING_M_2,
					SCOTCH_YOKE_2));

	public static final BogeyEntry
	HEAVYWEIGHT = new BogeyEntry(
			Railways.asResource("heavyweight"),
			small(CRBogeyStyles.HEAVYWEIGHT)),
	RADIAL = new BogeyEntry(
			Railways.asResource("radial"),
			small(CRBogeyStyles.RADIAL));

	public static final BogeyEntry
	STANDARD_M_3 = new BogeyEntry(
			Railways.asResource("standard/medium/3"),
			small(CRBogeyStyles.MEDIUM_TRIPLE_WHEEL)),
	TRAILING_M_3 = new BogeyEntry(
			Railways.asResource("trailing/medium/3"),
			small(CRBogeyStyles.MEDIUM_6_0_6_TRAILING)),
	TENDER_M_3 = new BogeyEntry(
			Railways.asResource("tender/medium/3"),
			small(CRBogeyStyles.MEDIUM_6_0_6_TENDER)),
	SCOTCH_YOKE_3 = new BogeyEntry(
			Railways.asResource("scotch_yoke/3"),
			large(CRBogeyStyles.LARGE_CREATE_STYLED_0_6_0));

	public static final BogeyEntryCategory $3 = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.railways.3"),
			List.of(HEAVYWEIGHT,
					RADIAL,
					STANDARD_M_3,
					TRAILING_M_3,
					TENDER_M_3,
					SCOTCH_YOKE_3));

	public static final BogeyEntry
	STANDARD_M_4 = new BogeyEntry(
			Railways.asResource("standard/medium/4"),
			small(CRBogeyStyles.MEDIUM_QUADRUPLE_WHEEL)),
	TENDER_M_4 = new BogeyEntry(
			Railways.asResource("tender/medium/4"),
			small(CRBogeyStyles.MEDIUM_8_0_8_TENDER)),
	SCOTCH_YOKE_4 = new BogeyEntry(
			Railways.asResource("scotch_yoke/4"),
			large(CRBogeyStyles.LARGE_CREATE_STYLED_0_8_0));

	public static final BogeyEntryCategory $4 = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.railways.4"),
			List.of(STANDARD_M_4,
					TENDER_M_4,
					SCOTCH_YOKE_4));

	public static final BogeyEntry
	STANDARD_M_5 = new BogeyEntry(
			Railways.asResource("standard/medium/5"),
			small(CRBogeyStyles.MEDIUM_QUINTUPLE_WHEEL)),
	TENDER_M_5 = new BogeyEntry(
			Railways.asResource("tender/medium/5"),
			small(CRBogeyStyles.MEDIUM_10_0_10_TENDER)),
	SCOTCH_YOKE_5 = new BogeyEntry(
			Railways.asResource("scotch_yoke/5"),
			large(CRBogeyStyles.LARGE_CREATE_STYLED_0_10_0));

	public static final BogeyEntryCategory $5 = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.railways.5"),
			List.of(STANDARD_M_5,
					TENDER_M_5,
					SCOTCH_YOKE_5));

	public static final BogeyEntry
	SCOTCH_YOKE_6 = new BogeyEntry(
			Railways.asResource("scotch_yoke/6"),
			large(CRBogeyStyles.LARGE_CREATE_STYLED_0_12_0));

	public static final BogeyEntryCategory $6 = new BogeyEntryCategory(
			Component.translatable("simurail_bogey_category.railways.6"),
			List.of(SCOTCH_YOKE_6));

	public static final BogeyParentCategory RAILWAYS = new BogeyParentCategory(
			Component.translatable("simurail_bogey_category.railways"),
			List.of(SPECIAL,
					$1, $2,
					$3, $4,
					$5, $6));

	public static final TrackTypeEntry
	MONORAIL = new TrackTypeEntry(
			CRTrackType.MONORAIL,
			Component.translatable("simurail_track_type.railways.monorail"),
			Component.translatable("simurail_track_type.railways.monorail.short"),
			true,
			CRBlocks.MONORAIL_TRACK::getDefaultState),
	NARROW = new TrackTypeEntry(
			CRTrackType.NARROW_GAUGE,
			Component.translatable("simurail_track_type.railways.narrow"),
			Component.translatable("simurail_track_type.railways.narrow.short"),
			false,
			() -> CRTrackMaterials.NARROW_GAUGE_ANDESITE.getBlockSupplier().get().defaultBlockState()),
	WIDE = new TrackTypeEntry(
			CRTrackType.WIDE_GAUGE,
			Component.translatable("simurail_track_type.railways.wide"),
			Component.translatable("simurail_track_type.railways.wide.short"),
			false,
			() -> CRTrackMaterials.WIDE_GAUGE_ANDESITE.getBlockSupplier().get().defaultBlockState());

	public static void register() {
		BogeyMenuManager.addBogeyCategory(RAILWAYS);
		BogeyMenuManager.addTrackTypeEntry(MONORAIL);
		BogeyMenuManager.addTrackTypeEntry(NARROW);
		BogeyMenuManager.addTrackTypeEntry(WIDE);

		BogeyType.setDefault(CRTrackType.MONORAIL, false, MONOBOGEY.type());
		BogeyType.setDefault(CRTrackType.MONORAIL, true, MONOBOGEY.type());
		BogeyType.setDefault(CRTrackType.NARROW_GAUGE, false, NARROW_STANDARD_SMALL.type());
		BogeyType.setDefault(CRTrackType.WIDE_GAUGE, false, WIDE_STANDARD_SMALL.type());

		BogeyPropertyOverrides.setGroundDrivableOverride(INVISIBLE.type(), false);
		BogeyPropertyOverrides.setGroundDrivableOverride(INVISIBLE_ALT.type(), false);
	}

	public static BogeyType small(BogeyStyle style) {
		return new BogeyType(style, BogeySizes.SMALL);
	}

	public static BogeyType large(BogeyStyle style) {
		return new BogeyType(style, BogeySizes.LARGE);
	}
}
