/*
 * Copyright (c) 2020 Pedro Albuquerque Santos.
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

import android.util.Base64;
import android.util.Log;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;

public class JwkKeyResolver extends SigningKeyResolverAdapter {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + JwkKeyResolver.class.getSimpleName();
    private static final String PUBLIC_KEY_ALGORITHM = "RSA";

    private final String baseUrl;

    public JwkKeyResolver(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
        try {
            String jku = (String) jwsHeader.get("jku");
            Log.d(LOG_TAG, "JKU: " + jku);
            if (jku != null && jku.startsWith(baseUrl)) {
                JwkProvider keyStore = new UrlJwkProvider(new URL(jku));
                String keyId = jwsHeader.getKeyId();
                try { return keyStore.get(keyId).getPublicKey(); }
                catch (NoSuchMethodError e) { return getPublicKey(keyStore.get(keyId)); }
            } else { throw new RuntimeException("'JKU' is missing or cannot be trusted."); }
        } catch (JwkException | MalformedURLException e) {
            throw new RuntimeException("Could not load JWK.", e);
        }
    }

    private PublicKey getPublicKey(Jwk jwk) throws InvalidPublicKeyException {
        if (!PUBLIC_KEY_ALGORITHM.equalsIgnoreCase(jwk.getType())) { throw new InvalidPublicKeyException("The key is not of type RSA", null); }
        try {
            KeyFactory kf = KeyFactory.getInstance(PUBLIC_KEY_ALGORITHM);
            BigInteger modulus = new BigInteger(1, Base64.decode(
                    ((String) jwk.getAdditionalAttributes().get("n")).getBytes(StandardCharsets.UTF_8),
                    Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE)
            );
            BigInteger exponent = new BigInteger(1, Base64.decode(
                    ((String) jwk.getAdditionalAttributes().get("e")).getBytes(StandardCharsets.UTF_8),
                    Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE)
            );
            return kf.generatePublic(new RSAPublicKeySpec(modulus, exponent));
        }
        catch (InvalidKeySpecException e) { throw new InvalidPublicKeyException("Invalid public key", e); }
        catch (NoSuchAlgorithmException e) { throw new InvalidPublicKeyException("Invalid algorithm to generate key", e); }
    }
}
