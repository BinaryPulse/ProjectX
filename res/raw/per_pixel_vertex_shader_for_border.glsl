uniform mat4 u_MVPMatrix ;       // An array representing the combined 
uniform float  u_Texmove;
attribute vec4 a_Position;          // Per-vertex position information we will pass in.
attribute vec2 a_TexCoordinate;    // Per-vertex texture coordinate information we will pass in
varying vec2 v_TexCoordinate;     // This will be passed into the fragment shader.
void main()                  
{          
  v_TexCoordinate.x = a_TexCoordinate.x - u_Texmove;;
 v_TexCoordinate.y= a_TexCoordinate.y;
  gl_Position = u_MVPMatrix * a_Position;    

}                      