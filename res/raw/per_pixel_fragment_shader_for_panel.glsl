precision mediump float;          // Set the default precision to medium. We don't need as high of a
uniform vec4 u_Color;  
void main()     // The entry point for our fragment shader.
{                         
gl_FragColor =u_Color;//(1.0-texture2D(u_Texture, v_TexCoordinate).g); // texture is grayscale so take only grayscale value from  
 // it when computing color output (otherwise font is always black)
} 