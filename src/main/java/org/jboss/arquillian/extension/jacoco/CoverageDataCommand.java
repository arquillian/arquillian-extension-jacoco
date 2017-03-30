package org.jboss.arquillian.extension.jacoco;

import java.io.Serializable;

import org.jboss.arquillian.container.test.spi.command.Command;

public class CoverageDataCommand implements Command<String>, Serializable {
    private static final long serialVersionUID = 1L;

    private byte[] coverageDate;
    private String result;
    private Throwable failure;

    public CoverageDataCommand(byte[] coverageData) {
        this.coverageDate = coverageData;
    }

    public byte[] getCoverageDate() {
        return coverageDate;
    }

    @Override
    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void setThrowable(Throwable throwable) {
        this.failure = throwable;
    }

    @Override
    public Throwable getThrowable() {
        return failure;
    }
}
