// src/seed.js
import { query } from './db.js';
import bcrypt from 'bcryptjs';

async function ensureUsuarioDemo() {
  const email = 'demo@cineplus.com';
  const u = await query('SELECT id FROM usuarios WHERE correo=?', [email]);
  if (u.length === 0) {
    await query(
      'INSERT INTO usuarios(nombre, correo, contrasena_hash) VALUES (?,?,?)',
      ['Demo', email, bcrypt.hashSync('demo123', 10)]
    );
    console.log('Usuario demo creado: demo@cineplus.com / demo123');
  }
}

async function ensureSalasYAsientos() {
  const salas = await query('SELECT * FROM salas');
  if (salas.length === 0) {
    await query('INSERT INTO salas(nombre, capacidad) VALUES (?,?), (?,?)', ['Sala 1', 36, 'Sala 2', 36]);
    const nuevas = await query('SELECT * FROM salas');
    for (const s of nuevas) {
      for (let f = 1; f <= 6; f++) {
        for (let c = 1; c <= 6; c++) {
          await query('INSERT INTO asientos(id_sala, fila, columna) VALUES (?,?,?)', [s.id, f, c]);
        }
      }
    }
  }
}

async function ensurePeliculasYFunciones() {
  const pelis = await query('SELECT * FROM peliculas');
  if (pelis.length === 0) {
    await query('INSERT INTO peliculas(titulo, url_poster, duracion_min, clasificacion) VALUES (?,?,?,?)',
      ['El Caballero del Código', null, 120, 'PG-13']);
    await query('INSERT INTO peliculas(titulo, url_poster, duracion_min, clasificacion) VALUES (?,?,?,?)',
      ['La Sombra del Servidor', null, 110, 'PG']);
  }
  const salas = await query('SELECT * FROM salas');
  const cnt = (await query('SELECT COUNT(*) AS c FROM funciones'))[0].c;
  if (cnt === 0) {
    const p1 = (await query('SELECT id FROM peliculas WHERE titulo=?', ['El Caballero del Código']))[0].id;
    const p2 = (await query('SELECT id FROM peliculas WHERE titulo=?', ['La Sombra del Servidor']))[0].id;
    const s1 = salas[0].id, s2 = salas[1].id;
    const now = Date.now(), twoH = 2 * 60 * 60 * 1000;
    await query(
      'INSERT INTO funciones(id_pelicula, id_sala, fecha_inicio) VALUES (?,?,?), (?,?,?), (?,?,?)',
      [p1, s1, now + twoH, p1, s2, now + 4 * twoH, p2, s1, now + 3 * twoH]
    );
  }
}

async function ensureGolosinas() {
  const g = await query('SELECT COUNT(*) AS c FROM golosinas');
  if (g[0].c === 0) {
    await query(
      'INSERT INTO golosinas(nombre, precio, stock) VALUES (?,?,?), (?,?,?), (?,?,?), (?,?,?)',
      ['Palomitas grandes', 4.50, 30, 'Nachos con queso', 3.75, 25, 'Refresco 500ml', 2.25, 40, 'Chocolate', 1.80, 50]
    );
  }
}

async function ensurePeliculasEjemplo() {
  const peliculas = [
    {
      titulo: 'Inception',
      duracion_min: 148,
      clasificacion: 'PG-13',
      sinopsis: 'Dom Cobb, un extractor experto, lidera un equipo que se infiltra en los sueños para implantar una idea. A medida que las capas de sueños se suceden, la realidad y la culpa se confunden.',
      generos: 'Acción, Ciencia ficción, Aventura',
      url_poster: 'https://image.tmdb.org/t/p/w500/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg'
    },
    {
      titulo: 'The Dark Knight',
      duracion_min: 152,
      clasificacion: 'PG-13',
      sinopsis: 'Batman debe enfrentarse a su némesis, el Joker, que siembra el caos en Gotham obligando a Bruce Wayne a cuestionar sus límites y la delgada línea entre orden y anarquía.',
      generos: 'Acción, Crimen, Drama',
      url_poster: 'https://image.tmdb.org/t/p/w500/kqjL17yufvn9OVLyXYpvtyrFfak.jpg'
    }
  ];

  for (const p of peliculas) {
    const existing = await query('SELECT id FROM peliculas WHERE titulo = ?', [p.titulo]);
    let pid;
    if (existing.length === 0) {
      const r = await query(
        'INSERT INTO peliculas(titulo, url_poster, duracion_min, clasificacion, sinopsis, generos) VALUES (?,?,?,?,?,?)',
        [p.titulo, p.url_poster, p.duracion_min, p.clasificacion, p.sinopsis, p.generos]
      );
      pid = r.insertId;
    } else {
      pid = existing[0].id;
      // Actualizar sinopsis/otros campos por si cambian
      await query(
        'UPDATE peliculas SET url_poster=?, duracion_min=?, clasificacion=?, sinopsis=?, generos=? WHERE id=?',
        [p.url_poster, p.duracion_min, p.clasificacion, p.sinopsis, p.generos, pid]
      );
    }

    // Asegurar reparto idempotente
    if (p.titulo === 'Inception') {
      const actores = [
        { nombre: 'Leonardo DiCaprio', personaje: 'Cobb' },
        { nombre: 'Joseph Gordon-Levitt', personaje: 'Arthur' }
      ];
      for (const a of actores) {
        const ex = await query('SELECT id FROM reparto WHERE id_pelicula=? AND nombre_actor=?', [pid, a.nombre]);
        if (ex.length === 0) {
          await query('INSERT INTO reparto(id_pelicula, nombre_actor, personaje) VALUES (?,?,?)', [pid, a.nombre, a.personaje]);
        }
      }
    }

    if (p.titulo === 'The Dark Knight') {
      const actores = [
        { nombre: 'Christian Bale', personaje: 'Bruce Wayne / Batman' }
      ];
      for (const a of actores) {
        const ex = await query('SELECT id FROM reparto WHERE id_pelicula=? AND nombre_actor=?', [pid, a.nombre]);
        if (ex.length === 0) {
          await query('INSERT INTO reparto(id_pelicula, nombre_actor, personaje) VALUES (?,?,?)', [pid, a.nombre, a.personaje]);
        }
      }
    }
  }
}

async function run() {
  await ensureUsuarioDemo();
  await ensureSalasYAsientos();
  await ensurePeliculasYFunciones();
  await ensurePeliculasEjemplo();
  await ensureGolosinas();
  console.log('Seed listo (es).');
  process.exit(0);
}

run().catch(e => { console.error(e); process.exit(1); });
