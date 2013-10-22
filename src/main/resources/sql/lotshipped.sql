SELECT  wdd.lot_number  as lot_number,
  DECODE(wdj.organization_id, 113, 'ERF', 114, 'IEP', 115, 'SOF', NULL) AS site,
  to_char(MAX (wnd.confirm_date), 'YYYY-MM-DD')               AS ship_confirm_date
FROM apps.wsh_delivery_details wdd,
  apps.wsh_delivery_assignments wda,
  apps.wsh_new_deliveries wnd,
  apps.hr_organization_units ou,
  apps.ar_customers cst,
  apps.mtl_system_items msi,
  apps.wip_discrete_jobs wdj
WHERE wda.delivery_detail_id = wdd.delivery_detail_id
AND wnd.delivery_id          = wda.delivery_id
AND ou.organization_id       = wnd.organization_id
AND cst.customer_id          = wdd.customer_id
AND msi.inventory_item_id    = wdd.inventory_item_id
AND msi.organization_id      = wdd.organization_id
AND msi.item_type            = 'FG'
-- AND wnd.confirm_date        >= to_date( '20130101' , 'YYYYMMDD')
-- AND wnd.confirm_date        <= to_date( '20130103' , 'YYYYMMDD')
AND wdj.lot_number           = wdd.lot_number
AND wdj.organization_id     IN (113,114,115)
AND NOT EXISTS
  (SELECT 1
	FROM apps.mtl_onhand_quantities moq
	WHERE wdd.lot_number = moq.lot_number
  )
AND wdd.lot_number != '${in.body}'
AND ROWNUM < 50
GROUP BY wdd.lot_number,
  wdj.organization_id
ORDER BY lot_number 

