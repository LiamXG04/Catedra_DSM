import { Router } from "express";
import { authMiddleware } from "../utils.js";
import { query } from "../db.js";
import { v4 as uuidv4 } from "uuid";

const r = Router();

r.post("/", authMiddleware, async (req, res) => {
  const { showtimeId, seatIds, detalle } = req.body; 
  if (!showtimeId || !Array.isArray(seatIds) || seatIds.length === 0) {
    return res
      .status(400)
      .json({ error: "showtimeId y seatIds[] son requeridos" });
  }

  const now = Date.now();

  const soldRows = await query(
    `SELECT id_asiento FROM boletos WHERE id_funcion = ? AND id_asiento IN (${seatIds
      .map(() => "?")
      .join(",")})`,
    [showtimeId, ...seatIds]
  );
  if (soldRows.length) {
    return res.status(409).json({
      error: "Algunos asientos ya fueron vendidos",
      soldSeats: soldRows.map((r) => r.id_asiento),
    });
  }

  const lockRows = await query(
    `SELECT id_asiento, id_usuario FROM bloqueos_asientos 
     WHERE id_funcion = ? AND id_asiento IN (${seatIds
       .map(() => "?")
       .join(",")}) AND expira_en > ?`,
    [showtimeId, ...seatIds, now]
  );
  const lockedByOther = lockRows
    .filter((l) => l.id_usuario !== req.user.id)
    .map((l) => l.id_asiento);
  if (lockedByOther.length) {
    return res.status(409).json({
      error: "Algunos asientos están bloqueados por otros usuarios",
      lockedByOther,
    });
  }

  const orderRes = await query(
    "INSERT INTO pedidos(id_usuario, total, estado, creado_en) VALUES (?,?,?,?)",
    [req.user.id, 0, "PAGADO", now]
  );
  const orderId = orderRes.insertId;

  const seatPrice = 5.0;
  for (const seatId of seatIds) {
    await query(
      "INSERT INTO boletos(id_pedido, id_funcion, id_asiento, codigo_qr, emitido_en) VALUES (?,?,?,?,?)",
      [orderId, showtimeId, seatId, uuidv4(), now]
    );
    await query(
      "DELETE FROM bloqueos_asientos WHERE id_funcion = ? AND id_asiento = ?",
      [showtimeId, seatId]
    );
  }

  if (Array.isArray(detalle) && detalle.length) {
    for (const item of detalle) {
      await query(
        `INSERT INTO detalle_pedido (id_pedido, id_golosina, cantidad, precio_unitario)
         SELECT ?, g.id, ?, g.precio FROM golosinas g WHERE g.id = ?`,
        [orderId, item.cantidad, item.id_golosina]
      );
    }
  }

  const totGolosinas = await query(
    "SELECT IFNULL(SUM(subtotal),0) AS total FROM detalle_pedido WHERE id_pedido = ?",
    [orderId]
  );
  const total = seatIds.length * seatPrice + Number(totGolosinas[0].total || 0);
  await query("UPDATE pedidos SET total=? WHERE id=?", [total, orderId]);

  const tickets = await query("SELECT * FROM boletos WHERE id_pedido = ?", [
    orderId,
  ]);
  res.json({ orderId, total, tickets });
});

r.get("/mine", authMiddleware, async (req, res) => {
  const orders = await query(
    "SELECT * FROM pedidos WHERE id_usuario = ? ORDER BY creado_en DESC",
    [req.user.id]
  );
  const tickets = await query(
    "SELECT * FROM boletos WHERE id_pedido IN (SELECT id FROM pedidos WHERE id_usuario = ?)",
    [req.user.id]
  );
  const detalle = await query(
    "SELECT * FROM detalle_pedido WHERE id_pedido IN (SELECT id FROM pedidos WHERE id_usuario = ?)",
    [req.user.id]
  );
  res.json({ orders, tickets, detalle });
});

r.get("/:id", authMiddleware, async (req, res) => {
  const { id } = req.params;
  const pedido = await query("SELECT * FROM pedidos WHERE id = ?", [id]);
  if (!pedido.length)
    return res.status(404).json({ error: "Pedido no encontrado" });

  const boletos = await query("SELECT * FROM boletos WHERE id_pedido = ?", [
    id,
  ]);
  const detalle = await query("SELECT * FROM detalle_pedido WHERE id_pedido = ?", [
    id,
  ]);
  res.json({ pedido: pedido[0], boletos, detalle });
});

r.delete("/:id", async (req, res) => {
  const { id } = req.params;
  await query("DELETE FROM pedidos WHERE id = ?", [id]);
  res.json({ ok: true, mensaje: `Pedido ${id} eliminado (solo DEV)` });
});

export default r;
