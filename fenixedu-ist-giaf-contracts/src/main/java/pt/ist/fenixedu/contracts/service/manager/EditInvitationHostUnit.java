/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Core.
 *
 * FenixEdu Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.contracts.service.manager;

import static org.fenixedu.academic.predicate.AccessControl.check;

import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.predicate.RolePredicates;

import pt.ist.fenixedu.contracts.domain.organizationalStructure.Invitation;
import pt.ist.fenixframework.Atomic;

public class EditInvitationHostUnit {

    @Atomic
    public static void run(Invitation invitation, Unit hostUnit) {
        check(RolePredicates.MANAGER_OR_OPERATOR_PREDICATE);
        if (invitation != null && hostUnit != null) {
            invitation.setParentParty(hostUnit);
        }
    }
}