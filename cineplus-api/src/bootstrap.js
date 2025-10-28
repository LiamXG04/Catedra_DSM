import fs from 'fs';
import path from 'path';
import { pool } from './db.js';

async function run() {
  const schemaPath = path.join(process.cwd(), 'src', 'schema.sql');
  const sql = fs.readFileSync(schemaPath, 'utf8');
  const conn = await pool.getConnection();
  try {
    await conn.query(sql);
    console.log('Schema applied.');
  } finally {
    conn.release();
  }
  process.exit(0);
}

run().catch(err => {
  console.error(err);
  process.exit(1);
});
