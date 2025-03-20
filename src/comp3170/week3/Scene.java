package comp3170.week3;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static comp3170.Math.*;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;

public class Scene {

	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";

	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	private Vector3f[] colours;
	private int colourBuffer;
	
	private Matrix4f modelMatrix = new Matrix4f();
	
	private Vector3f offset = new Vector3f(0.85f, 0.0f, 0.0f);
	private float speed = 9f;
	private float scaleRate = 0.1f;
	private float rotationRate = TAU/6;
	

	private Shader shader;

	public Scene() {

		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// @formatter:off
			//          (0,1)
			//           /|\
			//          / | \
			//         /  |  \
			//        / (0,0) \
			//       /   / \   \
			//      /  /     \  \
			//     / /         \ \		
			//    //             \\
			//(-1,-1)           (1,-1)
			//
	 		
		vertices = new Vector4f[] {
			new Vector4f( 0, 0, 0, 1),
			new Vector4f( 0, 1, 0, 1),
			new Vector4f(-1,-1, 0, 1),
			new Vector4f( 1,-1, 0, 1),
		};
			
			// @formatter:on
		vertexBuffer = GLBuffers.createBuffer(vertices);

		// @formatter:off
		colours = new Vector3f[] {
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,0),	// RED
			new Vector3f(0,0,1),	// BLUE
		};
			// @formatter:on

		colourBuffer = GLBuffers.createBuffer(colours);

		// @formatter:off
		indices = new int[] {  
			0, 1, 2, // left triangle
			0, 1, 3, // right triangle
			};
			// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);
		//modelMatrix = translationMatrix(0.5f, 0.5f, modelMatrix);
		//modelMatrix = rotationMatrix(TAU/8, modelMatrix);
		//modelMatrix = scaleMatrix(0.2f, 0.1f, modelMatrix);

		//Answers to questions 2
		
		//answer to question 2a
		// nothing as the code needs not be changed here.
		//answer to question 2b
		modelMatrix = identityMatrix(modelMatrix);
		modelMatrix = rotationMatrix(-TAU/4, modelMatrix);
		
		//answer to question 2c
		modelMatrix = identityMatrix(modelMatrix);
		modelMatrix = scaleMatrix(0.5f, 0.5f, modelMatrix);
		modelMatrix = translationMatrix(0.5f, -0.5f, modelMatrix);
		//answer to question 2d'
		modelMatrix = identityMatrix(modelMatrix);
		modelMatrix = rotationMatrix(TAU/8, modelMatrix);
		modelMatrix = scaleMatrix(0.5f, 0.5f, modelMatrix);
		modelMatrix = translationMatrix(-0.65f, 0.65f, modelMatrix);
		modelMatrix = identityMatrix(modelMatrix);
		modelMatrix.translate(offset).scale(scaleRate);
		

		
		
	}

	public void draw() {
		
		shader.enable();
		// set the attributes
		shader.setAttribute("a_position", vertexBuffer);
		shader.setAttribute("a_colour", colourBuffer);
		shader.setUniform("u_modelMatrix", modelMatrix);
		// draw using index buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
		
	}

	/**
	 * Set the destination matrix to a translation matrix. Note the destination
	 * matrix must already be allocated.
	 * 
	 * @param tx   Offset in the x direction
	 * @param ty   Offset in the y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f identityMatrix(Matrix4f dest) {
		// clear the matrix to the identity matrix
		dest.identity();
		return dest;

	}
	
	public static Matrix4f translationMatrix(float tx, float ty, Matrix4f dest) {
		System.out.println("Translation in Progress");

		System.out.println(dest);
		//     [ 1 0 0 tx ]
		// T = [ 0 1 0 ty ]
	    //     [ 0 0 0 0  ]
		//     [ 0 0 0 1  ]

		// Perform operations on only the x and y values of the T vec. 
		// Leaves the z value alone, as we are only doing 2D transformations.
		
		dest.m30(tx);
		dest.m31(ty);
		System.out.println(dest);

		return dest;
	}

	/**
	 * Set the destination matrix to a rotation matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param angle Angle of rotation (in radians)
	 * @param dest  Destination matrix to write into
	 * @return
	 */

	public static Matrix4f rotationMatrix(float angle, Matrix4f dest) {
		System.out.println("Rotation in Progress");
		System.out.println(dest);
		// TODO: Your code here
		//		[ cos(angle) -sin(angle) 0 0 ]
		//	T =	[ sin(angle)  cos(angle) 0 0 ]
		//		[ 0			 0			 0 0 ]
		//		[ 0			 0			 0 1 ]
		float magA = (float) Math.sqrt(dest.m00()*dest.m00() + dest.m01()*dest.m01());
		
		System.out.println("MagnitudeA = "+magA+";");
		float magB = (float) Math.sqrt(dest.m10()*dest.m10() + dest.m11()*dest.m11());
		System.out.println("MagnitudeB = "+magB+";");

		float currentAngle;
		
		if (dest.m00()==0) {
			if(dest.m01()==0) {
				currentAngle = 0;
			}else {
				currentAngle=1;				
			}
		}else {
			currentAngle = (float)Math.atan(dest.m01()/dest.m00());
		}
		
		dest.m00((float) Math.cos(angle)*magA);
		dest.m01((float) Math.sin(angle)*magA);
		dest.m10((float) -Math.sin(angle)*magB);
		dest.m11((float) Math.cos(angle)*magB);
		System.out.println(dest);

		return dest;
	}

	/**
	 * Set the destination matrix to a scale matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param sx   Scale factor in x direction
	 * @param sy   Scale factor in y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f scaleMatrix(float sx, float sy, Matrix4f dest) {
		System.out.println("Scaling in Progress");

		System.out.println(dest);

		// TODO: Your code here
		//		[ sx 0  0 0 ]
		//	T =	[ 0  sy 0 0 ]
		//		[ 0	 0  0 0 ]
		//		[ 0  0  0 1 ]
		
		dest.m00(sx*dest.m00());
		dest.m10(sx*dest.m10());
		dest.m01(sy*dest.m01());
		dest.m11(sy*dest.m11());

		
		System.out.println(dest);

		return dest;
	}

	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		float movement = speed*deltaTime;
		float spin = rotationRate*deltaTime;
		float growth = scaleRate*deltaTime;
		modelMatrix.translate(0.0f, movement, 0.0f).rotateZ(spin);
		System.out.println("Scene Updated");
	}

}
