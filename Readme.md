BookKeeper
==========

API:

`${endpoint}/transactions?client_id=XXXXX` : Transacciones registradas para un cliente en particular.

`${endpoint}/payments?client_id=XXXXX&status=paid` : Pagos procesados para un cliente.

`${endpoint}/payments?client_id=XXXXX&status=pending` : Pagos pendientes para un cliente.

`${endpoint}/file-import` : Forzar la importación del archivo.

Posibles mejoras:

- Podria calcular un hash por cada file procesado y guardarlo en la base de datos, y asi podes descartar un file duplicado antes de procesarlo, siempre y cuando este proceso sea 'costoso'.
- Si falla de alguna forma el procesamiento, podria guardar el archivo fallado en la base de datos, o en un file system remoto, NFS, hdfs, etc. Para su correccion manual y reprocesamiento.
- Se podria validar que los montos totales que figuran en el header del pago, se correspondan con las transacciones y descuentos que tiene asignados.

Decisiones de implementacion:

- No se cacheo el servicio de clientes, es solo un by-pass del servicio actual.
- Se procesara el archivo cada 5 minutos, al guardar los pagos en la base de datos previamente se chequea por id que no existan. Si se procesara cada 10 minutos, cualquier delay podria hacer que se pierda algun update.

Setup
=====

Correr el script `run.sh` en la raiz del proyecto, esto va a levantar un docker-compose con dos containers, un mariadb para la base de datos y otro con la aplicacion.

Este script va a levantar la app en el endpoint `http://localhost:8080`

Nota: Como el build tarda bastante, la imagen de la aplicacion fue publicada en docker hub. 