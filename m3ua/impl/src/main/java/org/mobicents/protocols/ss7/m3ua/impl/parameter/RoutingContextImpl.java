/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.protocols.ss7.m3ua.impl.parameter;

import java.util.Arrays;

import org.mobicents.protocols.ss7.m3ua.parameter.Parameter;
import org.mobicents.protocols.ss7.m3ua.parameter.RoutingContext;

public class RoutingContextImpl extends ParameterImpl implements RoutingContext {

    private long[] rcs = null;
    private byte[] value;

    protected RoutingContextImpl(byte[] value) {
        this.tag = Parameter.Routing_Context;

        int count = 0;
        int arrSize = 0;
        rcs = new long[(value.length / 4)];

        while (count < value.length) {
            rcs[arrSize] = 0;
            rcs[arrSize] |= value[count++] & 0xFF;
            rcs[arrSize] <<= 8;
            rcs[arrSize] |= value[count++] & 0xFF;
            rcs[arrSize] <<= 8;
            rcs[arrSize] |= value[count++] & 0xFF;
            rcs[arrSize] <<= 8;
            rcs[arrSize++] |= value[count++] & 0xFF;
        }

        this.value = value;
    }

    protected RoutingContextImpl(long[] routingcontexts) {
        this.tag = Parameter.Routing_Context;
        rcs = routingcontexts;
        encode();
    }

    private void encode() {
        // create byte array taking into account data, point codes and
        // indicators;
        this.value = new byte[(rcs.length * 4)];
        int count = 0;
        int arrSize = 0;
        // encode routing context
        while (count < value.length) {
            value[count++] = (byte) (rcs[arrSize] >>> 24);
            value[count++] = (byte) (rcs[arrSize] >>> 16);
            value[count++] = (byte) (rcs[arrSize] >>> 8);
            value[count++] = (byte) (rcs[arrSize++]);
        }
    }

    public long[] getRoutingContexts() {
        return this.rcs;
    }

    @Override
    protected byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("RoutingContext rc=%s", Arrays
                .toString(rcs));
    }
}
