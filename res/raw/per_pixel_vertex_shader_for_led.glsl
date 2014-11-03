//uniform mat4 u_MVPMatrix ;       // An array representing the combined 
uniform mat4 u_MVPMatrix[12];
attribute vec4 a_Position;          // Per-vertex position information we will pass in.
varying vec4 v_Color; 
attribute float a_MVPMatrixIndex; 
void main()                  
{          
   int mvpMatrixIndex = int(a_MVPMatrixIndex); 
if(a_Position.z ==0.0){
    v_Color =vec4(1.0,0.0,0.0,1.0);
}
else
{
    v_Color =vec4(0.0,1.0,0.0,1.0);
}
  gl_Position = gl_Position = u_MVPMatrix[mvpMatrixIndex] * a_Position;    


}                      