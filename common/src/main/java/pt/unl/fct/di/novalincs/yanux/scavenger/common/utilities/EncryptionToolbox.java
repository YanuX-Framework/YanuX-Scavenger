/*
 * Copyright (c) 2019 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger. If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities;

import android.content.Context;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

//TODO: Test if everything works with Bouncy Castle in place of Spongy Castle

public class EncryptionToolbox {
    private static String CERT_PATH = "public.pem";

    static {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.addProvider(new BouncyCastleProvider());
    }

    public static PublicKey getPublicKey(Context ctx) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        InputStream inputStream = ctx.getAssets().open(CERT_PATH);
        Reader reader = new InputStreamReader(inputStream);
        PemObject pemObject = new PemReader(reader).readPemObject();
        PublicKey key = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME).generatePublic(new X509EncodedKeySpec(pemObject.getContent()));
        return key;
    }
}