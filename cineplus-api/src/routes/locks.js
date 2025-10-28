import { Router } from 'express';
import { authMiddleware } from '../utils.js';
import { query } from '../db.js';

const r = Router();

r.post('/', authMiddleware, async (req, res) => {
  const { showtimeId, seatId } = req.body; 
  if (!showtimeId || !seatId) return res.status(400).json({ error: 'showtimeId and seatId required' });
  const now = Date.now();

  const sold = await query('SELECT 1 FROM boletos WHERE id_funcion = ? AND id_asiento = ? LIMIT 1', [showtimeId, seatId]);
  if (sold.length) return res.status(409).json({ error: 'Seat already sold' });

  const active = await query('SELECT 1 FROM bloqueos_asientos WHERE id_funcion = ? AND id_asiento = ? AND expira_en > ? LIMIT 1', [showtimeId, seatId, now]);
  if (active.length) return res.status(409).json({ error: 'Seat already locked' });

  const ttl = Number(process.env.LOCK_TTL_SECONDS || 180) * 1000;
  const expires = now + ttl;
  const result = await query(
    'INSERT INTO bloqueos_asientos(id_funcion, id_asiento, id_usuario, bloqueado_en, expira_en) VALUES (?,?,?,?,?)',
    [showtimeId, seatId, req.user.id, now, expires]
  );
  res.json({ id: result.insertId, showtimeId, seatId, expiresAt: expires });
});

r.delete('/:id', authMiddleware, async (req, res) => {
  const id = req.params.id;
  const rows = await query('SELECT * FROM bloqueos_asientos WHERE id=?', [id]);
  if (rows.length === 0) return res.status(404).json({ error: 'Not found' });
  const lock = rows[0];
  if (lock.id_usuario !== req.user.id) return res.status(403).json({ error: 'Not your lock' });
  await query('DELETE FROM bloqueos_asientos WHERE id=?', [id]);
  res.json({ ok: true });
});

export default r;
