package com.edwin.shakacore.component.clazz;

import java.io.File;
import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.shakacore.exception.ShakaException;
import com.edwin.shakapersist.entity.ShakaJob;

/**
 * 想复杂了，少年
 * 
 * @author jinming.wu
 * @date 2015-5-29
 */
@Deprecated
public class JavassistClassGenerator {

    private static Logger       logger         = LoggerFactory.getLogger(JavassistClassGenerator.class);

    private static String       NEW_CLAZZ_NAME = "com.edwin.shakacore.quartz.DefaultQuartzJob$Job";
    private static final String OLD_CLAZZ_NAME = "com.edwin.shakacore.quartz.DefaultQuartzJob";
    private static final String PARAM_CLAZZ    = "org.quartz.JobExecutionContext";
    private static final String JOB_CLAZZ      = "org.quartz.Job";
    private static final String EXECUTE_METHOD = "execute";
    private static final String INIT           = "init";

    private static final String LOGGER_FIELD   = "logger";
    private static final String LOGGER_CLAZZ   = "org.slf4j.Logger";

    private static final String IDGENERATOR    = "com.edwin.shakacore.id.IDGenerator";
    private static final String SHAKAJOBDAO    = "com.edwin.shakapersist.dao.ShakaJobDao";
    private static final String SHAKATASKDAO   = "com.edwin.shakapersist.dao.ShakaTaskDao";
    private static final String JOBMANAGER     = "com.edwin.shakacore.manager.JobManager";
    private static final String QTZSHAKAJOB    = "java.util.Map";

    public static final String  CLAZZ_DIR      = "/data/appdatas/clazz";

    public static final String  COMMAND        = "sudo chmod 777 " + CLAZZ_DIR;

    public JavassistClassGenerator() {
        File file = new File(CLAZZ_DIR);

        deleteFile(file);

        file.mkdir();
        try {
            Runtime.getRuntime().exec(COMMAND);
        } catch (IOException e) {
            throw new ShakaException(e);
        }
    }

    /**
     * generate class
     * 
     * @param shakJob
     * @return
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public Class<?> generateClass(ShakaJob shakJob) throws NotFoundException, CannotCompileException, IOException {

        ClassPool classPool = ClassPool.getDefault();
        String newClazzName = NEW_CLAZZ_NAME + shakJob.getJobId();

        // create new class
        CtClass newClazz = classPool.makeClass(newClazzName);
        newClazz.stopPruning(true);

        // add job interface
        newClazz.addInterface(classPool.get(JOB_CLAZZ));

        initMethod(classPool, newClazz);

        initField(newClazz);

        initConstructor(newClazz);

        newClazz.writeFile(CLAZZ_DIR);

        logger.info("Generate class " + newClazz.getName());

        return newClazz.toClass();
    }

    private void initMethod(ClassPool classPool, CtClass newClazz) throws NotFoundException, CannotCompileException {

        // get default class
        CtClass oldClazz = classPool.get(OLD_CLAZZ_NAME);

        // get param of execute method
        CtClass[] param = new CtClass[] { classPool.get(PARAM_CLAZZ) };

        // copy execute method
        CtMethod oldExecuteMethod = oldClazz.getDeclaredMethod(EXECUTE_METHOD, param);
        CtMethod newExecuteMethod = CtNewMethod.copy(oldExecuteMethod, newClazz, null);
        newClazz.addMethod(newExecuteMethod);

        // 复制需要init方法
        CtMethod oldInitMethod = oldClazz.getDeclaredMethod(INIT, null);
        CtMethod newInitMethod = CtNewMethod.copy(oldInitMethod, newClazz, null);
        newClazz.addMethod(newInitMethod);
    }

    // init constructor
    private void initConstructor(CtClass newClazz) throws CannotCompileException {
        CtConstructor constructor = new CtConstructor(null, newClazz);
        constructor.setBody("{init();}");
        newClazz.addConstructor(constructor);
    }

    private void initField(CtClass newClazz) throws CannotCompileException {

        // init params
        CtField loggerField = CtField.make("private " + LOGGER_CLAZZ + " " + LOGGER_FIELD
                                           + " = org.slf4j.LoggerFactory.getLogger(getClass());", newClazz);
        newClazz.addField(loggerField);

        CtField idGenerator = CtField.make("private " + IDGENERATOR + " generator;", newClazz);
        newClazz.addField(idGenerator);

        CtField shakaJobDao = CtField.make("private " + SHAKAJOBDAO + " shakaJobDao;", newClazz);
        newClazz.addField(shakaJobDao);

        CtField shakaTaskDao = CtField.make("private " + SHAKATASKDAO + " shakaTaskDao;", newClazz);
        newClazz.addField(shakaTaskDao);

        CtField jobManager = CtField.make("private " + JOBMANAGER + " jobManager;", newClazz);
        newClazz.addField(jobManager);

        CtField qtzShakaJobMap = CtField.make("private " + QTZSHAKAJOB + " qtzShakaJobMap;", newClazz);
        newClazz.addField(qtzShakaJobMap);
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    this.deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            logger.info("The directory need to delete is not exist！");
        }
    }

    public void clear() {
        deleteFile(new File(CLAZZ_DIR));
    }
}
