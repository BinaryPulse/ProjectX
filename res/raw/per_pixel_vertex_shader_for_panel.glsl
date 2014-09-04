uniform mat4 u_MVPMatrix ;       // An array representing the combined 
attribute vec4 a_Position;          // Per-vertex position information we will pass in.

void main()                  
{          
  gl_Position = u_MVPMatrix * a_Position;    

}                      