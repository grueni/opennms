<<<<<<< HEAD
package org.opennms.netmgt.dao.api;

import java.util.List;

import org.opennms.netmgt.model.topology.TopologyElement;
import org.opennms.netmgt.model.topology.ElementIdentifier;
import org.opennms.netmgt.model.topology.EndPoint;
import org.opennms.netmgt.model.topology.Link;

public interface TopologyDao {
	
	public void saveOrUpdate(Link link);
	
	public void saveOrUpdate(EndPoint endpoint);
	
	public void delete(ElementIdentifier elementidentifier);

	public void delete(TopologyElement element);
	
	public void delete(Link link);
	
	public void delete(EndPoint endpoint);

	public TopologyElement get(ElementIdentifier elementIdentifier);

	public EndPoint get(EndPoint endpoint);

	public List<TopologyElement> getTopology();
		
=======
/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2013 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2013 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
package org.opennms.netmgt.dao.api;

import org.opennms.netmgt.model.OnmsNode;

public interface TopologyDao {

    OnmsNode getDefaultFocusPoint();

>>>>>>> master
}