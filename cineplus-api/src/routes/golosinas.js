import { Router } from "express";
import { query } from "../db.js";

const router = Router();

router.get("/", async (_req, res) => {
  const rows = await query("SELECT * FROM golosinas ORDER BY nombre");
  res.json(rows);
});

router.post("/buy", async (req, res) => {
  const { id_pedido, id_golosina, cantidad } = req.body;
  if (!id_pedido || !id_golosina || !cantidad) {
    return res
      .status(400)
      .json({ error: "id_pedido, id_golosina y cantidad son requeridos" });
  }

  const result = await query(
    `INSERT INTO detalle_pedido (id_pedido, id_golosina, cantidad, precio_unitario)
     SELECT ?, g.id, ?, g.precio
     FROM golosinas g WHERE g.id = ?`,
    [id_pedido, cantidad, id_golosina]
  );

  if (!result.insertId) return res.status(404).json({ error: "Golosina no encontrada" });

  await query(
    `UPDATE pedidos p
       SET total = (
         SELECT IFNULL(SUM(dp.subtotal),0)
         FROM detalle_pedido dp
         WHERE dp.id_pedido = p.id
       )
     WHERE p.id = ?`,
    [id_pedido]
  );

  res.status(201).json({ ok: true, id_detalle: result.insertId });
});

export default router;
