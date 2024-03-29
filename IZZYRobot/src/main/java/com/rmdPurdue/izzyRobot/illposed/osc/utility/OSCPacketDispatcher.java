/*
 * Copyright (C) 2003-2014, C. Ramakrishnan / Auracle.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package com.rmdPurdue.izzyRobot.illposed.osc.utility;

import com.rmdPurdue.izzyRobot.illposed.osc.AddressSelector;
import com.rmdPurdue.izzyRobot.illposed.osc.OSCBundle;
import com.rmdPurdue.izzyRobot.illposed.osc.OSCListener;
import com.rmdPurdue.izzyRobot.illposed.osc.OSCMessage;
import com.rmdPurdue.izzyRobot.illposed.osc.OSCPacket;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Dispatches {@link OSCPacket}s to registered listeners (<i>Method</i>s).
 *
 * @author Chandrasekhar Ramakrishnan
 */
public class OSCPacketDispatcher {

	private final Map<AddressSelector, OSCListener> selectorToListener;

	public OSCPacketDispatcher() {
		this.selectorToListener = new HashMap<AddressSelector, OSCListener>();
	}

	/**
	 * Adds a listener (<i>Method</i> in OSC speak) that will be notified
	 * of incoming messages that match the selector.
	 * @param addressSelector selects which messages will be forwarded to the listener,
	 *   depending on the message address
	 * @param listener receives messages accepted by the selector
	 */
	public void addListener(final AddressSelector addressSelector, final OSCListener listener) {
		selectorToListener.put(addressSelector, listener);
	}

	public void removeListener(final AddressSelector addressSelector, final OSCListener listener) {
		selectorToListener.remove(addressSelector);
	}

	public void dispatchPacket(final OSCPacket packet) {
		dispatchPacket(packet, null);
	}

	public void dispatchPacket(final OSCPacket packet, final Date timestamp) {
		if (packet instanceof OSCBundle) {
			dispatchBundle((OSCBundle) packet);
		} else {
			dispatchMessage((OSCMessage) packet, timestamp);
		}
	}

	private void dispatchBundle(final OSCBundle bundle) {
		final Date timestamp = bundle.getTimestamp();
		final List<OSCPacket> packets = bundle.getPackets();
		for (final OSCPacket packet : packets) {
			dispatchPacket(packet, timestamp);
		}
	}

	private void dispatchMessage(final OSCMessage message, final Date time) {
		for (final Entry<AddressSelector, OSCListener> addrList : selectorToListener.entrySet()) {
			if (addrList.getKey().matches(message.getAddress())) {
				addrList.getValue().acceptMessage(time, message);
			}
		}
	}
}
