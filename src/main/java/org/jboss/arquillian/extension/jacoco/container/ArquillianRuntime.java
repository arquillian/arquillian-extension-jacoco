/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.extension.jacoco.container;

import org.jacoco.core.internal.instr.InstrSupport;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.UUID;

/**
 * ArquillianRuntime
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ArquillianRuntime implements IRuntime {
    private static ArquillianRuntime runtime = null;

    public static synchronized ArquillianRuntime getInstance() {
        if (runtime == null) {
            runtime = new ArquillianRuntime();
        }
        return runtime;
    }

    private RuntimeData runtimeData;

    /**
     *
     */
    private ArquillianRuntime() {
        runtimeData = new RuntimeData();
        runtimeData.setSessionId(UUID.randomUUID().toString());
    }

    /**
     * Retrieves the execution probe array for a given class. The passed
     * {@link Object} array instance is used for parameters and the return value
     * as follows. Call parameters:
     * <p>
     * <ul>
     * <li>args[0]: class id ({@link Long})
     * <li>args[1]: vm class name ({@link String})
     * <li>args[2]: probe count ({@link Integer})
     * </ul>
     * <p>
     * Return value:
     * <p>
     * <ul>
     * <li>args[0]: probe array (<code>boolean[]</code>)
     * </ul>
     *
     * @param args
     *     parameter array of length 3
     */
    public void swapExecutionData(Object[] args) {
        final Long classid = (Long) args[0];
        final String name = (String) args[1];
        final int probecount = (Integer) args[2];
        synchronized (runtimeData) {
            args[0] = runtimeData.getExecutionData(classid, name, probecount).getProbes();
        }
    }

    RuntimeData getRuntimeData() {
        return runtimeData;
    }

    /* (non-Javadoc)
     * @see org.jacoco.core.runtime.IRuntime#startup()
     */
    public void startup(RuntimeData rd) throws Exception {
        this.runtimeData = rd;
    }

    /* (non-Javadoc)
     * @see org.jacoco.core.runtime.IRuntime#shutdown()
     */
    public void shutdown() {
    }

    /* (non-Javadoc)
     * @see org.jacoco.core.runtime.IRuntime#reset()
     */
    public void reset() {
        runtimeData.reset();
    }

    /* (non-Javadoc)
     * @see org.jacoco.core.runtime.IExecutionDataAccessorGenerator#generateDataAccessor(long, java.lang.String, int, org.objectweb.asm.MethodVisitor)
     */
    public int generateDataAccessor(long classid, String classname, int probecount, MethodVisitor mv) {
        // 1. Create parameter array:
        generateArgumentArray(classid, classname, probecount, mv);
        // stack[0]: [Ljava/lang/Object;

        mv.visitInsn(Opcodes.DUP);

        // stack[1]: [Ljava/lang/Object;
        // stack[0]: [Ljava/lang/Object;

        // 2. Invoke ArquillianRuntime:
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/jboss/arquillian/extension/jacoco/container/ArquillianRuntime",
            "getInstance",
            "()Lorg/jboss/arquillian/extension/jacoco/container/ArquillianRuntime;", false);

        // stack[2]: LArquillianRuntime;
        // stack[1]: [Ljava/lang/Object;
        // stack[0]: [Ljava/lang/Object;

        mv.visitInsn(Opcodes.SWAP);

        // stack[2]: [Ljava/lang/Object;
        // stack[1]: LArquillianRuntime;
        // stack[0]: [Ljava/lang/Object;

        // 3. Invoke ArquillianRuntime swapExecutionData, gets the boolean[] in Object[0]:
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/jboss/arquillian/extension/jacoco/container/ArquillianRuntime",
            "swapExecutionData",
            "([Ljava/lang/Object;)V", false);

        // Stack[0]: [Ljava/lang/Object;

        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitInsn(Opcodes.AALOAD);
        mv.visitTypeInsn(Opcodes.CHECKCAST, InstrSupport.DATAFIELD_DESC);

        // Stack[0]: [Z;

        return 5;
    }

    /**
     * Generates code that creates the argument array for the
     * <code>getExecutionData()</code> method. The array instance is left on the
     * operand stack. The generated code requires a stack size of 5.
     *
     * @param classid
     *     class identifier
     * @param classname
     *     VM class name
     * @param probecount
     *     probe count for this class
     * @param mv
     *     visitor to emit generated code
     */
    public static void generateArgumentArray(final long classid, final String classname, final int probecount,
        final MethodVisitor mv) {
        mv.visitInsn(Opcodes.ICONST_3);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");

        // Class Id:
        mv.visitInsn(Opcodes.DUP);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitLdcInsn(classid);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        mv.visitInsn(Opcodes.AASTORE);

        // Class Name:
        mv.visitInsn(Opcodes.DUP);
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitLdcInsn(classname);
        mv.visitInsn(Opcodes.AASTORE);

        // Probe Count:
        mv.visitInsn(Opcodes.DUP);
        mv.visitInsn(Opcodes.ICONST_2);
        InstrSupport.push(mv, probecount);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        mv.visitInsn(Opcodes.AASTORE);
    }
}
