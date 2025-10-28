import mysql from 'mysql2/promise';
import dotenv from 'dotenv';
dotenv.config();

export const pool = mysql.createPool({
  host: process.env.DB_HOST || '127.0.0.1',
  port: Number(process.env.DB_PORT || 3307),
  user: process.env.DB_USER || 'cineplus',
  password: process.env.DB_PASSWORD || 'cineplus',
  database: process.env.DB_NAME || 'cineplusdb',
  connectionLimit: 10,
  multipleStatements: true
});

export async function query(sql, params = []) {
  const [rows] = await pool.query(sql, params);
  return rows;
}
