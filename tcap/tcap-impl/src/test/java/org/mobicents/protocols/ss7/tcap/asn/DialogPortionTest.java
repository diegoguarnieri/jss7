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

package org.mobicents.protocols.ss7.tcap.asn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnException;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.ss7.tcap.asn.DialogPortionImpl;
import org.mobicents.protocols.ss7.tcap.asn.ParseException;

import junit.framework.TestCase;

/**
 * @author baranowb
 * @author amit bhayani
 */
public class DialogPortionTest extends TestCase {

	public void testDialogPortion_UserInformation() throws Exception {

		// Hex dump is from wireshark trace for TCAP - MAP/USSD
		byte[] b = new byte[] { 0x6b, 0x41, 0x28, 0x3f, 0x06, 0x07, 0x00, 0x11,
				(byte) 0x86, 0x05, 0x01, 0x01, 0x01, (byte) 0xa0, 0x34, 0x60,
				0x32, (byte) 0xa1, 0x09, 0x06, 0x07, 0x04, 0x00, 0x00, 0x01,
				0x00, 0x13, 0x02, (byte) 0xbe, 0x25, 0x28, 0x23, 0x06, 0x07,
				0x04, 0x00, 0x00, 0x01, 0x01, 0x01, 0x01, (byte) 0xa0, 0x18,
				(byte) 0xa0, (byte) 0x80, (byte) 0x80, 0x09, (byte) 0x96, 0x02,
				0x24, (byte) 0x80, 0x03, 0x00, (byte) 0x80, 0x00, (byte) 0xf2,
				(byte) 0x81, 0x07, (byte) 0x91, 0x13, 0x26, (byte) 0x98,
				(byte) 0x86, 0x03, (byte) 0xf0, 0x00, 0x00 };

		AsnInputStream asin = new AsnInputStream(new ByteArrayInputStream(b));
		asin.readTag();
		DialogPortionImpl dpi = new DialogPortionImpl();
		dpi.decode(asin);

		long[] oidValue = dpi.getOidValue();
		Arrays.equals(new long[] { 0, 0, 17, 773, 1, 1, 1 }, oidValue);

		DialogAPDU dialogAPDU = dpi.getDialogAPDU();

		assertNotNull(dialogAPDU);

		assertEquals(DialogAPDUType.Request, dialogAPDU.getType());

		DialogRequestAPDU dialogRequestAPDU = (DialogRequestAPDU) dialogAPDU;

		ApplicationContextName acn = dialogRequestAPDU
				.getApplicationContextName();

		assertNotNull(acn);

		assertTrue(Arrays.equals(new long[] { 0, 4, 0, 0, 1, 0, 19, 2 }, acn
				.getOid()));

		UserInformation userInfos = dialogRequestAPDU.getUserInformation();

		assertNotNull(userInfos);

		UserInformation userInformation = userInfos;

		assertTrue(userInformation.isOid());

		assertTrue(Arrays.equals(new long[] { 0, 4, 0, 0, 1, 1, 1, 1 },
				userInformation.getOidValue()));

		assertFalse(userInformation.isInteger());

		assertTrue(userInformation.isAsn());
		assertTrue(Arrays.equals(new byte[] { (byte) 0xa0, (byte) 0x80,
				(byte) 0x80, 0x09, (byte) 0x96, 0x02, 0x24, (byte) 0x80, 0x03,
				0x00, (byte) 0x80, 0x00, (byte) 0xf2, (byte) 0x81, 0x07,
				(byte) 0x91, 0x13, 0x26, (byte) 0x98, (byte) 0x86, 0x03,
				(byte) 0xf0, 0x00, 0x00 }, userInformation.getEncodeType()));

	}

	@org.junit.Test
	public void testDialogPortion_DialogRequestAPDU() throws IOException,
			ParseException {
		// trace
		byte[] b = new byte[] { 107, 30, 40, 28, 6, 7, 0, 17, (byte) 134, 5, 1,
				1, 1, (byte) 160, 17, 0x60, 15, (byte) 128, 2, 7, (byte) 128,
				(byte) 161, 9, 6, 7, 4, 0, 1, 1, 1, 3, 0 };
		// In HEX
		// 6B 1E 28 1C 06 07 00 11 86 05 01 01 01 A0 11 60 0F 80 02 07 80 A1 09
		// 06 07 04 00 01 01 01 03 00

		AsnInputStream asin = new AsnInputStream(new ByteArrayInputStream(b));
		asin.readTag();
		DialogPortionImpl dpi = new DialogPortionImpl();
		dpi.decode(asin);

		AsnOutputStream aso = new AsnOutputStream();
		dpi.encode(aso);
		byte[] encoded = aso.toByteArray();
		assertTrue(Arrays.equals(b, encoded));

		try {
			dpi.getEncodeBitStringType();
			fail();
		} catch (UnsupportedOperationException e) {

		}
		try {
			encoded = null;
			encoded = dpi.getEncodeType();
			assertNotNull(encoded);

		} catch (UnsupportedOperationException e) {
			fail();
			e.printStackTrace();
		} catch (AsnException e) {
			fail();
			e.printStackTrace();
		}
		assertTrue(dpi.isAsn());
		assertTrue(dpi.isOid());
		assertFalse(dpi.isUnidirectional());

		assertFalse(dpi.isArbitrary());
		assertFalse(dpi.isOctet());
		assertFalse(dpi.isObjDescriptor());
		assertFalse(dpi.isInteger());

		DialogAPDU _apid = dpi.getDialogAPDU();
		assertEquals(DialogAPDUType.Request, _apid.getType());
		assertFalse(_apid.isUniDirectional());
		DialogRequestAPDU apdu = (DialogRequestAPDU) _apid;

		// no idea how to check rest...?

	}

