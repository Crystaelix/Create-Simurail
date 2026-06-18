package com.crystaelix.simurail.compat.create_bb;

import static com.crystaelix.simurail.api.bogey.BogeyPropertyOverrides.setWheelSpacingOverride;

import com.crystaelix.simurail.api.bogey.BogeyType;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.weido.create_bb.registry.BogieStyles;

public class BlocksBogiesOverrides {

	public static void register() {
		if(false) {
			return;
		}

		// 3 L
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_PISTONLESS), 54/16D);
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_EXTENDED_PISTONLESS), 54/16D);
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_RODLESS), 54/16D);
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_EXTENDED_RODLESS), 54/16D);
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_SCOTCH_YOKE), 54/16D);
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_LONG), 54/16D);
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_EXTENDED_LONG), 54/16D);
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_SHORT), 54/16D);
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_EXTENDED_SHORT), 54/16D);
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_LONG), 54/16D);
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_EXTENDED_LONG), 54/16D);
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_SHORT), 54/16D);
		setWheelSpacingOverride(small(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_EXTENDED_SHORT), 54/16D);
		// 3 XL
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_PISTONLESS), 72/16D);
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_EXTENDED_PISTONLESS), 72/16D);
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_RODLESS), 72/16D);
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_EXTENDED_RODLESS), 72/16D);
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_SCOTCH_YOKE), 72/16D);
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_LONG), 72/16D);
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_EXTENDED_LONG), 72/16D);
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_SHORT), 72/16D);
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_EXTENDED_SHORT), 72/16D);
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_LONG), 72/16D);
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_EXTENDED_LONG), 72/16D);
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_SHORT), 72/16D);
		setWheelSpacingOverride(large(BogieStyles.TRIPLE_AXLE_WALSCHAERTS_EXTENDED_SHORT), 72/16D);
		// 4 L
		setWheelSpacingOverride(small(BogieStyles.QUADRUPLE_AXLE_PISTONLESS), 84/16D);
		setWheelSpacingOverride(small(BogieStyles.QUADRUPLE_AXLE_RODLESS), 84/16D);
		setWheelSpacingOverride(small(BogieStyles.QUADRUPLE_AXLE_SCOTCH_YOKE), 84/16D);
		setWheelSpacingOverride(small(BogieStyles.QUADRUPLE_AXLE_LONG), 84/16D);
		setWheelSpacingOverride(small(BogieStyles.QUADRUPLE_AXLE_SHORT), 84/16D);
		setWheelSpacingOverride(small(BogieStyles.QUADRUPLE_AXLE_WALSCHAERTS_LONG), 84/16D);
		setWheelSpacingOverride(small(BogieStyles.QUADRUPLE_AXLE_WALSCHAERTS_SHORT), 84/16D);
		// 4 XL
		setWheelSpacingOverride(large(BogieStyles.QUADRUPLE_AXLE_PISTONLESS), 108/16D);
		setWheelSpacingOverride(large(BogieStyles.QUADRUPLE_AXLE_RODLESS), 108/16D);
		setWheelSpacingOverride(large(BogieStyles.QUADRUPLE_AXLE_SCOTCH_YOKE), 108/16D);
		setWheelSpacingOverride(large(BogieStyles.QUADRUPLE_AXLE_LONG), 108/16D);
		setWheelSpacingOverride(large(BogieStyles.QUADRUPLE_AXLE_SHORT), 108/16D);
		setWheelSpacingOverride(large(BogieStyles.QUADRUPLE_AXLE_WALSCHAERTS_LONG), 108/16D);
		setWheelSpacingOverride(large(BogieStyles.QUADRUPLE_AXLE_WALSCHAERTS_SHORT), 108/16D);
		// 5 L
		setWheelSpacingOverride(small(BogieStyles.QUINTUPLE_AXLE_PISTONLESS), 104/16D);
		setWheelSpacingOverride(small(BogieStyles.QUINTUPLE_AXLE_RODLESS), 104/16D);
		setWheelSpacingOverride(small(BogieStyles.QUINTUPLE_AXLE_SCOTCH_YOKE), 104/16D);
		setWheelSpacingOverride(small(BogieStyles.QUINTUPLE_AXLE_LONG), 104/16D);
		setWheelSpacingOverride(small(BogieStyles.QUINTUPLE_AXLE_SHORT), 104/16D);
		setWheelSpacingOverride(small(BogieStyles.QUINTUPLE_AXLE_WALSCHAERTS_LONG), 104/16D);
		setWheelSpacingOverride(small(BogieStyles.QUINTUPLE_AXLE_WALSCHAERTS_SHORT), 104/16D);
		// 5 XL
		setWheelSpacingOverride(large(BogieStyles.QUINTUPLE_AXLE_PISTONLESS), 144/16D);
		setWheelSpacingOverride(large(BogieStyles.QUINTUPLE_AXLE_RODLESS), 144/16D);
		setWheelSpacingOverride(large(BogieStyles.QUINTUPLE_AXLE_SCOTCH_YOKE), 144/16D);
		setWheelSpacingOverride(large(BogieStyles.QUINTUPLE_AXLE_LONG), 144/16D);
		setWheelSpacingOverride(large(BogieStyles.QUINTUPLE_AXLE_SHORT), 144/16D);
		setWheelSpacingOverride(large(BogieStyles.QUINTUPLE_AXLE_WALSCHAERTS_LONG), 144/16D);
		setWheelSpacingOverride(large(BogieStyles.QUINTUPLE_AXLE_WALSCHAERTS_SHORT), 144/16D);
		// 6 L
		setWheelSpacingOverride(small(BogieStyles.SEXTUPLE_AXLE_PISTONLESS), 140/16D);
		setWheelSpacingOverride(small(BogieStyles.SEXTUPLE_AXLE_RODLESS), 140/16D);
		setWheelSpacingOverride(small(BogieStyles.SEXTUPLE_AXLE_SCOTCH_YOKE), 140/16D);
		setWheelSpacingOverride(small(BogieStyles.SEXTUPLE_AXLE_LONG), 140/16D);
		setWheelSpacingOverride(small(BogieStyles.SEXTUPLE_AXLE_SHORT), 140/16D);
		setWheelSpacingOverride(small(BogieStyles.SEXTUPLE_AXLE_WALSCHAERTS_LONG), 140/16D);
		setWheelSpacingOverride(small(BogieStyles.SEXTUPLE_AXLE_WALSCHAERTS_SHORT), 140/16D);
	}

	public static BogeyType small(BogeyStyle style) {
		return new BogeyType(style, BogeySizes.SMALL);
	}

	public static BogeyType large(BogeyStyle style) {
		return new BogeyType(style, BogeySizes.LARGE);
	}
}
