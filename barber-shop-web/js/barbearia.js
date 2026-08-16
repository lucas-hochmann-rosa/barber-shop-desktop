/* Abas (dados, serviços, barbeiros), cartões e modal (RF03, RF04) */

const TelaBarbearia = {

    /** 'servico' ou 'barbeiro'; e o item sendo editado (null = novo). */
    tipoEmEdicao: null,
    itemEmEdicao: null,

    /* Abas */

    trocarAba(nome) {
        const paineis = {
            dados: 'painelDados',
            servicos: 'painelServicos',
            barbeiros: 'painelBarbeiros'
        };

        document.querySelectorAll('.aba').forEach((aba) => {
            const ativa = aba.dataset.aba === nome;
            aba.setAttribute('aria-selected', String(ativa));
        });

        Object.entries(paineis).forEach(([chave, id]) => {
            document.getElementById(id).hidden = chave !== nome;
        });
    },

    /* Aba Dados */

    carregarDados() {
        const b = Dados.barbearia;
        document.getElementById('barbeariaNome').value = b.nome;
        document.getElementById('barbeariaCep').value = b.cep;
        document.getElementById('barbeariaCultura').value = b.culturaValores;
        document.getElementById('barbeariaAbertura').value = b.horarioAbertura;
        document.getElementById('barbeariaFechamento').value = b.horarioFechamento;
    },

    salvarDados(evento) {
        evento.preventDefault();
        const self = TelaBarbearia;
        const formulario = document.getElementById('formBarbearia');
        Validacao.limparTodos(formulario);

        const nome = document.getElementById('barbeariaNome');
        const abertura = document.getElementById('barbeariaAbertura');
        const fechamento = document.getElementById('barbeariaFechamento');

        let valido = Validacao.obrigatorio(nome, 'Informe o nome da barbearia.');

        // Mesma regra do desktop: se os dois horários vierem, abertura < fechamento
        if (abertura.value && fechamento.value && abertura.value >= fechamento.value) {
            Validacao.marcarErro(abertura, 'A abertura deve ser antes do fechamento.');
            valido = false;
        }
        if (!valido) return;

        Object.assign(Dados.barbearia, {
            nome: nome.value.trim(),
            cep: document.getElementById('barbeariaCep').value.trim(),
            culturaValores: document.getElementById('barbeariaCultura').value.trim(),
            horarioAbertura: abertura.value,
            horarioFechamento: fechamento.value
        });

        document.querySelectorAll('.barra-lateral__barbearia')
            .forEach((e) => { e.textContent = Dados.barbearia.nome; });

        self.avisar('Dados da barbearia atualizados.');
    },

    /* Cartões */

    desenharServicos() {
        const grade = document.getElementById('gradeServicos');
        grade.innerHTML = Dados.servicos.map((s) => `
            <li class="cartao-item">
                <img class="cartao-item__foto" src="${s.imagem}" alt="" width="320" height="180">
                <div class="cartao-item__corpo">
                    <h3 class="cartao-item__nome">${s.nome}</h3>
                    <p class="cartao-item__detalhe">
                        <span class="valor">${Formato.moeda(s.preco)}</span>
                        <span class="texto-secundario texto-pequeno"> · ${s.duracaoMinutos} min</span>
                    </p>
                </div>
                <div class="cartao-item__acoes">
                    <button class="botao botao--secundario" type="button" data-editar="servico" data-id="${s.id}">Editar</button>
                    <button class="botao botao--perigo" type="button" data-excluir="servico" data-id="${s.id}">Excluir</button>
                </div>
            </li>`).join('');
    },

    desenharBarbeiros() {
        const grade = document.getElementById('gradeBarbeiros');
        grade.innerHTML = Dados.barbeiros.map((b) => `
            <li class="cartao-item">
                <div class="cartao-item__avatar">
                    <img src="${b.imagem}" alt="" width="96" height="96">
                </div>
                <div class="cartao-item__corpo">
                    <h3 class="cartao-item__nome">${b.nome}</h3>
                    <p class="cartao-item__detalhe texto-secundario texto-pequeno">
                        ${TelaBarbearia.contarAtendimentos(b.id)} atendimentos concluídos
                    </p>
                </div>
                <div class="cartao-item__acoes">
                    <button class="botao botao--secundario" type="button" data-editar="barbeiro" data-id="${b.id}">Editar</button>
                    <button class="botao botao--perigo" type="button" data-excluir="barbeiro" data-id="${b.id}">Excluir</button>
                </div>
            </li>`).join('');
    },

    contarAtendimentos(barbeiroId) {
        return Dados.agendamentos.filter(
            (a) => a.barbeiroId === barbeiroId && a.status === StatusAgendamento.CONCLUIDO
        ).length;
    },

    /* Modal */

    abrirModal(tipo, item) {
        this.tipoEmEdicao = tipo;
        this.itemEmEdicao = item || null;

        const ehServico = tipo === 'servico';
        const titulo = (item ? 'Editar ' : 'Novo ') + (ehServico ? 'serviço' : 'barbeiro');
        document.getElementById('modalTitulo').textContent = titulo;

        // Preço e duração só existem para serviço
        document.getElementById('camposServico').hidden = !ehServico;

        document.getElementById('itemNome').value = item ? item.nome : '';
        document.getElementById('itemPreco').value = item && ehServico ? item.preco : '';
        document.getElementById('itemDuracao').value = item && ehServico ? item.duracaoMinutos : 30;

        Validacao.limparTodos(document.getElementById('formModal'));
        Modal.abrir('#modalCadastro');
    },

    salvarModal(evento) {
        evento.preventDefault();
        const self = TelaBarbearia;
        const formulario = document.getElementById('formModal');
        Validacao.limparTodos(formulario);

        const nome = document.getElementById('itemNome');
        const ehServico = self.tipoEmEdicao === 'servico';

        let valido = Validacao.obrigatorio(nome, 'Informe o nome.');

        // Nome duplicado, mesma regra do CatalogoService do desktop
        const lista = ehServico ? Dados.servicos : Dados.barbeiros;
        const repetido = lista.some((i) =>
            i.nome.toLowerCase() === nome.value.trim().toLowerCase()
            && (!self.itemEmEdicao || i.id !== self.itemEmEdicao.id));
        if (valido && repetido) {
            Validacao.marcarErro(nome, `Já existe um ${ehServico ? 'serviço' : 'barbeiro'} com esse nome.`);
            valido = false;
        }

        let preco = 0;
        let duracao = 30;
        if (ehServico) {
            const campoPreco = document.getElementById('itemPreco');
            const campoDuracao = document.getElementById('itemDuracao');

            preco = Number(campoPreco.value);
            duracao = Number(campoDuracao.value);

            if (!campoPreco.value.trim() || isNaN(preco) || preco <= 0) {
                Validacao.marcarErro(campoPreco, 'Informe um preço maior que zero.');
                valido = false;
            }
            if (isNaN(duracao) || duracao < 5 || duracao > 480) {
                Validacao.marcarErro(campoDuracao, 'A duração deve ficar entre 5 e 480 minutos.');
                valido = false;
            }
        }

        if (!valido) return;

        if (self.itemEmEdicao) {
            self.itemEmEdicao.nome = nome.value.trim();
            if (ehServico) {
                self.itemEmEdicao.preco = preco;
                self.itemEmEdicao.duracaoMinutos = duracao;
            }
            self.avisar(`${ehServico ? 'Serviço' : 'Barbeiro'} atualizado.`);
        } else {
            const novoId = lista.reduce((maior, i) => Math.max(maior, i.id), 0) + 1;
            if (ehServico) {
                Dados.servicos.push({
                    id: novoId, nome: nome.value.trim(), preco: preco,
                    duracaoMinutos: duracao, imagem: 'img/servico-corte.svg'
                });
            } else {
                // Reaproveita um dos avatares existentes
                const avatar = 'img/avatar-' + ((novoId % 4) + 1) + '.svg';
                Dados.barbeiros.push({ id: novoId, nome: nome.value.trim(), imagem: avatar });
            }
            self.avisar(`${ehServico ? 'Serviço' : 'Barbeiro'} cadastrado.`);
        }

        Modal.fechar();
        self.desenharServicos();
        self.desenharBarbeiros();
    },

    /* Exclusão */

    excluir(tipo, id) {
        const ehServico = tipo === 'servico';
        const lista = ehServico ? Dados.servicos : Dados.barbeiros;
        const posicao = lista.findIndex((i) => i.id === Number(id));
        if (posicao < 0) return;

        const item = lista[posicao];

        // Avisa se há agendamentos usando este item (o histórico guarda o
        // nome como snapshot, então não se perde — mesmo desenho do desktop)
        const emUso = Dados.agendamentos.filter(
            (a) => (ehServico ? a.servicoId : a.barbeiroId) === item.id
        ).length;

        lista.splice(posicao, 1);
        this.desenharServicos();
        this.desenharBarbeiros();

        this.avisar(
            `${item.nome} foi excluído.`
            + (emUso ? ` ${emUso} agendamento(s) mantêm o nome registrado no histórico.` : ''),
            emUso ? 'alerta' : 'sucesso'
        );
    },

    /* Mensagens */

    avisar(mensagem, tipo) {
        const painel = document.getElementById('avisoBarbearia');
        painel.hidden = false;
        painel.className = 'aviso aviso--' + (tipo || 'sucesso');
        painel.textContent = mensagem;

        clearTimeout(this._temporizador);
        this._temporizador = setTimeout(() => { painel.hidden = true; }, 5000);
    },

    /* Partida */

    iniciar() {
        if (!document.getElementById('painelServicos')) return;

        this.carregarDados();
        this.desenharServicos();
        this.desenharBarbeiros();
        this.trocarAba('servicos');

        document.querySelectorAll('.aba').forEach((aba) => {
            aba.addEventListener('click', () => this.trocarAba(aba.dataset.aba));
        });

        document.getElementById('formBarbearia').addEventListener('submit', this.salvarDados);
        document.getElementById('formModal').addEventListener('submit', this.salvarModal);

        document.addEventListener('click', (evento) => {
            const novo = evento.target.closest('[data-novo]');
            if (novo) return this.abrirModal(novo.dataset.novo, null);

            const editar = evento.target.closest('[data-editar]');
            if (editar) {
                const tipo = editar.dataset.editar;
                const item = tipo === 'servico'
                    ? Dados.servicoPorId(editar.dataset.id)
                    : Dados.barbeiroPorId(editar.dataset.id);
                return this.abrirModal(tipo, item);
            }

            const excluir = evento.target.closest('[data-excluir]');
            if (excluir) return this.excluir(excluir.dataset.excluir, excluir.dataset.id);
        });
    }
};

document.addEventListener('DOMContentLoaded', () => TelaBarbearia.iniciar());
