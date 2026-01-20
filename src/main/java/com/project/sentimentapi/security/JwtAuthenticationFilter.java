package com.project.sentimentapi.security;

import com.project.sentimentapi.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Permitir acceso sin token a login y registro
        if (requestPath.contains("/usuario/login") ||
                (requestPath.contains("/usuario") && request.getMethod().equals("POST") && !requestPath.contains("/login"))) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String correo = jwtUtil.extractCorreo(token);
                Integer usuarioId = jwtUtil.extractUsuarioId(token);

                // Validar que el usuario existe
                if (userRepository.findByCorreo(correo).isPresent() &&
                        jwtUtil.validateToken(token, correo)) {

                    // Agregar usuarioId al request para usarlo en los controladores
                    request.setAttribute("usuarioId", usuarioId);
                    request.setAttribute("correo", correo);

                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido o expirado");
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Acceso no autorizado. Token requerido");
    }
}