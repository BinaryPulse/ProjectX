uniform sampler2D u_Texture;    // The input texture.
precision mediump float;          // Set the default precision to medium. We don't need as high of a
uniform vec4 u_Color;  
uniform vec4 u_Boundary; 
varying vec2 v_TexCoordinate;  // Interpolated texture coordinate per fragment.
varying vec4 v_Boundary;
void main()     // The entry point for our fragment shader.
{        

if(v_Boundary.y >u_Boundary.z || v_Boundary.y <u_Boundary.w)
{
  discard;
}                 
gl_FragColor = texture2D(u_Texture, v_TexCoordinate)*u_Color;//(1.0-texture2D(u_Texture, v_TexCoordinate).g); // texture is grayscale so take only grayscale value from  
 // it when computing color output (otherwise font is always black)



} 