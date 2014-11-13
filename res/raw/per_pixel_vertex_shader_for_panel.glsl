uniform mat4 u_MVPMatrix ;       // An array representing the combined 
attribute vec4 a_Position;          // Per-vertex position information we will pass in.
varying vec4 v_Boundary;
void main()                  
{          
  gl_Position = u_MVPMatrix * a_Position;    
   v_Boundary= gl_Position;
}                      