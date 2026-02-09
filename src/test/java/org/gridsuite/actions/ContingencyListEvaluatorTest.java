package org.gridsuite.actions;

import com.powsybl.contingency.ContingencyElement;
import com.powsybl.contingency.list.IdentifierContingencyList;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.identifiers.IdBasedNetworkElementIdentifier;
import com.powsybl.iidm.network.identifiers.NetworkElementIdentifierContingencyList;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.gridsuite.actions.dto.*;
import org.gridsuite.actions.dto.contingency.FilterBasedContingencyList;
import org.gridsuite.actions.dto.contingency.IdBasedContingencyList;
import org.gridsuite.actions.dto.evaluation.ContingencyInfos;
import org.gridsuite.filter.FilterLoader;
import org.gridsuite.filter.identifierlistfilter.FilteredIdentifiables;
import org.gridsuite.filter.identifierlistfilter.IdentifiableAttributes;
import org.gridsuite.filter.utils.FilterServiceUtils;
import org.gridsuite.filter.utils.FiltersWithEquipmentTypes;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gridsuite.filter.utils.EquipmentType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class ContingencyListEvaluatorTest {
    @Test
    void testEvaluateFilterBasedContingencyList() {
        // SETUP TEST
        // filters used for filter based contingency
        List<FilterAttributes> filtersAttributes = List.of(
            new FilterAttributes(UUID.randomUUID(), LINE, "Filter1"),
            new FilterAttributes(UUID.randomUUID(), SUBSTATION, "Filter2"),
            new FilterAttributes(UUID.randomUUID(), TWO_WINDINGS_TRANSFORMER, "Filter3")
        );
        List<EquipmentTypesByFilter> equipmentTypesByFilter = List.of(
            new EquipmentTypesByFilter(UUID.randomUUID(), Set.of(IdentifiableType.GENERATOR))
        );

        try (MockedStatic<FilterServiceUtils> mockedFiltersServiceUtils =
                 Mockito.mockStatic(FilterServiceUtils.class)) {
            Network network = EurostagTutorialExample1Factory.createWithMoreGenerators();
            // mock filter evaluation which is made in another lib
            FilteredIdentifiables filteredIdentifiables = Mockito.mock(FilteredIdentifiables.class);
            when(filteredIdentifiables.equipmentIds())
                .thenReturn(List.of(
                    new IdentifiableAttributes("NHV1_NHV2_1", IdentifiableType.LINE, 0D),
                    new IdentifiableAttributes("GEN2", IdentifiableType.GENERATOR, 0D),
                    new IdentifiableAttributes("NGEN_NHV1", IdentifiableType.TWO_WINDINGS_TRANSFORMER, 0D),
                    new IdentifiableAttributes("TEST", IdentifiableType.TWO_WINDINGS_TRANSFORMER, 0D))); // this element does not exist in the network and should be removed after evaluation
            mockedFiltersServiceUtils.when(() ->
                FilterServiceUtils.evaluateFiltersWithEquipmentTypes(
                    any(FiltersWithEquipmentTypes.class),
                    eq(network),
                    any(FilterLoader.class)
                )
            ).thenReturn(filteredIdentifiables);

            ContingencyListEvaluator contingencyListEvaluator = new ContingencyListEvaluator(filtersUuids -> null);
            FilterBasedContingencyList filterBasedContingencyList = new FilterBasedContingencyList(UUID.randomUUID(), Instant.now(), filtersAttributes, equipmentTypesByFilter);
            List<ContingencyInfos> contingencyInfos = contingencyListEvaluator.evaluateContingencyList(filterBasedContingencyList, network);

            mockedFiltersServiceUtils.verify(() -> FilterServiceUtils.evaluateFiltersWithEquipmentTypes(
                    any(FiltersWithEquipmentTypes.class),
                    eq(network),
                    any(FilterLoader.class)),
                times(1));
            assertEquals(3, contingencyInfos.size());
            assertThat(List.of("NHV1_NHV2_1", "GEN2", "NGEN_NHV1")).usingRecursiveComparison().isEqualTo(contingencyInfos.stream().map(ContingencyInfos::getId).toList());
        }
    }

    @Test
    void testEvaluateIdBasedContingencyList() {
        Network network = EurostagTutorialExample1Factory.createWithMoreGenerators();
        ContingencyListEvaluator contingencyListEvaluator = new ContingencyListEvaluator(filtersUuids -> null);

        NetworkElementIdentifierContingencyList networkElementIdentifierContingencyList = new NetworkElementIdentifierContingencyList(List.of(
            new IdBasedNetworkElementIdentifier("NHV1_NHV2_1"),
            new IdBasedNetworkElementIdentifier("NHV1_NHV2_2"),
            new IdBasedNetworkElementIdentifier("TEST") // this element does not exist in the network and should be removed after evaluation
        ), "default");
        IdBasedContingencyList idBasedContingencyList = new IdBasedContingencyList(null, Instant.now(), new IdentifierContingencyList("defaultName", List.of(networkElementIdentifierContingencyList)));

        List<ContingencyInfos> contingencyInfos = contingencyListEvaluator.evaluateContingencyList(idBasedContingencyList, network);
        assertEquals(1, contingencyInfos.size());
        assertThat(List.of("NHV1_NHV2_1", "NHV1_NHV2_2")).usingRecursiveComparison().isEqualTo(contingencyInfos.stream().flatMap(c -> c.getContingency().getElements().stream().map(ContingencyElement::getId)).toList());
    }
}
