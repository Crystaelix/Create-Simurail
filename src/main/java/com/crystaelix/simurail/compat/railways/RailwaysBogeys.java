package com.crystaelix.simurail.compat.railways;

import java.util.List;

import com.crystaelix.simurail.api.bogey.BogeyPropertyOverrides;
import com.crystaelix.simurail.api.bogey.BogeyType;
import com.crystaelix.simurail.api.bogey.menu.BogeyEntry;
import com.crystaelix.simurail.api.bogey.menu.BogeyEntryCategory;
import com.crystaelix.simurail.api.bogey.menu.BogeyMenuManager;
import com.crystaelix.simurail.api.bogey.menu.BogeyParentCategory;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRBogeyStyles;
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
			small(CRBogeyStyles.MEDIUM_SINGLE_WHEEL)),
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

	public static void register() {
		BogeyMenuManager.addBogeyCategory(RAILWAYS);

		BogeyType.setDefault(CRTrackType.MONORAIL, false, MONOBOGEY.type());
		BogeyType.setDefault(CRTrackType.MONORAIL, true, MONOBOGEY.type());
		BogeyType.setDefault(CRTrackType.NARROW_GAUGE, false, NARROW_STANDARD_SMALL.type());
		BogeyType.setDefault(CRTrackType.WIDE_GAUGE, false, WIDE_STANDARD_SMALL.type());

		axleSpacing(INVISIBLE, 0);
		axleSpacing(INVISIBLE_ALT, 0);
		axleSpacing(NARROW_STANDARD_SMALL, 20);
		axleSpacing(NARROW_SCOTCH_YOKE_1, 0);
		axleSpacing(NARROW_SCOTCH_YOKE_2, 24);
		axleSpacing(WIDE_STANDARD_SMALL, 48);
		axleSpacing(WIDE_SCOTCH_YOKE_L, 0);
		axleSpacing(WIDE_SCOTCH_YOKE_XXL, 0);

		axleSpacing(COILSPRING, 0);
		axleSpacing(LEAFSPRING, 0);
		axleSpacing(STANDARD_S_1, 0);
		axleSpacing(STANDARD_M_1, 0);
		axleSpacing(TRAILING_M_1, 0);
		axleSpacing(SCOTCH_YOKE_1, 0);

		axleSpacing(MODERN, 32);
		axleSpacing(BLOMBERG, 32);
		axleSpacing(Y25, 32);
		axleSpacing(FREIGHT, 32);
		axleSpacing(PASSENGER, 32);
		axleSpacing(ARCHBAR, 32);
		axleSpacing(STANDARD_M_2, 32);
		axleSpacing(TRAILING_M_2, 32);
		axleSpacing(SCOTCH_YOKE_2, 28);

		axleSpacing(HEAVYWEIGHT, 48);
		axleSpacing(RADIAL, 48);
		axleSpacing(STANDARD_M_3, 48);
		axleSpacing(TRAILING_M_3, 48);
		axleSpacing(TENDER_M_3, 48);
		axleSpacing(SCOTCH_YOKE_3, 54);

		axleSpacing(STANDARD_M_4, 72);
		axleSpacing(TENDER_M_4, 72);
		axleSpacing(SCOTCH_YOKE_4, 84);

		axleSpacing(STANDARD_M_5, 96);
		axleSpacing(TENDER_M_5, 96);
		axleSpacing(SCOTCH_YOKE_5, 108);

		axleSpacing(SCOTCH_YOKE_6, 140);

		axleCount(0, INVISIBLE, INVISIBLE_ALT);
		axleCount(1, NARROW_SCOTCH_YOKE_1, WIDE_SCOTCH_YOKE_L, WIDE_SCOTCH_YOKE_XXL);
		axleCount(2, MONOBOGEY, NARROW_STANDARD_SMALL, NARROW_SCOTCH_YOKE_2, WIDE_STANDARD_SMALL);

		axleCount(1, $1);
		axleCount(2, $2);
		axleCount(3, $3);
		axleCount(4, $4);
		axleCount(5, $5);
		axleCount(6, $6);;

		groundDrivable(INVISIBLE, false);
		groundDrivable(INVISIBLE_ALT, false);
	}

	public static BogeyType small(BogeyStyle style) {
		return new BogeyType(style, BogeySizes.SMALL);
	}

	public static BogeyType large(BogeyStyle style) {
		return new BogeyType(style, BogeySizes.LARGE);
	}

	public static void axleSpacing(BogeyEntry entry, double axleSpacing) {
		BogeyPropertyOverrides.setLogicalAxleSpacingOverride(entry.type(), (int)Math.round(axleSpacing / 16));
		BogeyPropertyOverrides.setVisualAxleSpacingOverride(entry.type(), axleSpacing / 16);
	}

	public static void axleCount(int axleCount, BogeyEntry... entries) {
		for(BogeyEntry entry : entries) {
			BogeyPropertyOverrides.setAxleCountOverride(entry.type(), axleCount);
		}
	}

	public static void axleCount(int axleCount, BogeyEntryCategory category) {
		for(BogeyEntry entry : category.children()) {
			BogeyPropertyOverrides.setAxleCountOverride(entry.type(), axleCount);
		}
	}

	public static void groundDrivable(BogeyEntry entry, boolean groundDrivable) {
		BogeyPropertyOverrides.setGroundDrivableOverride(entry.type(), groundDrivable);
	}
}
