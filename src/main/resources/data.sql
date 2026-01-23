INSERT INTO categoria (nombre_categoria, descripcion, usuario_id)
SELECT 'Tecnología', 'Productos electrónicos y gadgets', usuario_id
FROM usuarios
WHERE NOT EXISTS (
    SELECT 1 FROM categoria WHERE nombre_categoria = 'Tecnología' AND usuario_id = usuarios.usuario_id
);