	@org.junit.Test
	public void testDialogPortion_DialogAbortAPDU() throws IOException,
			ParseException {

		// trace
		byte[] b = new byte[] { 0x6B, 0x12, 0x28, 0x10, 0x06, 0x07, 0x00, 0x11,
				(byte) 0x86, 0x05, 0x01, 0x01, 0x01, (byte) 0xA0, 0x05, 0x64,
				0x03, (byte) 0x80, 0x01, 0x01 };
		AsnInputStream asin = new AsnInputStream(new ByteArrayInputStream(b));
		asin.readTag();
		DialogPortionImpl dpi = new DialogPortionImpl();
		dpi.decode(asin);
		AsnOutputStream aso = new AsnOutputStream();
		dpi.encode(aso);
		byte[] encoded = aso.toByteArray();
		assertTrue(Arrays.equals(b, encoded));

		try {
			dpi.getEncodeBitStringType();
			fail();
		} catch (UnsupportedOperationException e) {

		}
		try {
			encoded = null;
			encoded = dpi.getEncodeType();
			assertNotNull(encoded);

		} catch (UnsupportedOperationException e) {
			fail();
			e.printStackTrace();
		} catch (AsnException e) {
			fail();
			e.printStackTrace();
		}
		assertTrue(dpi.isAsn());
		assertTrue(dpi.isOid());
		assertFalse(dpi.isUnidirectional());

		assertFalse(dpi.isArbitrary());
		assertFalse(dpi.isOctet());
		assertFalse(dpi.isObjDescriptor());
		assertFalse(dpi.isInteger());

		DialogAPDU _apid = dpi.getDialogAPDU();
		assertEquals(DialogAPDUType.Abort, _apid.getType());
		assertFalse(_apid.isUniDirectional());
		DialogAbortAPDU apdu = (DialogAbortAPDU) _apid;
	}

	// @org.junit.Test
	// public void testDialogPortion_DialogResponseAPDU() throws IOException,
	// ParseException {
	//		
	//
	// byte[] b = new byte[] { 0x6B, 0x2A, /*EXT_T*/0x28, 0x28, /*OID_T*/0x06,
	// 0x07, 0x00, 0x11, (byte)0x86, 0x05, 0x01, 0x01, 0x01,
	// /*ASN_T*/(byte)0xA0, /*29*/0x1D, /*REsponse_T*/0x61, 0x1B, /*rest of
	// trace is gone... ech*/ };
	// System.err.println(b.length);
	// AsnInputStream asin = new AsnInputStream(new ByteArrayInputStream(b));
	// asin.readTag();
	// DialogPortionImpl dpi = new DialogPortionImpl();
	// dpi.decode(asin);
	// AsnOutputStream aso = new AsnOutputStream();
	// dpi.encode(aso);
	// byte[] encoded = aso.toByteArray();
	// assertTrue(Arrays.equals(b, encoded));
	//		
	// try{
	// dpi.getEncodeBitStringType();
	// fail();
	// }catch(UnsupportedOperationException e)
	// {
	//			
	// } catch (AsnException e) {
	// fail();
	// e.printStackTrace();
	// }
	// try{
	// encoded = null;
	// encoded = dpi.getEncodeType();
	// assertNotNull(encoded);
	//			
	// }catch(UnsupportedOperationException e)
	// {
	// fail();
	// e.printStackTrace();
	// } catch (AsnException e) {
	// fail();
	// e.printStackTrace();
	// }
	// assertTrue(dpi.isAsn());
	// assertTrue(dpi.isOid());
	// assertFalse(dpi.isUnidirectional());
	//		
	// assertFalse(dpi.isArbitrary());
	// assertFalse(dpi.isOctet());
	// assertFalse(dpi.isObjDescriptor());
	// assertFalse(dpi.isInteger());
	//		
	//		
	// // DialogAPDU _apid = dpi.getDialogAPDU();
	// // assertEquals(DialogAPDUType.Abort, _apid.getType());
	// // assertFalse(_apid.isUniDirectional());
	// // DialogAbortAPDU apdu = (DialogAbortAPDU) _apid;
	// }

}
