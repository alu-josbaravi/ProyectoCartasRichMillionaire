create table if not exists Usuario
(
    id number primary key,
    usuario varchar(16) unique,
    email varchar(255) unique,
    telefono varchar(20) unique,
    password varchar(255),
    fecha_registro date default (curdate()),
    rol varchar(20) check (trim(upper(rol)) in ('ADMINISTRADOR', 'VERIFICADO', 'VISITANTE'))
);

create table if not exists Anuncio
(
    id number primary key,
    vendedor_id number,
    nombre_carta varchar(255),
    numero_carta varchar(20),
    coleccion varchar(255),
    rareza varchar(100),
    precio decimal(10,2),
    estado varchar(50),
    descripcion text,
    fecha_publicacion date default (curdate()),

    foreign key (vendedor_id) references Usuario(id)
);

create table if not exists Pedido
(
    id number primary key,
    comprador_id number,
    fecha_pedido date default (curdate()),
    estado varchar(20),
    total decimal(10,2),

    foreign key (comprador_id) references Usuario(id)
);

create table if not exists Mensaje
(
    id number primary key,
    emisor_id number,
    receptor_id number,
    contenido text,
    fecha_envio date default (curdate()),

    foreign key (emisor_id) references Usuario(id),
    foreign key (receptor_id) references Usuario(id)
);

create table if not exists Valoracion
(
    id number primary key,
    autor_id number,
    destinatario_id number,
    puntuacion number,
    comentario text,
    fecha_valoracion date default (curdate()),

    foreign key (autor_id) references Usuario(id),
    foreign key (destinatario_id) references Usuario(id)
);

create table if not exists Cashback
(
    id number primary key,
    usuario_id number,
    pedido_id number,
    importe decimal(10,2),
    tipo varchar(20),

    foreign key (usuario_id) references Usuario(id),
    foreign key (pedido_id) references Pedido(id)
);

create table if not exists VerificacionUsuario
(
    id number primary key,
    usuario_id number,
    nivel number,
    estado varchar(20),

    foreign key (usuario_id) references Usuario(id)
);

create table if not exists Incidencia
(
    id number primary key,
    pedido_id number,
    usuario_id number,
    descripcion text,
    estado varchar(20),
    fecha_incidencia date default (curdate()),

    foreign key (pedido_id) references Pedido(id),
    foreign key (usuario_id) references Usuario(id)
);

create table if not exists Favorito
(
    usuario_id number,
    anuncio_id number,

    primary key(usuario_id, anuncio_id),

    foreign key (usuario_id) references Usuario(id),
    foreign key (anuncio_id) references Anuncio(id)
);