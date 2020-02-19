package org.jboss.arquillian.extension.jacoco;

/**
 * This file exists purely to satisfy Maven release validation process, as jar modules are expected to
 * contain -sources and -javadoc JARs.
 *
 * JaCoCo requires ASM to work properly. Since ASM is also used by many other libraries like Apache CXF, you might run into version conflicts.
 * E.g. JBoss EAP 6.4 ships CXF 2.7 which requires ASM 3 but JaCoCo requires ASM 7+.
 *
 * As a workaround, this modules provides an alternate `with-asm` flavour that includes "private" ASM and JaCoCo packages, shaded via `maven-shade-plugin`.
 */
public interface About {
}
