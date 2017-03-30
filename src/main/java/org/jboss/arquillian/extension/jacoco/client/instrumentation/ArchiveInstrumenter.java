package org.jboss.arquillian.extension.jacoco.client.instrumentation;

import org.jboss.arquillian.extension.jacoco.client.JaCoCoApplicationArchiveProcessor;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArchiveInstrumenter {

    private static final Logger LOGGER = Logger.getLogger(JaCoCoApplicationArchiveProcessor.class.getName());

    private final SignatureRemover signatureRemover;

    public ArchiveInstrumenter(SignatureRemover signatureRemover) {
        this.signatureRemover = signatureRemover;
    }

    public void processArchive(Archive<?> archive, Filter<ArchivePath> filter) {
        instrument(archive, archive.getContent(filter));
        signatureRemover.removeSignatures(archive);

        final Map<ArchivePath, Node> jars = archive.getContent(Filters.include(".*\\.(jar|war|rar|ear)$"));
        for (Map.Entry<ArchivePath, Node> entry : jars.entrySet()) {
            // Should have used genericArchive, but with GenericArchive we need
            // to specify a ArchiveFormat and that trigger this SHRINKWRAP-474
            final JavaArchive subArchive = archive.getAsType(JavaArchive.class, entry.getKey());
            if (subArchive == null) // [ARQ-1931] Asset is null which means we are dealing with the directory
            {
                LOGGER.log(Level.WARNING,
                    String.format("directory path %s  ends one of the suffixes: [.ear, .war, .rar, .jar]",
                        entry.getValue()));
            } else {
                processArchive(subArchive, filter);
            }
        }
    }

    private void instrument(Archive<?> archive, Map<ArchivePath, Node> classes) {
        for (Map.Entry<ArchivePath, Node> entry : classes.entrySet()) {
            final Asset original = entry.getValue().getAsset();
            archive.delete(entry.getKey());
            archive.add(new InstrumentedAsset(original), entry.getKey());
        }
    }
}
