/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.actions.utils;

import com.powsybl.contingency.*;
import com.powsybl.iidm.network.IdentifiableType;
import org.gridsuite.filter.identifierlistfilter.IdentifiableAttributes;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Bassel El Cheikh <bassel.el-cheikh at rte-france.com>
 */

class ContingencyListUtilsTest {
    @ParameterizedTest
    @MethodSource({
        "provideArgumentsForTests"
    })
    void testConversionFromIdentifiableAttributesToContingency(String id, IdentifiableType type, Class<ContingencyElement> expectedClass) {
        IdentifiableAttributes identifierAttributes = new IdentifiableAttributes(id, type, null);
        assertEquals(expectedClass, ContingencyListUtils.toContingencyElement(identifierAttributes).getClass());
    }

    static Stream<Arguments> provideArgumentsForTests() {
        return Stream.of(
            Arguments.of("id", IdentifiableType.LINE, LineContingency.class),
            Arguments.of("id", IdentifiableType.TWO_WINDINGS_TRANSFORMER, TwoWindingsTransformerContingency.class),
            Arguments.of("id", IdentifiableType.THREE_WINDINGS_TRANSFORMER, ThreeWindingsTransformerContingency.class),
            Arguments.of("id", IdentifiableType.GENERATOR, GeneratorContingency.class),
            Arguments.of("id", IdentifiableType.BATTERY, BatteryContingency.class),
            Arguments.of("id", IdentifiableType.LOAD, LoadContingency.class),
            Arguments.of("id", IdentifiableType.SHUNT_COMPENSATOR, ShuntCompensatorContingency.class),
            Arguments.of("id", IdentifiableType.STATIC_VAR_COMPENSATOR, StaticVarCompensatorContingency.class),
            Arguments.of("id", IdentifiableType.HVDC_LINE, HvdcLineContingency.class),
            Arguments.of("id", IdentifiableType.DANGLING_LINE, DanglingLineContingency.class),
            Arguments.of("id", IdentifiableType.BUSBAR_SECTION, BusbarSectionContingency.class)
        );
    }

}
