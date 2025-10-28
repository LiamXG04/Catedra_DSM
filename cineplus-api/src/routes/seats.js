import { Router } from 'express';
import { query } from '../db.js';

const r = Router();

r.get('/', async (req, res) => {
  const { showtimeId } = req.query; // id de funcion
  if (!showtimeId) return res.status(400).json({ error: 'showtimeId required' });

  const funciones = await query('SELECT * FROM funciones WHERE id = ?', [showtimeId]);
  if (funciones.length === 0) return res.status(404).json({ error: 'Showtime not found' });
  const fun = funciones[0];

  const seats = await query('SELECT * FROM asientos WHERE id_sala = ? ORDER BY fila, columna', [fun.id_sala]);
  const now = Date.now();

  const locked = (await query('SELECT id_asiento FROM bloqueos_asientos WHERE id_funcion = ? AND expira_en > ?', [showtimeId, now])).map(x => x.id_asiento);
  const sold = (await query('SELECT id_asiento FROM boletos WHERE id_funcion = ?', [showtimeId])).map(x => x.id_asiento);

  const lockedSet = new Set(locked);
  const soldSet = new Set(sold);

  const result = seats.map(s => ({
    id: s.id, row: s.fila, col: s.columna,
    status: soldSet.has(s.id) ? 'SOLD' : lockedSet.has(s.id) ? 'LOCKED' : 'FREE'
  }));
  res.json(result);
});

export default r;
