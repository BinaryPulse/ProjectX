precision mediump float;          // Set the default precision to medium. We don't need as high of a
uniform vec4 u_Color;  
void main()     // The entry point for our fragment shader.
{                         
gl_FragColor =  u_Color; // texture is grayscale so take only grayscale value from  
 // it when computing color output (otherwise font is always black)
} 