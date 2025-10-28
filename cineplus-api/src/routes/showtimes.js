import { Router } from 'express';
import { query } from '../db.js';

const r = Router();

r.get('/', async (req, res) => {
  const { movieId } = req.query;
  const base = `
    SELECT f.id, f.id_pelicula AS movie_id, f.id_sala AS room_id,
           f.fecha_inicio AS start_at,
           p.titulo AS title, s.nombre AS room_name
    FROM funciones f
    JOIN peliculas p ON p.id = f.id_pelicula
    JOIN salas s ON s.id = f.id_sala
  `;
  const rows = movieId
    ? await query(`${base} WHERE f.id_pelicula = ? ORDER BY f.fecha_inicio`, [movieId])
    : await query(`${base} ORDER BY f.fecha_inicio`);
  res.json(rows);
});

export default r;
