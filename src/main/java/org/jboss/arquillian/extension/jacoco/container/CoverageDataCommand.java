package org.jboss.arquillian.extension.jacoco.container;

import org.jboss.arquillian.container.test.impl.client.deployment.command.AbstractCommand;

public class CoverageDataCommand<ByteArrayOutputStream> extends AbstractCommand<ByteArrayOutputStream> {

    /**
     * Generated id
     */
    private static final long serialVersionUID = -4105223045546010016L;

    public CoverageDataCommand(ByteArrayOutputStream coverageData) {
        super();
        this.setResult(coverageData);
    }

}
