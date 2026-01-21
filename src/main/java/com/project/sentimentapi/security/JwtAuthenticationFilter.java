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
        String method = request.getMethod();

        System.out.println("🔍 JWT Filter - Path: " + requestPath + " | Method: " + method);

        // ✅ PERMITIR peticiones OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("✅ OPTIONS request - Permitiendo sin validación JWT");
            filterChain.doFilter(request, response);
            return;
        }

        // Permitir acceso sin token a login y registro
        if (requestPath.contains("/usuario/login") ||
                (requestPath.contains("/usuario") && method.equals("POST") && !requestPath.contains("/login"))) {
            System.out.println("✅ Ruta pública - Sin validación JWT");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("🔑 Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("🎫 Token extraído (primeros 20 chars): " + token.substring(0, Math.min(20, token.length())) + "...");

            try {
                String correo = jwtUtil.extractCorreo(token);
                Integer usuarioId = jwtUtil.extractUsuarioId(token);

                System.out.println("📧 Correo del token: " + correo);
                System.out.println("🆔 Usuario ID del token: " + usuarioId);

                if (userRepository.findByCorreo(correo).isPresent() &&
                        jwtUtil.validateToken(token, correo)) {

                    request.setAttribute("usuarioId", usuarioId);
                    request.setAttribute("correo", correo);

                    System.out.println("✅ Token válido - Usuario autenticado");

                    filterChain.doFilter(request, response);
                    return;
                } else {
                    System.err.println("❌ Token inválido o usuario no encontrado");
                }
            } catch (Exception e) {
                System.err.println("❌ Error al procesar token: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido o expirado");
                return;
            }
        } else {
            System.err.println("❌ No se encontró header Authorization o no empieza con 'Bearer '");
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Acceso no autorizado. Token requerido");
    }
}