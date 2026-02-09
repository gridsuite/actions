package org.gridsuite.actions.api;

import com.powsybl.iidm.network.Network;
import org.gridsuite.actions.api.dto.evaluation.ContingencyInfos;
import org.gridsuite.actions.api.dto.contingency.PersistentContingencyList;

import java.util.List;

public interface ContingencyListEvaluator {
    List<ContingencyInfos> evaluateContingencyList(PersistentContingencyList persistentContingencyList, Network network);
}
