precision mediump float;          // Set the default precision to medium. We don't need as high of a
varying vec4 v_Color ;
varying vec4 v_Boundary;
uniform vec4 u_Boundary; 
void main()     // The entry point for our fragment shader.
{    
if(v_Boundary.y >u_Boundary.z || v_Boundary.y <u_Boundary.w)
{
  discard;
}                     
gl_FragColor =v_Color;//(1.0-texture2D(u_Texture, v_TexCoordinate).g); // texture is grayscale so take only grayscale value from  
 // it when computing color output (otherwise font is always black)
} 