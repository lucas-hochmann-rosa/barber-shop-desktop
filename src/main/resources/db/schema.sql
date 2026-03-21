CREATE DATABASE IF NOT EXISTS barberdesk;
USE barberdesk;

CREATE TABLE IF NOT EXISTS barbearias (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(120) NOT NULL,
  cep VARCHAR(12) NOT NULL,
  data_fundacao DATE,
  cultura_valores TEXT
);

CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  barbearia_id INT NOT NULL,
  login VARCHAR(60) NOT NULL UNIQUE,
  senha_hash VARCHAR(255) NOT NULL,
  FOREIGN KEY (barbearia_id) REFERENCES barbearias(id)
);

CREATE TABLE IF NOT EXISTS servicos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  barbearia_id INT NOT NULL,
  nome VARCHAR(120) NOT NULL,
  preco DECIMAL(10,2) NOT NULL,
  imagem_path VARCHAR(255),
  duracao_minutos INT NOT NULL DEFAULT 30,
  FOREIGN KEY (barbearia_id) REFERENCES barbearias(id)
);

CREATE TABLE IF NOT EXISTS barbeiros (
  id INT AUTO_INCREMENT PRIMARY KEY,
  barbearia_id INT NOT NULL,
  nome VARCHAR(120) NOT NULL,
  imagem_path VARCHAR(255),
  FOREIGN KEY (barbearia_id) REFERENCES barbearias(id)
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
  FOREIGN KEY (barbearia_id) REFERENCES barbearias(id),

  INDEX idx_agend_servico (servico_id),
  INDEX idx_agend_barbeiro (barbeiro_id)
);

-- Trava, em nível de banco, o caso exato de dois agendamentos no mesmíssimo instante
-- para o mesmo barbeiro. É uma segunda camada de defesa, não a regra de negócio
-- completa: a checagem de sobreposição real (considerando a duração de cada
-- serviço) fica em AgendamentoDAO.verificarConflito, pois um índice único não
-- expressa "intervalo de tempo".
DROP INDEX IF EXISTS ux_barbeiro_horario ON agendamentos;
CREATE UNIQUE INDEX ux_barbeiro_horario ON agendamentos (barbeiro_id, data_hora);
