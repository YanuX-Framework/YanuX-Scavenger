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

import android.util.Log;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;

public class JwkKeyResolver extends SigningKeyResolverAdapter {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + JwkKeyResolver.class.getSimpleName();

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
                return keyStore.get(keyId).getPublicKey();
            } else {
                throw new RuntimeException("'JKU' is missing or cannot be trusted.");
            }
        } catch (JwkException | MalformedURLException e) {
            throw new RuntimeException("Could not load JWK.", e);
        }
    }
}
