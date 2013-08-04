package com.github.marschall.jdtavailabilitycheck;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassLoaderBean {


    public boolean isCheckPossible() {
        Method isRegistered = getIsRegisteredMethod();
        return isRegistered != null
                && isRegistered.getReturnType() == Boolean.TYPE
                && Modifier.isStatic(isRegistered.getModifiers());
    }

    public boolean isApplicationClassLoaderThreadContextClassLoader() {
        return this.getApplicationClassLoader() == this.getThreadContextClassLoader();
    }

    public String getThreadContextClassLoaderName() {
        return getThreadContextClassLoader().getClass().getName();
    }

    public boolean isThreadContextClassLoaderParallelCapable() {
        return isParallelCapable(getThreadContextClassLoader());
    }

    public String getApplicationClassLoaderName() {
        return getApplicationClassLoader().getClass().getName();
    }

    public boolean isApplicationClassLoaderParallelCapable() {
        return isParallelCapable(getApplicationClassLoader());
    }

    private ClassLoader getThreadContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private ClassLoader getApplicationClassLoader() {
        return ClassLoaderBean.class.getClassLoader();
    }

    private boolean isParallelCapable(ClassLoader classLoader) {
        Method isRegisteredMethod = getIsRegisteredMethod();
        if (isRegisteredMethod == null) {
            return false;
        }
        try {
            Object result = isRegisteredMethod.invoke(null, classLoader.getClass());
            return (Boolean) result;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    private Method getIsRegisteredMethod() {
        for (Class<?> clazz : ClassLoader.class.getDeclaredClasses()) {
            if (clazz.getName().equals("java.lang.ClassLoader$ParallelLoaders")) {
                try {
                    Method isRegistered = clazz.getDeclaredMethod("isRegistered", Class.class);
                    isRegistered.setAccessible(true);
                    return isRegistered;
                } catch (NoSuchMethodException e) {
                    return null;
                }
            }
        }
        return null;
    }

}