package com.edwin.shakacore.component.clazz;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import org.junit.Test;

import com.edwin.shakapersist.entity.ShakaJob;

/**
 * @author jinming.wu
 * @date 2015-5-29
 */
public class JavassistClassGeneratorTest {

    JavassistClassGenerator classGenerator = new JavassistClassGenerator();

    @Test
    public void testClear() {
        classGenerator.clear();
    }

    @Test
    public void testGenerateClass() throws NotFoundException, CannotCompileException, IOException {
        ShakaJob shakaJob = new ShakaJob();
        shakaJob.setJobId(1);
        JavassistClassGenerator javassistClassGenerator = new JavassistClassGenerator();
        javassistClassGenerator.generateClass(shakaJob);
    }
}
