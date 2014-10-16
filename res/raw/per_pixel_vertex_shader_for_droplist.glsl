uniform mat4 u_MVPMatrix ;       // An array representing the combined 
attribute vec4 a_Position;          // Per-vertex position information we will pass in.
attribute vec4 a_Color;          // Per-vertex position information we will pass in.
varying vec4 v_Color; 

void main()                  
{   
 
  v_Color = a_Color;       
  gl_Position =u_MVPMatrix*a_Position;     

}                      