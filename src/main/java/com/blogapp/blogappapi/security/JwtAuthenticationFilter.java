package com.blogapp.blogappapi.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private UserDetailsService userDetailService;
	
	@Autowired
	private JwtTokenHelper jwtTokenhelper;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		//get token
		
		String requestToken = request.getHeader("Authorization");
		
		//Bearer ToeknValue
		String username = null;
		
		String token = null;
		
		if(requestToken!=null && requestToken.startsWith("Bearer")) {
			
			token = requestToken.substring(7);
			try {
				username = this.jwtTokenhelper.getUsernameFromToken(token);
			}catch(IllegalArgumentException e){
				System.out.println("Unable toget JWT Token");
			}catch(ExpiredJwtException e) {
				System.out.println("JWT Token is expired");
			}catch(MalformedJwtException e) {
				System.out.println("Invalid JWt");
			}
			
			
		}else {
			System.out.println("Invalid Token Value");
		}
		
		//Once we get the token now validate
		
		if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
			
			UserDetails userDetails = this.userDetailService.loadUserByUsername(username);
			
			if(this.jwtTokenhelper.validateToken(token, userDetails)) {
				//true
				
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				
			}else {
				System.out.println("Invalid JWt Token");
			}
			
			
		}else {
			System.out.println("Username is null or Context is not null");
		}
		
		filterChain.doFilter(request, response);
		
	}

}
