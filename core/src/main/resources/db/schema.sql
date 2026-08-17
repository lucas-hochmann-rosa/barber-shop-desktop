CREATE DATABASE IF NOT EXISTS barbershop;
USE barbershop;

CREATE TABLE IF NOT EXISTS barbearias (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(120) NOT NULL,
  cep VARCHAR(12) NOT NULL,
  data_fundacao DATE,
  cultura_valores TEXT,
  horario_abertura TIME NULL,
  horario_fechamento TIME NULL
);

CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  barbearia_id INT NOT NULL,
  login VARCHAR(60) NOT NULL UNIQUE,
  senha_hash VARCHAR(255) NOT NULL,
  salt VARCHAR(64) NULL,
  FOREIGN KEY (barbearia_id) REFERENCES barbearias(id)
);

CREATE TABLE IF NOT EXISTS servicos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  barbearia_id INT NOT NULL,
  nome VARCHAR(120) NOT NULL,
  preco DECIMAL(10,2) NOT NULL,
  imagem_base64 LONGTEXT NULL,
  duracao_minutos INT NOT NULL DEFAULT 30,
  FOREIGN KEY (barbearia_id) REFERENCES barbearias(id)
);

CREATE TABLE IF NOT EXISTS barbeiros (
  id INT AUTO_INCREMENT PRIMARY KEY,
  barbearia_id INT NOT NULL,
  nome VARCHAR(120) NOT NULL,
  imagem_base64 LONGTEXT NULL,
  FOREIGN KEY (barbearia_id) REFERENCES barbearias(id)
);

-- Diretório de clientes, populado automaticamente a partir dos agendamentos
-- (não há tela dedicada de cadastro). agendamentos continua guardando
-- cliente_nome/contato como texto livre - esta tabela é só um índice de
-- consulta/busca por cima disso, não uma FK obrigatória.
CREATE TABLE IF NOT EXISTS clientes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  barbearia_id INT NOT NULL,
  nome VARCHAR(120) NOT NULL,
  contato VARCHAR(120) NOT NULL,
  criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (barbearia_id) REFERENCES barbearias(id),
  UNIQUE KEY ux_cliente_contato (barbearia_id, contato)
);

CREATE TABLE IF NOT EXISTS agendamentos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  barbearia_id INT NOT NULL,
  servico_id INT NULL,
  barbeiro_id INT NULL,
  servico_nome_snapshot VARCHAR(120) NULL,
  barbeiro_nome_snapshot VARCHAR(120) NULL,
  duracao_minutos_snapshot INT NULL,
  cliente_nome VARCHAR(120) NOT NULL,
  contato VARCHAR(120) NOT NULL,
  data_hora DATETIME NOT NULL,
  origem_contato VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL,
  motivo_cancelamento VARCHAR(255) NULL,
  FOREIGN KEY (barbearia_id) REFERENCES barbearias(id),

  INDEX idx_agend_servico (servico_id),
  INDEX idx_agend_barbeiro (barbeiro_id)
);

-- Trava, em nível de banco, o caso exato de dois agendamentos no mesmíssimo instante
-- para o mesmo barbeiro. É uma segunda camada de defesa, não a regra de negócio
-- completa: a checagem de sobreposição real (considerando a duração de cada
-- serviço) fica em AgendamentoDAO.verificarConflito, pois um índice único não
-- expressa "intervalo de tempo". Sem DROP antes: este script só roda numa
-- instalação nova, com agendamentos recém-criada - o índice não existe ainda
-- (e "DROP INDEX ... IF EXISTS" nem é sintaxe válida no MySQL).
CREATE UNIQUE INDEX ux_barbeiro_horario ON agendamentos (barbeiro_id, data_hora);
