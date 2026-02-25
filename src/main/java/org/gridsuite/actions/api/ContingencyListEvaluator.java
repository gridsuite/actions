package org.gridsuite.actions.api;

import com.powsybl.iidm.network.Network;
import org.gridsuite.actions.api.dto.contingency.PersistentContingencyList;
import org.gridsuite.actions.api.dto.evaluation.ContingencyInfos;

import java.util.List;

/**
 * Evaluates a {@link PersistentContingencyList} against a given {@link Network}.
 * <p>
 * The evaluator resolves the contingency list definition (ID-based or filter-based) into
 * Powsybl {@code Contingency} objects when possible, and returns per-contingency information
 * including:
 * </p>
 * <ul>
 *   <li>the generated Powsybl contingency (may be {@code null} in some cases),</li>
 *   <li>elements not found in the network for that contingency,</li>
 *   <li>elements that are present but currently disconnected in the network.</li>
 * </ul>
 */
public interface ContingencyListEvaluator {

    /**
     * Evaluates the given contingency list using the provided network as context.
     *
     * @param persistentContingencyList the contingency list to evaluate
     * @param network the IIDM network used as evaluation context
     * @return a list of {@link ContingencyInfos} (possibly empty, never {@code null})
     * @throws NullPointerException if {@code persistentContingencyList} or {@code network} is {@code null}
     */
    List<ContingencyInfos> evaluateContingencyList(PersistentContingencyList persistentContingencyList, Network network);
}
