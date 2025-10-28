import { Router } from 'express';
import { query } from '../db.js';

const r = Router();

// Lista de películas
r.get('/', async (_req, res) => {
  try {
    const movies = await query(`
      SELECT
        id,
        titulo         AS title,
        url_poster     AS poster_url,
        sinopsis       AS synopsis,
        generos        AS genres,
        duracion_min   AS duration_min,
        clasificacion  AS rating
      FROM peliculas
      ORDER BY id
    `);
    res.json(movies);
  } catch (err) {
    console.error('GET /api/movies error:', err);
    res.status(500).json({ error: 'Error al obtener películas' });
  }
});

// Detalle de película
r.get('/:id', async (req, res) => {
  try {
    const { id } = req.params;

    const rows = await query(
      `SELECT
         id,
         titulo        AS title,
         url_poster    AS poster_url,
         sinopsis      AS synopsis,
         generos       AS genres,
         duracion_min  AS duration_min,
         clasificacion AS rating
       FROM peliculas
       WHERE id = ?`,
      [id]
    );

    if (!rows.length) {
      return res.status(404).json({ error: 'Película no encontrada' });
    }

    const movie = rows[0];

    // ✅ Evita palabra reservada "character"
    const cast = await query(
      `SELECT
         nombre_actor AS actor,
         personaje    AS role   -- o AS personaje si quieres mantener español
       FROM reparto
       WHERE id_pelicula = ?
       ORDER BY nombre_actor`,
      [id]
    );

    movie.reparto = cast;
    res.json(movie);
  } catch (err) {
    console.error('GET /api/movies/:id error:', err);
    res.status(500).json({ error: 'Error al obtener detalle de la película' });
  }
});

export default r;
