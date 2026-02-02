-- Inserir novos usuários
insert into usuario (id, nome, email, senha, data_cadastro) values
(9, 'Admin User', 'admin@algafood.com.br', '$2y$12$NSsM4gEOR7MKogflKR7GMeYugkttjNhAJMvFdHrBLaLp2HzlggP5W', utc_timestamp),
(10, 'Yuri User', 'yuri@algafood.com.br', '$2y$12$NSsM4gEOR7MKogflKR7GMeYugkttjNhAJMvFdHrBLaLp2HzlggP5W', utc_timestamp),
(11, 'Cliente User', 'cliente@algafood.com.br', '$2y$12$NSsM4gEOR7MKogflKR7GMeYugkttjNhAJMvFdHrBLaLp2HzlggP5W', utc_timestamp);

-- Associar usuários a grupos (opcional)
-- Grupos existentes: 1: Gerente, 2: Vendedor, 3: Secretária, 4: Cadastrador

-- Associa 'Admin User' ao grupo 'Gerente'
insert into usuario_grupo (usuario_id, grupo_id) values (9, 1);

-- Associa 'Yuri User' aos grupos 'Vendedor' e 'Cadastrador'
insert into usuario_grupo (usuario_id, grupo_id) values (10, 2), (10, 4);
