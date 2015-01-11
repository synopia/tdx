package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.synopia.tdx.components.TextureComponent;
import com.synopia.tdx.components.TransformComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Comparator;

/**
 * Created by synopia on 06.01.2015.
 */
public class RenderingSystem extends IteratingSystem {
    public static final float FRUSTUM_WIDTH = 50;
    public static final float FRUSTUM_HEIGHT = 50;
    public static final float PIXELS_TO_METERS = 1.f/46.f;

    @Inject
    private SpriteBatch batch;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator;
    private OrthographicCamera camera;

    @Inject
    private ComponentMapper<TextureComponent> textureM;
    @Inject
    private ComponentMapper<TransformComponent> transformM;
    private Logger logger = LoggerFactory.getLogger(RenderingSystem.class);
    private Matrix4 matrix;

    public RenderingSystem() {
        super(Family.getFor(TransformComponent.class, TextureComponent.class),-1);
        renderQueue = new Array<Entity>();
        comparator = new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return (int) Math.signum(transformM.get(o2).pos.z- transformM.get(o1).pos.z);
            }
        };

        camera = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        camera.position.set(FRUSTUM_WIDTH/2-0.5f, FRUSTUM_HEIGHT/2-0.5f, 0);
        matrix = new Matrix4();
        logger.info("RenderingSystem started");
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        renderQueue.sort(comparator);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.setTransformMatrix(matrix);
        batch.begin();

        for (Entity entity : renderQueue) {
            TextureComponent tex = textureM.get(entity);
            if( tex.region==null ) {
                continue;
            }
            TransformComponent t = transformM.get(entity);
            float width = tex.region.getRegionWidth();
            float height = tex.region.getRegionHeight();
            float originX = width*0.5f;
            float originY = height*0.5f;

            batch.draw(tex.region, t.pos.x-originX, t.pos.y-originY, originX, originY, width, height, t.scale.x*PIXELS_TO_METERS, t.scale.y*PIXELS_TO_METERS, MathUtils.radiansToDegrees*t.rotation);
        }

        batch.end();
        renderQueue.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
