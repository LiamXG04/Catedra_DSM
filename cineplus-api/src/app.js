import express from 'express';
import morgan from 'morgan';
import cors from 'cors';
import dotenv from 'dotenv';
import { pool } from './db.js';
import authRoutes from './routes/auth.js';
import movieRoutes from './routes/movies.js';
import showtimeRoutes from './routes/showtimes.js';
import seatRoutes from './routes/seats.js';
import lockRoutes from './routes/locks.js';
import orderRoutes from './routes/orders.js';
import golosinasRoutes from "./routes/golosinas.js";

dotenv.config();
const app = express();
app.use(cors());
app.use(express.json());
app.use(morgan('dev'));

app.get('/health', async (req, res) => {
  try {
    await pool.query('SELECT 1');
    res.json({ ok: true, ts: Date.now() });
  } catch (e) {
    res.status(500).json({ ok: false, error: e.message });
  }
});

app.use('/api/auth', authRoutes);
app.use('/api/movies', movieRoutes);
app.use('/api/showtimes', showtimeRoutes);
app.use('/api/seats', seatRoutes);
app.use('/api/locks', lockRoutes);
app.use('/api/orders', orderRoutes);
app.use("/api/golosinas", golosinasRoutes);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`CinePlus API (MySQL) on http://localhost:${PORT}`));
