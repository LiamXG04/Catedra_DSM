import { Router } from 'express';
import bcrypt from 'bcryptjs';
import { signToken } from '../utils.js';
import { query } from '../db.js';

const r = Router();

r.post('/register', async (req, res) => {
  const { name, email, password } = req.body;
  if (!name || !email || !password) return res.status(400).json({ error: 'Missing fields' });
  const pass_hash = bcrypt.hashSync(password, 10);
  try {
    await query('INSERT INTO usuarios(nombre, correo, contrasena_hash) VALUES (?,?,?)', [name, email, pass_hash]);
    const user = (await query('SELECT id, nombre AS name, correo AS email FROM usuarios WHERE correo=?', [email]))[0];
    const token = signToken({ id: user.id, email: user.email });
    res.json({ ...user, token });
  } catch (e) {
    if (e && e.code === 'ER_DUP_ENTRY') return res.status(409).json({ error: 'Email already registered' });
    res.status(500).json({ error: 'Server error' });
  }
});

r.post('/login', async (req, res) => {
  const { email, password } = req.body;
  const rows = await query('SELECT * FROM usuarios WHERE correo=?', [email]);
  if (rows.length === 0) return res.status(401).json({ error: 'Invalid credentials' });
  const u = rows[0];
  const ok = bcrypt.compareSync(password, u.contrasena_hash);
  if (!ok) return res.status(401).json({ error: 'Invalid credentials' });
  const token = signToken({ id: u.id, email: u.correo });
  res.json({ id: u.id, name: u.nombre, email: u.correo, token });
});

export default r;
