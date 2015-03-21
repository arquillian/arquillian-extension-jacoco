/*
 * Copyright (C) 2015 Computer Science Corporation
 * All rights reserved.
 *
 */
package org.jboss.arquillian.extension.jacoco.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * @author arcivanov
 */
public class ManifestAsset implements Asset
{
    private static final org.jacoco.core.internal.instr.SignatureRemover JACOCO_SIG_REMOVER =
            new org.jacoco.core.internal.instr.SignatureRemover();

    private Asset asset;

    public ManifestAsset(Asset asset)
    {
        this.asset = asset;
    }

    /* (non-Javadoc)
     * @see org.jboss.shrinkwrap.api.asset.Asset#openStream()
     */
    public InputStream openStream()
    {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            JACOCO_SIG_REMOVER.filterEntry("META-INF/MANIFEST.MF", asset.openStream(), out);

            return new ByteArrayInputStream(out.toByteArray());
        }
        catch (Exception e) {
            throw new RuntimeException("Could not instrument MANIFEST.MF Asset " + asset, e);
        }
    }
}