image:https://travis-ci.org/arquillian/arquillian-extension-jacoco.svg?branch=master["Build Status", link="https://travis-ci.org/arquillian/arquillian-extension-jacoco"]
image:https://img.shields.io/maven-central/v/org.jboss.arquillian.extension/arquillian-jacoco["Maven Central", link="https://maven-badges.herokuapp.com/maven-central/org.jboss.arquillian.extension/arquillian-jacoco"]


== Arquillian Jacoco Extension

Automagic Remote InContainer Code Coverage

=== Usage

==== Maven default setup

Add the following to your pom.xml:


Set the versions to use:

[source, xml]
----
...
<properties>
   <version.jacoco>0.8.5</version.jacoco>
   <version.arquillian_jacoco>1.1.0</version.arquillian_jacoco>
</properties>
...
----

Configure the Jacoco Maven plugin and depend on the Arquillian Jacoco Extension:

[source, xml]
----
...
<profile>
   <id>jacoco</id>
   <dependencies>
      <dependency>
         <groupId>org.jacoco</groupId>
         <artifactId>org.jacoco.core</artifactId>
         <version>${version.jacoco}</version>
         <scope>test</scope>
      </dependency>
      <dependency>
         <groupId>org.jboss.arquillian.extension</groupId>
         <artifactId>arquillian-jacoco</artifactId>
         <version>${version.arquillian_jacoco}</version>
         <scope>test</scope>
      </dependency>
   </dependencies>
   <build>
      <plugins>
         <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>${version.jacoco}</version>
            <executions>
               <execution>
                  <goals>
                     <goal>prepare-agent</goal>
                  </goals>
               </execution>
               <execution>
                  <id>report</id>
                  <phase>prepare-package</phase>
                  <goals>
                     <goal>report</goal>
                  </goals>
               </execution>
            </executions>
         </plugin>
      </plugins>
   </build>
</profile>
----

Activate this profile on command line by using the -P flag:

[source, console]
----
mvn test -Pjacoco
----

Please note that `prepare-agent` will set a property that is picked up by `maven-surefire-plugin` by default
(see https://www.eclemma.org/jacoco/trunk/doc/prepare-agent-mojo.html[documentation]).

==== Maven setup with shaded ASM and jacoco-core

JaCoCo requires ASM to work properly. Since ASM is also used by many other libraries like Apache CXF, you might run into version conflicts. +
E.g. JBoss EAP 6.4 ships CXF 2.7 which requires ASM 3 but JaCoCo requires ASM 7+.

As a workaround, this extension provides an alternate `with-asm` flavour that includes "private" ASM and JaCoCo packages, shaded via `maven-shade-plugin`.

You only need a single dependency for this setup:
[source, xml]
----
...
   <dependencies>
      <dependency>
         <groupId>org.jboss.arquillian.extension</groupId>
         <artifactId>arquillian-jacoco-with-asm</artifactId>
         <version>${version.arquillian_jacoco}</version>
         <scope>test</scope>
      </dependency>
   </dependencies>
----

Please note the absence of the `jacoco-core` dependency. This also means that it is _not_ possible to choose a custom JaCoCo version.

==== (Optional) arquillian.xml

This extension can by configured via `arquillian.xml`, e.g.:

[source, xml]
----
...
<extension qualifier="jacoco">
   <property name="includes">org.foo.*; org.bar.*</property>
   <property name="excludes">org.bar.baz.*</property>
   <property name="appendAsmLibrary">true</property>
</extension>
----

`appendAsmLibrary` will deploy the ASM library (which is used by JaCoCo) to the server. Defaults to `true`. +
This can be set to `false` in case the container already provides a suitable version of ASM. +
In case the `with-asm` flavour is used this property _must_ be set to `true`.

==== Sonar

When using the Arquillian Jacoco Extension with Sonar you only need to depend on the Arquillian Jacoco Extension. Sonar will handle the setup of Jacoco for you.

