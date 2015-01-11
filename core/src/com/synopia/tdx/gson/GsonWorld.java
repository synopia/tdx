package com.synopia.tdx.gson;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.synopia.tdx.Injector;
import com.synopia.tdx.TDGame;
import com.synopia.tdx.components.NameComponent;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Ref;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by synopia on 06.01.2015.
 */
public class GsonWorld {
    private Engine engine;
    private GsonBuilder gsonBuilder;
    private Gson gson;

    private Map<String, JsonObject> prefabs = Maps.newHashMap();
    private Map<String, Type> registeredComponents = Maps.newHashMap();

    private Reflections reflections;
    private Injector injector;

    public GsonWorld(Injector injector, Reflections reflections) {
        this.injector = injector;
        this.reflections = reflections;
        this.engine = new Engine();
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Factory.class, (JsonDeserializer<Factory<?>>) (json, typeOfT, context) -> {
            Type type = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
            return new Factory<>(GsonWorld.this, json, type);
        });
        gsonBuilder.registerTypeAdapter(Vector2.class, new TypeAdapter<Vector2>() {
            @Override
            public void write(JsonWriter out, Vector2 value) throws IOException {
                out.beginArray();
                out.value(value.x);
                out.value(value.y);
                out.endArray();
            }

            @Override
            public Vector2 read(JsonReader in) throws IOException {
                in.beginArray();
                float x = (float) in.nextDouble();
                float y = (float) in.nextDouble();
                in.endArray();
                return new Vector2(x, y);
            }
        });
    }

    public void registerGsonAdapter(Type type, Object typeAdapter) {
        gsonBuilder.registerTypeAdapter(type, typeAdapter);
        gson = null;
    }

    public Entity createEntity(String name) {
        Entity entity = new Entity();
        engine.addEntity(entity);
        JsonObject prefab = prefabs.get(name);
        if( prefab==null ) {
            return null;
        }
        for (Map.Entry<String, JsonElement> entry : prefab.entrySet()) {
            Type type = registeredComponents.get(entry.getKey().toLowerCase());
            Component component = createInstance(type, entry.getValue());
            entity.add(component);
        }
        NameComponent nameComponent = new NameComponent();
        nameComponent.internalName = name;
        entity.add(nameComponent);
        return entity;
    }

    public <T> T createInstance(Type type, String name) {
        return createInstance(type, prefabs.get(name));
    }

    protected <T> T createInstance(Type type, JsonElement jsonElement) {
        return getGson().fromJson(jsonElement, type);
    }

    public void registerComponent(String name, Class<? extends Component> type) {
        registeredComponents.put(name.toLowerCase(), type);
    }

    public void load(InputStream jsonFile) {
        prefabs.putAll(new Gson().fromJson(new InputStreamReader(jsonFile), new TypeToken<Map<String, JsonObject>>() {
        }.getType()));
    }

    private Gson getGson() {
        if( gson==null ) {
            gson = gsonBuilder.create();
        }
        return gson;
    }

    public Engine getEngine() {
        return engine;
    }

    public void registerComponents() {
        Set<Class<? extends Component>> componentTypes = reflections.getSubTypesOf(Component.class);
        for (Class<? extends Component> componentType : componentTypes) {
            String name = componentType.getCanonicalName().toLowerCase();
            if(name.endsWith("component")) {
                name = name.substring(0, name.length()-"component".length());
            }
            int index = name.lastIndexOf('.');
            if( index>=0 ) {
                name = name.substring(index+1);
            }
            registerComponent(name, componentType);
        }
    }

    public void registerSystems() {
        List<EntitySystem> systems = Lists.newArrayList();
        Set<Class<? extends EntitySystem>> types = reflections.getSubTypesOf(EntitySystem.class);
        for (Class<? extends EntitySystem> system : types) {
            int modifiers = system.getModifiers();
            if(!Modifier.isAbstract(modifiers)) {
                try {
                    EntitySystem entitySystem = system.newInstance();
                    injector.add(system, entitySystem);
                    systems.add(entitySystem);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        for (EntitySystem system : systems) {
            injector.inject(system);
            engine.addSystem(system);
        }
    }

}
