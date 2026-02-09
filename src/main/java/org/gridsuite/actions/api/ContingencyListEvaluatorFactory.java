package org.gridsuite.actions.api;

import org.gridsuite.actions.internal.impl.DefaultContingencyListEvaluator;
import org.gridsuite.filter.api.FilterEvaluator;

import java.util.Objects;

public final class ContingencyListEvaluatorFactory {

    private ContingencyListEvaluatorFactory() {
        throw new IllegalStateException("Should not initialize an utility class");
    }

    public static ContingencyListEvaluator create(FilterEvaluator filterEvaluator) {
        Objects.requireNonNull(filterEvaluator, "Filter evaluator is not provided while creating contingency list evaluator");
        return new DefaultContingencyListEvaluator(filterEvaluator);
    }
}
