uniform mat4 u_MVPMatrix ;       // An array representing the combined 
uniform vec4 u_Color;         // An array representing the combined 
attribute vec4 a_Position;          // Per-vertex position information we will pass in.
attribute vec4 a_Color;          // Per-vertex position information we will pass in.
varying vec4 v_Color; 
uniform mat4 u_MVMatrix[20];
attribute float a_MVPMatrixIndex; 
varying vec4 v_Boundary;
void main()                  
{   
   int mvpMatrixIndex = int(a_MVPMatrixIndex);   
 v_Color = a_Color +u_Color;       
  gl_Position =u_MVPMatrix *u_MVMatrix[mvpMatrixIndex ]*a_Position;     
   v_Boundary= gl_Position;
}                      