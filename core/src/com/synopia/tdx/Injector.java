package com.synopia.tdx;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.google.common.collect.Maps;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.FilterBuilder;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * Created by synopia on 07.01.2015.
 */
public class Injector {
    private Map<Class, Object> dependencies = Maps.newHashMap();

    public void add( Class cls, Object obj ) {
        dependencies.put(cls, obj);
    }

    public <T> T get(Class<T> cls) {
        return (T) dependencies.get(cls);
    }

    public void inject(Object obj) {
        Set<Field> injectableFields = ReflectionUtils.getAllFields(obj.getClass(), ReflectionUtils.withAnnotation(Inject.class));
        for (Field injectableField : injectableFields) {

            Class type = injectableField.getType();
            Object value;

            value = dependencies.get(type);
            if (ComponentMapper.class.isAssignableFrom(type)) {
                type = (Class) ((ParameterizedType) injectableField.getGenericType()).getActualTypeArguments()[0];
                value = ComponentMapper.getFor((Class<Component>) type);
            }
            if( value!=null ) {
                boolean accessible = injectableField.isAccessible();
                if( !accessible ) {
                    injectableField.setAccessible(true);
                }

                try {
                    injectableField.set(obj, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                if( !accessible ) {
                    injectableField.setAccessible(false);
                }
            }
        }
    }
}
