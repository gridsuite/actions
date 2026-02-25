/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.gridsuite.actions.api.dto;
import org.gridsuite.filter.utils.EquipmentType;

import java.util.UUID;

/**
 * @author Bassel El Cheikh <bassel.el-cheikh at rte-france.com>
 */

// partial class from FilterAttributes (Filter-server)

public record FilterAttributes(UUID id, EquipmentType equipmentType, String name) {
    public org.gridsuite.filter.api.dto.FilterAttributes toFilterAttributes() {
        return org.gridsuite.filter.api.dto.FilterAttributes.builder()
            .equipmentType(equipmentType)
            .name(name)
            .id(id)
            .build();
    }
}
