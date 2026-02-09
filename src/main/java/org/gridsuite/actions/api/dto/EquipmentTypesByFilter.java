/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.gridsuite.actions.api.dto;

import com.powsybl.iidm.network.IdentifiableType;
import org.gridsuite.filter.api.dto.EquipmentTypesByFilterId;

import java.util.Set;
import java.util.UUID;

/**
 * Store a list of equipment types for a given filter ID
 *
 * @author Florent MILLOT <florent.millot@rte-france.com>
 */
public record EquipmentTypesByFilter(UUID filterId, Set<IdentifiableType> equipmentTypes) {
    public EquipmentTypesByFilterId toEquipmentTypesByFilterId() {
        return new EquipmentTypesByFilterId(filterId, equipmentTypes);
    }
}
