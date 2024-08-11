package mytimeacty.filter;

//Implementation layer to implement Filter 

import jakarta.servlet.Filter; 
import jakarta.servlet.FilterChain; 
import jakarta.servlet.ServletException; 
import jakarta.servlet.ServletRequest; 
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mytimeacty.model.users.UserDTO;
import mytimeacty.service.UserService;
import mytimeacty.service.JWT.JWTService;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;


public class JwtAuthenticationFilter implements Filter { 
	
	@Autowired
    private JWTService jwtService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
    private JwtDecoder jwtDecoder;
	
	@Override
	public void doFilter(ServletRequest servletRequest, 
						ServletResponse servletResponse, 
						FilterChain filterChain) 
		throws IOException, ServletException{ 

		HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getRequestURI();

        if (path.startsWith("/auth")) {
        	filterChain.doFilter(servletRequest, servletResponse);
        	return;
        }
        
        String token = resolveToken(request);
        UserDTO userFromToken = getUserFromToken(token);
        
        
        if (token != null && this.validateToken(token)) {
        	if (path.startsWith("/admin") && !userFromToken.getUserRole().equals("admin")) {
        		HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden : not good roles");
            	return;
            }
        	if (path.startsWith("/chief") && !userFromToken.getUserRole().equals("chief")) {
        		HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden : not good roles");
            	return;
            }
            Authentication authentication = getAuthentication(userFromToken);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }	
        }else {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden : Invalid Token");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
        
	} 
	 
	
	public String resolveToken(HttpServletRequest request) {
	    String header = request.getHeader("Authorization");
	    if (header != null && header.startsWith("Bearer ")) {
	        return header.substring(7);  
	    }
	    return null;
	}
	
	
	public UserDTO getUserFromToken(String token) {
		Jwt jwt = this.getJwtFromToken(token);

		long userId = jwt.getClaim("id");

	    UserDTO user = userService.getUserById((int) userId);
	    return user;
	}
	
	 public Authentication getAuthentication(UserDTO user) {
       String role = user.getUserRole();

       List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.toUpperCase()));

       return new UsernamePasswordAuthenticationToken(user.getNickname(), null, authorities);
	}
	 
	 public boolean validateToken(String token) {
       try {
           jwtDecoder.decode(token);
           return true;
       } catch (JwtException | IllegalArgumentException e) {
           return false;
       }
   }
	
	public Jwt getJwtFromToken(String token) {
       return jwtDecoder.decode(token);  
	}
   
}
