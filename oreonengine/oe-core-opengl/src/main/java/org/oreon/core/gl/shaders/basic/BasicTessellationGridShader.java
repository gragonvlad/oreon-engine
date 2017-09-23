package org.oreon.core.gl.shaders.basic;

import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.model.Material;
import org.oreon.core.scene.GameObject;
import org.oreon.core.shaders.Shader;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.ResourceLoader;

public class BasicTessellationGridShader extends Shader{
	
private static BasicTessellationGridShader instance = null;
	
	public static BasicTessellationGridShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BasicTessellationGridShader();
	    }
	      return instance;
	}
	
	protected BasicTessellationGridShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/basic/tessellation grid/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("shaders/basic/tessellation grid/Tessellation Control.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("shaders/basic/tessellation grid/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("shaders/basic/tessellation grid/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/basic/tessellation grid/Fragment.glsl"));
		compileShader();
		
		
		addUniform("viewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("eyePosition");
		
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		
		
		addUniform("material.displacemap");
		addUniform("material.displaceScale");
		addUniform("displacement");
		
		addUniform("clipplane");
		
		for (int i=0; i<6; i++)
		{
			addUniform("frustumPlanes[" + i +"]");
		}
	}
	
	public void updateUniforms(GameObject object)
	{
		
		setUniform("viewProjectionMatrix", CoreSystem.getInstance().getScenegraph().getCamera().getViewProjectionMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());
		setUniform("eyePosition", CoreSystem.getInstance().getScenegraph().getCamera().getPosition());
		
		setUniform("clipplane", CoreSystem.getInstance().getRenderingEngine().getClipplane());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", CoreSystem.getInstance().getScenegraph().getCamera().getFrustumPlanes()[i]);
		}
		
		Material material = (Material) object.getComponents().get("Material");
		
		setUniformi("tessFactor", 10000);
		setUniformf("tessSlope", 1.4f);
		setUniformf("tessShift", 1.0f);
			
		if (material.getDisplacemap() != null){
			setUniformi("displacement", 1);
			glActiveTexture(GL_TEXTURE3);
			material.getDisplacemap().bind();
			setUniformi("material.displacemap", 3);
			setUniformf("material.displaceScale", material.getDisplaceScale());
		}
		else
			setUniformi("displacement", 0);
	}
}