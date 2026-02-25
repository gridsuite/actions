package org.gridsuite.actions.api;

import org.gridsuite.actions.internal.DefaultContingencyListEvaluator;
import org.gridsuite.filter.api.FilterEvaluator;

import java.util.Objects;

/**
 * Factory for creating {@link ContingencyListEvaluator} instances.
 * <p>
 * This utility wires the default implementation ({@link DefaultContingencyListEvaluator})
 * with the required dependency for resolving filter-based contingency lists.
 * </p>
 */
public final class ContingencyListEvaluatorFactory {

    private ContingencyListEvaluatorFactory() {
        throw new IllegalStateException("Should not initialize an utility class");
    }

    /**
     * Creates a {@link ContingencyListEvaluator} using a {@link FilterProvider}.
     * <p>
     * The resulting evaluator will internally build a {@link org.gridsuite.filter.api.FilterEvaluator}
     * from the provider in order to evaluate filter-based contingency lists.
     * </p>
     *
     * @param filterProvider provider used to load filters referenced by contingency lists
     * @return a {@link ContingencyListEvaluator} instance
     * @throws NullPointerException if {@code filterProvider} is {@code null}
     */
    public static ContingencyListEvaluator create(FilterProvider filterProvider) {
        Objects.requireNonNull(filterProvider, "Filter provider is not provided while creating contingency list evaluator");
        return new DefaultContingencyListEvaluator(filterProvider);
    }

    /**
     * Creates a {@link ContingencyListEvaluator} using an already configured {@link FilterEvaluator}.
     *
     * @param filterEvaluator filter evaluator used to resolve filter-based contingency lists
     * @return a {@link ContingencyListEvaluator} instance
     * @throws NullPointerException if {@code filterEvaluator} is {@code null}
     */
    public static ContingencyListEvaluator create(FilterEvaluator filterEvaluator) {
        Objects.requireNonNull(filterEvaluator, "Filter evaluator is not provided while creating contingency list evaluator");
        return new DefaultContingencyListEvaluator(filterEvaluator);
    }
}
