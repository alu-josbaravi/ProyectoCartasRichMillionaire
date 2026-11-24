/* tienda.js - Conecta index.html con el backend Spring Boot (CardTradeDB).
 *
 * Se carga DESPUES del script principal del buscador. No modifica su logica:
 * solo "engancha" abrirDetalle() y mostrarCartas() para anadir la parte de tienda
 * (precios, carrito, favoritos, login, pedidos).
 *
 * Todo va dentro de una funcion para no chocar con los nombres globales del buscador.
 */
(function () {
    "use strict";

    // Abierto con doble clic (file://) no hay servidor propio: se usa el backend en localhost:8080
    const API = location.protocol === "file:" ? "http://localhost:8080/api" : "/api";
    const API_CARTAS = "https://api.pokemontcg.io/v2/cards";
    const CLAVE_SESION = "cardtrade_usuario";

    const euros = new Intl.NumberFormat("es-ES", { style: "currency", currency: "EUR" });

    const ETIQ_ESTADO = {
        NUEVA: "Nueva", EXCELENTE: "Excelente", BUENA: "Buena",
        ACEPTABLE: "Aceptable", DANADA: "Dañada"
    };
    const ETIQ_ACABADO = { NORMAL: "Normal", HOLO: "Holo", REVERSE_HOLO: "Reverse holo" };
    const IDIOMAS = ["es", "en", "fr", "de", "it", "pt", "ja"];

    /* ---------------- Estado ---------------- */
    let usuario = leerSesion();
    let productosPorCarta = new Map();   // carta_externa_id -> [productos a la venta]
    const infoCartas = new Map();        // carta_externa_id -> {name, set, number, image}
    let favoritos = new Set();
    let carrito = null;
    let cartaAbierta = null;

    /* ---------------- Utilidades ---------------- */
    const $ = (id) => document.getElementById(id);

    function el(etiqueta, props = {}, ...hijos) {
        const nodo = document.createElement(etiqueta);
        for (const [clave, valor] of Object.entries(props)) {
            if (clave === "class") nodo.className = valor;
            else if (clave === "text") nodo.textContent = valor;
            else if (clave.startsWith("on")) nodo.addEventListener(clave.slice(2), valor);
            else if (valor !== false && valor != null) nodo.setAttribute(clave, valor === true ? "" : valor);
        }
        hijos.flat().forEach((h) => { if (h != null && h !== false) nodo.append(h); });
        return nodo;
    }

    function leerSesion() {
        try { return JSON.parse(sessionStorage.getItem(CLAVE_SESION) || "null"); }
        catch (_) { return null; }
    }

    function guardarSesion() {
        try {
            if (usuario) sessionStorage.setItem(CLAVE_SESION, JSON.stringify(usuario));
            else sessionStorage.removeItem(CLAVE_SESION);
        } catch (_) { /* almacenamiento no disponible */ }
    }

    let temporizadorAviso = null;
    function avisar(texto, tipo = "") {
        const aviso = $("aviso");
        aviso.textContent = texto;
        aviso.className = "visible " + tipo;
        clearTimeout(temporizadorAviso);
        temporizadorAviso = setTimeout(() => { aviso.className = ""; }, 3500);
    }

    /* Llamada al backend. Devuelve "datos" o lanza Error con el mensaje del servidor. */
    async function api(ruta, opciones = {}) {
        let respuesta;
        try {
            respuesta = await fetch(API + ruta, {
                headers: { "Content-Type": "application/json" },
                ...opciones
            });
        } catch (_) {
            throw new Error("No se puede conectar con el servidor. Comprueba que el backend está arrancado.");
        }

        let cuerpo = null;
        try { cuerpo = await respuesta.json(); } catch (_) { /* sin JSON */ }

        if (!respuesta.ok || (cuerpo && cuerpo.correcto === false)) {
            throw new Error((cuerpo && cuerpo.mensaje) || `Error HTTP ${respuesta.status}`);
        }
        return cuerpo ? cuerpo.datos : null;
    }

    const post = (ruta, datos) => api(ruta, { method: "POST", body: JSON.stringify(datos) });
    const put = (ruta, datos) => api(ruta, { method: "PUT", body: JSON.stringify(datos) });

    /* ---------------- Modales ---------------- */
    function abrir(modal) {
        modal.classList.add("visible");
        modal.setAttribute("aria-hidden", "false");
        document.body.classList.add("sin-scroll");
    }

    function cerrar(modal) {
        modal.classList.remove("visible");
        modal.setAttribute("aria-hidden", "true");
        if (!document.querySelector(".modal.visible")) document.body.classList.remove("sin-scroll");
    }

    const modalAuth = $("modalAuth");
    const modalCarrito = $("modalCarrito");
    const modalPedidos = $("modalPedidos");

    [modalAuth, modalCarrito, modalPedidos].forEach((m) => {
        m.addEventListener("click", (ev) => { if (ev.target === m) cerrar(m); });
        m.querySelectorAll("[data-cerrar]").forEach((b) => b.addEventListener("click", () => cerrar(m)));
    });

    document.addEventListener("keydown", (ev) => {
        if (ev.key !== "Escape") return;
        [modalAuth, modalCarrito, modalPedidos].forEach((m) => {
            if (m.classList.contains("visible")) cerrar(m);
        });
    });

    /* ---------------- Datos de la API externa ---------------- */
    async function obtenerInfoCarta(id) {
        if (infoCartas.has(id)) return infoCartas.get(id);
        try {
            const r = await fetch(`${API_CARTAS}/${encodeURIComponent(id)}`, { headers: { Accept: "application/json" } });
            if (!r.ok) return null;
            const c = (await r.json()).data;
            const info = { name: c.name, set: c.set?.name, number: c.number, image: c.images?.small };
            infoCartas.set(id, info);
            return info;
        } catch (_) {
            return null;
        }
    }

    /* ---------------- Productos de la tienda ---------------- */
    async function cargarProductos() {
        try {
            const lista = await api("/productos/disponibles");
            const mapa = new Map();
            for (const p of lista) {
                if (!mapa.has(p.carta_externa_id)) mapa.set(p.carta_externa_id, []);
                mapa.get(p.carta_externa_id).push(p);
            }
            productosPorCarta = mapa;
        } catch (e) {
            console.warn("No se pudieron cargar los productos:", e.message);
        }
    }

    /* Precio "Desde X €" en las tarjetas del buscador */
    function decorarCartas() {
        cartasActuales.forEach((c) => {
            infoCartas.set(c.id, { name: c.name, set: c.set?.name, number: c.number, image: c.images?.small });
        });

        document.querySelectorAll("#resultado .carta").forEach((tarjeta, i) => {
            const carta = cartasActuales[i];
            if (!carta) return;
            tarjeta.querySelector(".precio-tienda")?.remove();
            const ofertas = productosPorCarta.get(carta.id);
            if (ofertas && ofertas.length) {
                const minimo = Math.min(...ofertas.map((o) => Number(o.precio)));
                tarjeta.append(el("span", { class: "precio-tienda", text: `Desde ${euros.format(minimo)}` }));
            }
        });
    }

    /* ---------------- Barra de usuario ---------------- */
    function renderBarra() {
        const zona = $("zonaSesion");
        zona.replaceChildren();

        if (usuario) {
            const admin = usuario.rol === "ADMINISTRADOR" ? " (admin)" : "";
            zona.append(
                el("span", { class: "saludo", text: `Hola, ${usuario.usuario}${admin}` }),
                el("button", { type: "button", class: "boton-secundario", text: "Favoritos", onclick: verFavoritos }),
                el("button", { type: "button", class: "boton-secundario", text: "Mis pedidos", onclick: abrirPedidos }),
                el("button", { type: "button", class: "boton-secundario", text: "Salir", onclick: cerrarSesion })
            );
        } else {
            zona.append(el("button", { type: "button", text: "Entrar / Registrarse", onclick: () => abrirAuth("login") }));
        }

        $("contadorCarrito").textContent = carrito ? carrito.unidades : 0;
    }

    /* ---------------- Sesión ---------------- */
    function abrirAuth(pestana) {
        mostrarPestana(pestana);
        $("errorAuth").textContent = "";
        abrir(modalAuth);
        (pestana === "login" ? $("loginUsuario") : $("regUsuario")).focus();
    }

    function mostrarPestana(pestana) {
        const login = pestana === "login";
        $("formLogin").hidden = !login;
        $("formRegistro").hidden = login;
        $("pestanaLogin").classList.toggle("activa", login);
        $("pestanaRegistro").classList.toggle("activa", !login);
    }

    async function iniciarSesion(datosUsuario) {
        usuario = datosUsuario;
        guardarSesion();
        renderBarra();
        await cargarDatosUsuario();
        renderZonaTienda();
    }

    async function cargarDatosUsuario() {
        if (!usuario) return;
        const [fav, car] = await Promise.allSettled([
            api(`/favoritos/${usuario.id}`),
            api(`/carrito/${usuario.id}`)
        ]);
        favoritos = new Set(fav.status === "fulfilled" ? fav.value : []);
        carrito = car.status === "fulfilled" ? car.value : null;
        if (car.status === "rejected") console.warn("Carrito:", car.reason.message);
        renderBarra();
    }

    function cerrarSesion() {
        usuario = null;
        carrito = null;
        favoritos = new Set();
        guardarSesion();
        renderBarra();
        renderZonaTienda();
        avisar("Sesión cerrada");
    }

    $("pestanaLogin").addEventListener("click", () => mostrarPestana("login"));
    $("pestanaRegistro").addEventListener("click", () => mostrarPestana("registro"));

    $("formLogin").addEventListener("submit", async (ev) => {
        ev.preventDefault();
        $("errorAuth").textContent = "";
        try {
            const datos = await post("/auth/login", {
                usuario: $("loginUsuario").value.trim(),
                password: $("loginPassword").value
            });
            ev.target.reset();
            cerrar(modalAuth);
            await iniciarSesion(datos);
            avisar(`Bienvenido, ${datos.usuario}`, "ok");
        } catch (e) {
            $("errorAuth").textContent = e.message;
        }
    });

    $("formRegistro").addEventListener("submit", async (ev) => {
        ev.preventDefault();
        $("errorAuth").textContent = "";
        const nombre = $("regUsuario").value.trim();
        const password = $("regPassword").value;
        try {
            const cuenta = { usuario: nombre, email: $("regEmail").value.trim(), password, rol: "CLIENTE" };
            const telefono = $("regTelefono").value.trim();
            if (telefono) cuenta.telefono = telefono;

            await post("/usuarios", cuenta);
            const datos = await post("/auth/login", { usuario: nombre, password });
            ev.target.reset();
            cerrar(modalAuth);
            await iniciarSesion(datos);
            avisar("Cuenta creada correctamente", "ok");
        } catch (e) {
            $("errorAuth").textContent = e.message;
        }
    });

    /* ---------------- Detalle de carta: zona de tienda ---------------- */
    function campo(etiqueta, control) {
        return el("label", {}, etiqueta, control);
    }

    function opciones(valores, etiquetas = {}) {
        return valores.map((v) => el("option", { value: v, text: etiquetas[v] || v.toUpperCase() }));
    }

    function renderZonaTienda() {
        const zona = $("zonaTienda");
        zona.replaceChildren();
        const carta = cartaAbierta;
        if (!carta) return;

        zona.append(el("h3", { text: "En la tienda" }));

        if (usuario) {
            const esFav = favoritos.has(carta.id);
            zona.append(el("button", {
                type: "button",
                class: "boton-favorito" + (esFav ? " activo" : ""),
                text: esFav ? "♥ En favoritos" : "♡ Añadir a favoritos",
                onclick: () => alternarFavorito(carta.id)
            }));
        } else {
            zona.append(el("p", { class: "nota", text: "Inicia sesión para comprar y guardar favoritos." }));
        }

        const ofertas = productosPorCarta.get(carta.id) || [];

        if (ofertas.length === 0) {
            zona.append(el("p", { class: "nota", text: "Esta carta no está disponible en la tienda por ahora." }));
        }

        ofertas.forEach((p) => {
            zona.append(el("div", { class: "oferta" },
                el("div", { class: "detalle", text: `${ETIQ_ESTADO[p.estado_carta] || p.estado_carta} · ${ETIQ_ACABADO[p.acabado] || p.acabado} · ${String(p.idioma).toUpperCase()}` }),
                el("div", {},
                    el("span", { class: "precio", text: euros.format(Number(p.precio)) }),
                    el("div", { class: "stock", text: `Stock: ${p.stock}` })
                ),
                el("button", { type: "button", text: "Añadir", onclick: () => anadirAlCarrito(p.id) })
            ));
        });

        if (usuario && usuario.rol === "ADMINISTRADOR") {
            zona.append(formularioAlta(carta));
        }
    }

    function formularioAlta(carta) {
        const formulario = el("form", { class: "formulario" },
            el("div", { class: "fila" },
                campo("Idioma", el("select", { name: "idioma" }, opciones(IDIOMAS))),
                campo("Acabado", el("select", { name: "acabado" }, opciones(Object.keys(ETIQ_ACABADO), ETIQ_ACABADO)))
            ),
            campo("Estado", el("select", { name: "estado" }, opciones(Object.keys(ETIQ_ESTADO), ETIQ_ESTADO))),
            el("div", { class: "fila" },
                campo("Precio (€)", el("input", { name: "precio", type: "number", min: "0", step: "0.01", required: true })),
                campo("Stock", el("input", { name: "stock", type: "number", min: "0", step: "1", required: true }))
            ),
            el("button", { type: "submit", text: "Poner a la venta" })
        );

        formulario.addEventListener("submit", async (ev) => {
            ev.preventDefault();
            const f = ev.target.elements;
            try {
                await post("/productos", {
                    cartaExternaId: carta.id,
                    idioma: f.idioma.value,
                    estadoCarta: f.estado.value,
                    acabado: f.acabado.value,
                    precio: Number(f.precio.value),
                    stock: parseInt(f.stock.value, 10),
                    activo: true
                });
                await cargarProductos();
                renderZonaTienda();
                decorarCartas();
                avisar("Producto puesto a la venta", "ok");
            } catch (e) {
                avisar(e.message, "error");
            }
        });

        return el("details", {}, el("summary", { text: "Poner a la venta (admin)" }), formulario);
    }

    /* ---------------- Favoritos ---------------- */
    async function alternarFavorito(cartaId) {
        try {
            const lista = favoritos.has(cartaId)
                ? await api(`/favoritos/${usuario.id}?cartaExternaId=${encodeURIComponent(cartaId)}`, { method: "DELETE" })
                : await post(`/favoritos/${usuario.id}`, { cartaExternaId: cartaId });
            favoritos = new Set(lista);
            renderZonaTienda();
        } catch (e) {
            avisar(e.message, "error");
        }
    }

    /* Muestra las cartas favoritas reutilizando la rejilla del buscador (máx. 20) */
    async function verFavoritos() {
        try {
            const ids = (await api(`/favoritos/${usuario.id}`)).slice(0, 20);
            favoritos = new Set(ids);
            if (ids.length === 0) { avisar("Aún no tienes cartas favoritas"); return; }

            const consulta = ids.map((id) => `id:"${id}"`).join(" OR ");
            const respuesta = await obtenerConReintentos(
                `${API_CARTAS}?q=${encodeURIComponent(consulta)}&pageSize=20`, 3);
            const datos = await respuesta.json();

            cartasActuales = datos.data || [];
            mostrarCartas();
            estado.textContent = `${cartasActuales.length} cartas favoritas`;
            paginacion.style.display = "none";
            window.scrollTo({ top: 0, behavior: "smooth" });
        } catch (e) {
            avisar(e.message === "SIN_INTERNET" ? "No tienes conexión a Internet" : "No se pudieron cargar los favoritos", "error");
        }
    }

    /* ---------------- Carrito ---------------- */
    async function anadirAlCarrito(productoId) {
        if (!usuario) {
            avisar("Inicia sesión para añadir al carrito");
            abrirAuth("login");
            return;
        }
        try {
            carrito = await post(`/carrito/${usuario.id}/items`, { productoId, cantidad: 1 });
            renderBarra();
            avisar("Añadido al carrito", "ok");
        } catch (e) {
            avisar(e.message, "error");
        }
    }

    async function cambiarCantidad(item, nueva) {
        try {
            carrito = nueva < 1
                ? await api(`/carrito/${usuario.id}/items/${item.producto_id}`, { method: "DELETE" })
                : await put(`/carrito/${usuario.id}/items/${item.producto_id}`, { cantidad: nueva });
            renderBarra();
            renderCarrito();
        } catch (e) {
            avisar(e.message, "error");
        }
    }

    async function abrirCarrito() {
        if (!usuario) {
            avisar("Inicia sesión para ver tu carrito");
            abrirAuth("login");
            return;
        }
        $("mensajeCarrito").textContent = "";
        abrir(modalCarrito);
        try {
            carrito = await api(`/carrito/${usuario.id}`);
            renderBarra();
            renderCarrito();
            await Promise.all(carrito.items.map((i) => obtenerInfoCarta(i.carta_externa_id)));
            renderCarrito();
        } catch (e) {
            $("mensajeCarrito").textContent = e.message;
        }
    }

    function renderCarrito() {
        const lista = $("listaCarrito");
        const resumen = $("resumenCarrito");
        const formulario = $("formPedido");
        lista.replaceChildren();
        resumen.replaceChildren();

        if (!carrito || carrito.items.length === 0) {
            lista.append(el("p", { class: "nota", text: "Tu carrito está vacío." }));
            formulario.hidden = true;
            return;
        }

        carrito.items.forEach((item) => {
            const info = infoCartas.get(item.carta_externa_id);
            lista.append(el("div", { class: "linea-carrito" },
                el("img", { src: info?.image || "", alt: info?.name || item.carta_externa_id, loading: "lazy" }),
                el("div", {},
                    el("div", { class: "nombre", text: info?.name || item.carta_externa_id }),
                    el("div", { class: "meta", text: [info?.set, `${ETIQ_ESTADO[item.estado_carta] || item.estado_carta} · ${ETIQ_ACABADO[item.acabado] || item.acabado} · ${String(item.idioma).toUpperCase()}`].filter(Boolean).join(" — ") }),
                    el("div", { class: "meta", text: `${euros.format(Number(item.precio))} / ud.` })
                ),
                el("div", {},
                    el("div", { class: "controles" },
                        el("button", { type: "button", text: "−", "aria-label": "Quitar una unidad", onclick: () => cambiarCantidad(item, item.cantidad - 1) }),
                        el("span", { text: String(item.cantidad) }),
                        el("button", { type: "button", text: "+", "aria-label": "Añadir una unidad", onclick: () => cambiarCantidad(item, item.cantidad + 1) }),
                        el("button", { type: "button", class: "boton-secundario", text: "Quitar", onclick: () => cambiarCantidad(item, 0) })
                    ),
                    el("div", { class: "importe", text: euros.format(Number(item.subtotal)) })
                )
            ));
        });

        const envio = Number(carrito.gastosEnvio);
        resumen.append(
            el("div", { text: `Subtotal: ${euros.format(Number(carrito.subtotal))}` }),
            el("div", { text: `Envío: ${envio === 0 ? "Gratis" : euros.format(envio)}` }),
            el("div", { class: "total", text: `Total: ${euros.format(Number(carrito.total))}` })
        );
        formulario.hidden = false;
    }

    $("botonCarrito").addEventListener("click", abrirCarrito);

    $("formPedido").addEventListener("submit", async (ev) => {
        ev.preventDefault();
        const f = ev.target.elements;
        const mensaje = $("mensajeCarrito");
        mensaje.className = "mensaje-error";
        mensaje.textContent = "";

        // Nombre, set y número de cada carta: el backend los guarda como copia histórica
        const cartas = {};
        carrito.items.forEach((i) => {
            const info = infoCartas.get(i.carta_externa_id);
            cartas[i.carta_externa_id] = { nombre: info?.name || null, set: info?.set || null, numero: info?.number || null };
        });

        try {
            const resultado = await post("/pedidos", {
                usuarioId: usuario.id,
                nombreDestinatario: f.nombreDestinatario.value,
                calle: f.calle.value,
                numero: f.numero.value,
                piso: f.piso.value,
                ciudad: f.ciudad.value,
                provincia: f.provincia.value,
                codigoPostal: f.codigoPostal.value,
                pais: f.pais.value,
                metodoPago: f.metodoPago.value,
                cartas
            });

            carrito = await api(`/carrito/${usuario.id}`);
            renderBarra();
            renderCarrito();
            await cargarProductos();
            decorarCartas();
            renderZonaTienda();

            mensaje.className = "mensaje-ok";
            mensaje.textContent = `Pedido #${resultado.pedidoId} realizado. Total: ${euros.format(Number(resultado.total))}. Pago pendiente (ref. ${resultado.referenciaPago}).`;
        } catch (e) {
            mensaje.textContent = e.message;
        }
    });

    /* ---------------- Pedidos ---------------- */
    async function abrirPedidos() {
        const contenedor = $("listaPedidos");
        contenedor.replaceChildren(el("p", { class: "nota", text: "Cargando pedidos..." }));
        abrir(modalPedidos);
        try {
            const pedidos = await api(`/pedidos/usuario/${usuario.id}`);
            contenedor.replaceChildren();
            if (pedidos.length === 0) {
                contenedor.append(el("p", { class: "nota", text: "Todavía no has hecho ningún pedido." }));
                return;
            }
            pedidos.forEach((p) => {
                contenedor.append(el("article", { class: "pedido" },
                    el("header", {},
                        el("span", { text: `Pedido #${p.id} · ${new Date(p.fecha_pedido).toLocaleDateString("es-ES")}` }),
                        el("span", { text: `${p.estado} · pago ${String(p.pago_estado || "—").toLowerCase()} · ${euros.format(Number(p.total))}` })
                    ),
                    el("ul", {}, p.lineas.map((l) => el("li", {
                        text: `${l.cantidad} × ${l.nombre_carta}${l.nombre_set ? " (" + l.nombre_set + ")" : ""} — ${ETIQ_ESTADO[l.estado_carta] || l.estado_carta}, ${ETIQ_ACABADO[l.acabado] || l.acabado}, ${String(l.idioma).toUpperCase()} — ${euros.format(Number(l.subtotal))}`
                    })))
                ));
            });
        } catch (e) {
            contenedor.replaceChildren(el("p", { class: "mensaje-error", text: e.message }));
        }
    }

    /* ---------------- Enganche con el buscador ---------------- */
    const mostrarCartasOriginal = window.mostrarCartas;
    window.mostrarCartas = function () {
        mostrarCartasOriginal();
        decorarCartas();
    };

    const abrirDetalleOriginal = window.abrirDetalle;
    window.abrirDetalle = function (indice) {
        abrirDetalleOriginal(indice);
        const carta = cartasActuales[indice];
        cartaAbierta = carta;
        renderZonaTienda();
        // Refresca stock/precios por si han cambiado desde que se cargó la página
        cargarProductos().then(() => {
            if (cartaAbierta === carta && modal.classList.contains("visible")) renderZonaTienda();
        });
    };

    /* ---------------- Inicio ---------------- */
    renderBarra();
    cargarProductos().then(decorarCartas);
    if (usuario) cargarDatosUsuario();
})();
