uniform mat4 u_MVPMatrix ;       // An array representing the combined 
uniform float  u_Texmove;
attribute vec4 a_Position;          // Per-vertex position information we will pass in.
attribute vec2 a_TexCoordinate;    // Per-vertex texture coordinate information we will pass in
uniform mat4 u_MVMatrix[20];
attribute float a_MVPMatrixIndex; 
varying vec2 v_TexCoordinate;     // This will be passed into the fragment shader.
varying vec4 v_Boundary;
void main()                  
{        
   int mvpMatrixIndex = int(a_MVPMatrixIndex);   
  v_TexCoordinate.x = a_TexCoordinate.x - u_Texmove;;
 v_TexCoordinate.y= a_TexCoordinate.y;
  gl_Position = u_MVPMatrix *u_MVMatrix[mvpMatrixIndex ]*a_Position;    
   v_Boundary= gl_Position;
}                      