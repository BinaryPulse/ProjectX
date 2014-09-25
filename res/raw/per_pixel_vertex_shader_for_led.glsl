//uniform mat4 u_MVPMatrix ;       // An array representing the combined 
uniform mat4 u_MVPMatrix[12];
attribute vec4 a_Position;          // Per-vertex position information we will pass in.
attribute float a_MVPMatrixIndex; 
void main()                  
{          
   int mvpMatrixIndex = int(a_MVPMatrixIndex); 
  gl_Position = gl_Position = u_MVPMatrix[mvpMatrixIndex] * a_Position;    

}                      