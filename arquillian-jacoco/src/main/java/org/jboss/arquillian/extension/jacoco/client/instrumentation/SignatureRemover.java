/*
 * Copyright (C) 2015 Computer Science Corporation
 * All rights reserved.
 *
 */
package org.jboss.arquillian.extension.jacoco.client.instrumentation;

import java.util.Map;
import java.util.Map.Entry;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * Signed jars must have their signatures removed. Since {@link InstrumentedAsset} only deals with classes we must
 * actually
 * perform the duties of {@link org.jacoco.core.internal.instr.SignatureRemover} ourselves
 *
 * @author arcivanov
 * @see org.jacoco.core.internal.instr.SignatureRemover
 */
public class SignatureRemover {
    public void removeSignatures(Archive<?> archive) {
        removeSignatureFiles(archive);
        removeManifestDigests(archive);
    }

    public void removeSignatureFiles(Archive<?> archive) {
        // Remove signatures files if any
        Map<ArchivePath, Node> signatureFiles = getSignatureFiles(archive);
        for (Entry<ArchivePath, Node> entry : signatureFiles.entrySet()) {
            // We don't want these at all - remove
            archive.delete(entry.getKey());
        }
    }

    public void removeManifestDigests(Archive<?> archive) {
        Map<ArchivePath, Node> manifests = getManifestFiles(archive);
        for (Entry<ArchivePath, Node> entry : manifests.entrySet()) {
            Asset original = entry.getValue().getAsset();
            archive.delete(entry.getKey());
            archive.add(new ManifestAsset(original), entry.getKey());
        }
    }

    public Map<ArchivePath, Node> getSignatureFiles(Archive<?> archive) {
        return archive.getContent(//
            // This is adapted from Jacoco SignatureRemover
            Filters.include("/META-INF/[^/]*\\.SF|" //
                + "/META-INF/[^/]*\\.DSA|" //
                + "/META-INF/[^/]*\\.RSA|" //
                + "/META-INF/SIG-[^/]*"));
    }

    public Map<ArchivePath, Node> getManifestFiles(Archive<?> archive) {
        return archive.getContent(Filters.include("/META-INF/MANIFEST\\.MF"));
    }
}
